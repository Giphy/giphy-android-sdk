package com.giphy.sdk.uidemo

import android.net.Uri
import android.view.SurfaceView
import com.giphy.sdk.tracking.KEY_VIDEO_LENGTH
import com.giphy.sdk.ui.utils.GPHAbstractVideoPlayer
import com.giphy.sdk.ui.utils.GPHVideoPlayerState
import com.giphy.sdk.ui.utils.videoUrl
import com.giphy.sdk.ui.views.GPHVideoPlayerView
import com.giphy.sdk.uidemo.VideoPlayer.VideoCache
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.text.CueGroup
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import timber.log.Timber
import java.io.IOException

class VideoPlayerExoPlayer2181Impl(
    playerView: GPHVideoPlayerView?,
    repeatable: Boolean = false,
    showCaptions: Boolean = true
) : GPHAbstractVideoPlayer(playerView, repeatable, showCaptions), Player.Listener {

    // region Exoplayer stuff
    private var player: ExoPlayer? = null

    override val duration: Long
        get() {
            return player?.duration ?: 0
        }
    override val currentPosition: Long
        get() {
            return player?.currentPosition ?: 0
        }
    override val isPlaying: Boolean
        get() {
            return player?.isPlaying ?: false
        }

    override fun getVolume(): Float {
        return player?.audioComponent?.volume ?: 0f
    }

    override fun setVolume(audioVolume: Float) {
        val volume = if (isDeviceMuted) 0f else audioVolume
        player?.audioComponent?.volume = volume
        listeners.forEach {
            it(GPHVideoPlayerState.MuteChanged(volume > 0))
        }
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {
        player?.setVideoSurfaceView(surfaceView)
    }

    override fun seekTo(position: Long) {
        player?.seekTo(position)
    }

    override fun play() {
        player?.play()
    }

    override fun setupExoPlayer(playerView: GPHVideoPlayerView, autoPlay: Boolean) {

        val videoUrl = media.videoUrl

        if (videoUrl == null) {
            onPlayerError(ExoPlaybackException.createForSource(IOException("Video url is null"), -1))
        }

        val loadControl = DefaultLoadControl.Builder().setPrioritizeTimeOverSizeThresholds(true)
            .setBufferDurationsMs(
                500,
                5000,
                500,
                500
            ).build()

        val trackSelector = DefaultTrackSelector(playerView.context)
        // It gets embedded captions from the video source
        trackSelector.setParameters(trackSelector.buildUponParameters().setPreferredTextLanguage("en"))

        player = ExoPlayer
            .Builder(playerView.context)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()
            .apply {
                addListener(this@VideoPlayerExoPlayer2181Impl)
                playWhenReady = autoPlay
            }

        playerView.preloadFirstFrame(media)
        playerView.prepare(media, this@VideoPlayerExoPlayer2181Impl)

        player?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT

        updateRepeatMode()
        startProgressTimer()
        // This is the MediaSource representing the media to be played.
        val extractoryFactory =
            DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true)
        val uri = Uri.parse(videoUrl)

        val mediaItemBuilder = MediaItem.Builder()
            .setUri(uri)
            .setCustomCacheKey(uri.buildUpon().clearQuery().build().toString())

        // Sideloading captions. Keep it here just in case.
        /*media.video?.captions?.videoCaption?.vtt?.let {
            val uriSubtitle = Uri.parse(it)

            val mediaItemSubtitle = MediaItem.Subtitle(uriSubtitle,
                MimeTypes.TEXT_VTT,
                "en",
                C.SELECTION_FLAG_DEFAULT)

            mediaItemBuilder.setSubtitles(mutableListOf(mediaItemSubtitle))
        }*/

        val mediaItem = mediaItemBuilder
            .build()
        val mediaSource = DefaultMediaSourceFactory(VideoCache.cacheDataSourceFactory, extractoryFactory).createMediaSource(mediaItem)

        // Prepare the player with the source.
        player?.setMediaSource(mediaSource)
        player?.prepare()
        stopListeningToDeviceVolume()
        startListeningToDeviceVolume()

    }

    override fun destroyPlayer() {
        player?.release()
        player = null
    }

    override fun updateRepeatMode() {
        player?.repeatMode =
            if (repeatable) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
    }

    // endregion

    // region Exoplayer listener

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT) {
            listeners.forEach {
                it(GPHVideoPlayerState.Repeated)
            }
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        val stateStr: String
        val videoPlaybackState: GPHVideoPlayerState
        when (state) {
            Player.STATE_READY -> {
                stateStr = "STATE_READY"
                videoPlaybackState = GPHVideoPlayerState.Ready
            }
            Player.STATE_BUFFERING -> {
                stateStr = "STATE_BUFFERING"
                videoPlaybackState = GPHVideoPlayerState.Buffering
            }
            Player.STATE_ENDED -> {
                stateStr = "STATE_ENDED"
                videoPlaybackState = GPHVideoPlayerState.Ended
            }
            Player.STATE_IDLE -> {
                stateStr = "STATE_IDLE"
                videoPlaybackState = GPHVideoPlayerState.Idle
            }
            else -> {
                stateStr = "STATE_UNKNOWN"
                videoPlaybackState = GPHVideoPlayerState.Unknown
            }
        }
        Timber.d("onPlayerStateChanged $stateStr")
        if (state == Player.STATE_ENDED) {
            // make sure we send a final progress
            player?.duration?.let {
                updateProgress(it)
            }
        }
        listeners.forEach {
            it(videoPlaybackState)
        }
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)
        Timber.d("onLoadingChanged $isLoading")
        if (isLoading) {
            if (lastProgress > 0) {
                Timber.d("restore seek $lastProgress")
                player?.seekTo(lastProgress)
                lastProgress = 0L
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Timber.d("onIsPlayingChanged ${media.id} $isPlaying")
        if (isPlaying) {
            listeners.forEach {
                it(GPHVideoPlayerState.Playing)
            }
            playerView?.keepScreenOn = true
        } else {
            player?.playbackState?.let {
                if (it != Player.STATE_ENDED) {
                    onPlaybackStateChanged(it)
                }
            }
            playerView?.keepScreenOn = false
        }
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        player?.duration?.let { duration ->
            listeners.forEach {
                it(GPHVideoPlayerState.TimelineChanged(duration))
            }
            if (duration > 0) {
                if (media.userDictionary == null)
                    media.userDictionary = HashMap()
                media.userDictionary?.put(KEY_VIDEO_LENGTH, duration.toString())
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        listeners.forEach {
            it(GPHVideoPlayerState.Error(error.localizedMessage ?: "Error occurred"))
        }
    }

    override fun onCues(cueGroup: CueGroup) {
        listeners.forEach {

            it(GPHVideoPlayerState.CaptionsTextChanged(if (cueGroup.cues.size > 0) cueGroup.cues[0].text.toString() else ""))
        }
    }

    // endregion

}