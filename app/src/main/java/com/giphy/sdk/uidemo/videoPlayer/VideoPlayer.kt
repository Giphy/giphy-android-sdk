package com.giphy.sdk.uidemo.videoPlayer

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.SurfaceView
import android.view.View
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT
import androidx.media3.common.Timeline
import androidx.media3.common.text.CueGroup
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.extractor.DefaultExtractorsFactory
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

sealed class VideoPlayerState {
    object Idle : VideoPlayerState()
    object Ready : VideoPlayerState()
    object Buffering : VideoPlayerState()
    object Ended : VideoPlayerState()
    object Unknown : VideoPlayerState()
    object Repeated : VideoPlayerState()
    object Playing : VideoPlayerState()
    data class Error(val details: String) : VideoPlayerState()
    data class TimelineChanged(val duration: Long) : VideoPlayerState()
    data class MuteChanged(val muted: Boolean) : VideoPlayerState()
    data class MediaChanged(val mediaUrl: String) : VideoPlayerState()
    data class CaptionsTextChanged(val subtitle: String) : VideoPlayerState()
    data class CaptionsVisibilityChanged(val visible: Boolean) : VideoPlayerState()
}

typealias PlayerStateListener = (VideoPlayerState) -> Unit

class VideoPlayer : Player.Listener {
    private var playerView: VideoPlayerView?
    private var repeatable: Boolean
    var showCaptions: Boolean
        set(value) {
            listeners.forEach {
                it(VideoPlayerState.CaptionsVisibilityChanged(value))
            }
            field = value
        }

    constructor(
        playerView: VideoPlayerView?,
        repeatable: Boolean = false,
        showCaptions: Boolean = true
    ) {
        this.playerView = playerView
        this.repeatable = repeatable
        this.showCaptions = showCaptions
    }

    var player: ExoPlayer? = null
    private val listeners = mutableSetOf<(PlayerStateListener)>()

    private var progressTimer: Timer? = null
    private var progressTimerTask: TimerTask? = null
    private var contentObserver: ContentObserver? = null

    var videoUrl: String = ""

    var isDestroyed = false

    private var audioManager: AudioManager? = null

    val duration: Long
        get() {
            return player?.duration ?: 0
        }

    val currentPosition: Long
        get() {
            return player?.currentPosition ?: 0
        }

    val isPlaying: Boolean
        get() {
            return player?.isPlaying ?: false
        }

    init {
        startListeningToDeviceVolume()
    }

    private var isDeviceMuted: Boolean = false
    private fun startListeningToDeviceVolume() {
        if (playerView == null) {
            return
        }
        fun setVolumeValue() {
            val currentVolume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            val playerVolumeValue = if (currentVolume > 0) 1.0f else 0f
            isDeviceMuted = playerVolumeValue == 0f
            setVolume(playerVolumeValue)
        }
        audioManager = playerView!!.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        setVolumeValue()
        contentObserver = object :
            ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                setVolumeValue()
            }
        }
        playerView!!.context.contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            contentObserver!!
        )
    }

    private fun stopListeningToDeviceVolume() {
        if (playerView == null || contentObserver == null) {
            return
        }
        playerView!!.context.contentResolver.unregisterContentObserver(contentObserver!!)
        contentObserver = null
    }

    private fun startProgressTimer() {
        progressTimerTask?.cancel()
        progressTimer?.cancel()
        progressTimerTask = object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    if (player?.isPlaying == false) return@post
                    val playerPosition = player?.currentPosition ?: return@post
                    updateProgress(playerPosition)
                }
            }
        }
        progressTimer = Timer("VideoProgressTimer")
        progressTimer?.schedule(progressTimerTask, 0, PROGRESS_INTERVAL)
    }

    private fun stopProgressTimer() {
        progressTimer?.cancel()
    }

    /**
     * Gets the audio volume, with 0 being silence and 1 being unity gain.
     *
     * @return audioVolume The audio volume.
     */
    fun getVolume(): Float {
        return player?.volume ?: 0f
    }

    /**
     * Sets the audio volume, with 0 being silence and 1 being unity gain.
     *
     * @param audioVolume The audio volume.
     */
    fun setVolume(audioVolume: Float) {
        val volume = if (isDeviceMuted) 0f else audioVolume
        player?.volume = volume
        listeners.forEach {
            it(VideoPlayerState.MuteChanged(volume > 0))
        }
    }

    fun setVideoSurfaceView(surfaceView: SurfaceView?) {
        player?.setVideoSurfaceView(surfaceView)
    }

    fun addListener(listener: PlayerStateListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: PlayerStateListener) {
        listeners.remove(listener)
    }

    fun seekTo(position: Long) {
        player?.seekTo(position)
    }

    fun play() {
        player?.play()
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    @Throws(Exception::class)
    fun loadMedia(
        videoUrl: String,
        videoAspectRatio: Float? = null,
        autoPlay: Boolean = true,
        view: VideoPlayerView? = null,
        repeatable: Boolean? = null
    ) {
        repeatable?.let {
            this.repeatable = it
        }
        if (isDestroyed) {
            Timber.e("Player is already destroyed!")
            return
        }
        val t0 = SystemClock.elapsedRealtime()
        if (view != null) {
            if (view != playerView) {
                playerView?.onPause()
            }
            playerView = view
        }
        currentAspectRatio = videoAspectRatio
        playerView?.aspectRatio = videoAspectRatio
        this.videoUrl = videoUrl
        listeners.forEach {
            it(VideoPlayerState.MediaChanged(videoUrl))
        }
        releasePlayer()
        if (playerView == null) {
            throw Exception("playerView must not be null")
        }
        paused = false
        playerView?.visibility = View.VISIBLE
        Timber.d("load url $videoUrl")

        val loadControl = DefaultLoadControl.Builder().setPrioritizeTimeOverSizeThresholds(true)
            .setBufferDurationsMs(
                500,
                5000,
                500,
                500
            ).build()
        lastVideoUrl = videoUrl
        lastProgress = 0L

        val trackSelector = DefaultTrackSelector(playerView!!.context)
        trackSelector.setParameters(trackSelector.buildUponParameters().setPreferredTextLanguage("en"))

        player = ExoPlayer
            .Builder(playerView!!.context)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()
            .apply {
                addListener(this@VideoPlayer)
                playWhenReady = autoPlay
            }
        playerView!!.prepare(videoUrl, this@VideoPlayer)
        player?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT

        updateRepeatMode()
        startProgressTimer()
        // This is the MediaSource representing the media to be played.
        val extractorFactory =
            DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true)
        val uri = Uri.parse(videoUrl)
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setCustomCacheKey(uri.buildUpon().clearQuery().build().toString())
            .build()
        val videoSource =
            ProgressiveMediaSource.Factory(VideoCache.cacheDataSourceFactory, extractorFactory)
                .createMediaSource(mediaItem)
        // Prepare the player with the source.
        player?.setMediaSource(videoSource)
        player?.prepare()
        stopListeningToDeviceVolume()
        startListeningToDeviceVolume()

        Timber.d("loadMedia time=${SystemClock.elapsedRealtime() - t0}")
    }

    private fun releasePlayer() {
        stopProgressTimer()
        player?.release()
        player = null
    }

    private fun updateProgress(position: Long) {
        playerView?.onProgress(position)
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        if (reason == MEDIA_ITEM_TRANSITION_REASON_REPEAT) {
            listeners.forEach {
                it(VideoPlayerState.Repeated)
            }
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        val stateStr: String
        val videoPlaybackState: VideoPlayerState
        when (state) {
            Player.STATE_READY -> {
                stateStr = "STATE_READY"
                videoPlaybackState = VideoPlayerState.Ready
            }
            Player.STATE_BUFFERING -> {
                stateStr = "STATE_BUFFERING"
                videoPlaybackState = VideoPlayerState.Buffering
            }
            Player.STATE_ENDED -> {
                stateStr = "STATE_ENDED"
                videoPlaybackState = VideoPlayerState.Ended
            }
            Player.STATE_IDLE -> {
                stateStr = "STATE_IDLE"
                videoPlaybackState = VideoPlayerState.Idle
            }
            else -> {
                stateStr = "STATE_UNKNOWN"
                videoPlaybackState = VideoPlayerState.Unknown
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
        Timber.d("onIsPlayingChanged $videoUrl $isPlaying")
        if (isPlaying) {
            listeners.forEach {
                it(VideoPlayerState.Playing)
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
                it(VideoPlayerState.TimelineChanged(duration))
            }
        }
    }

    private fun onStopPlaying() {
        releasePlayer()
        playerView?.visibility = View.GONE
        playerView = null
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        listeners.forEach {
            it(VideoPlayerState.Error(error.localizedMessage ?: "Error occurred"))
        }
    }

    private var lastProgress = 0L

    private var lastVideoUrl: String? = null
    private var currentAspectRatio: Float? = null
    var paused = false

    fun onPause() {
        paused = true
        player?.pause()
        playerView?.onPause()
        if (videoUrl.isNotEmpty()) {
            lastVideoUrl = videoUrl
        }
        lastProgress = player?.currentPosition ?: 0
        releasePlayer()
    }

    fun onResume() {
        paused = false
        playerView?.onResume()
        lastVideoUrl?.let {
            loadMedia(it, videoAspectRatio = currentAspectRatio)
        }
    }

    fun onDestroy() {
        isDestroyed = true
        stopListeningToDeviceVolume()
        onStopPlaying()
    }

    fun isActive(): Boolean {
        return playerView != null
    }

    private fun updateRepeatMode() {
        player?.repeatMode =
            if (repeatable) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
    }

    override fun onCues(cueGroup: CueGroup) {
        listeners.forEach {
            it(VideoPlayerState.CaptionsTextChanged(if (cueGroup.cues.size > 0) cueGroup.cues[0].text.toString() else ""))
        }
    }
}
