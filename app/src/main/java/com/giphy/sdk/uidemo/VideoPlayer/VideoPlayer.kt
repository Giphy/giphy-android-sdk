package com.giphy.sdk.uidemo.VideoPlayer

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.TextureView
import android.view.View
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import java.io.IOException
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

sealed class VideoPlayerState {
    object Idle : VideoPlayerState()
    object Ready : VideoPlayerState()
    object Buffering : VideoPlayerState()
    object Ended : VideoPlayerState()
    object Unknown : VideoPlayerState()
    object Playing : VideoPlayerState()
    data class Error(val details: String) : VideoPlayerState()
    data class TimelineChanged(val duration: Long) : VideoPlayerState()
    data class MuteChanged(val muted: Boolean) : VideoPlayerState()
    data class MediaChanged(val mediaUrl: String) : VideoPlayerState()
}

typealias PlayerStateListener = (VideoPlayerState) -> Unit

class VideoPlayer(
    private var playerView: VideoPlayerView?,
    private var repeatable: Boolean = false
) : Player.EventListener {

    var player: SimpleExoPlayer? = null
    private val listeners = mutableSetOf<(PlayerStateListener)>()

    private var progressTimer: Timer? = null
    private var progressTimerTask: TimerTask? = null
    private var contentObserver: ContentObserver? = null

    var videoUrl: String = ""

    var isDestroyed = false

    var audioManager: AudioManager? = null

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
        return player?.audioComponent?.volume ?: 0f
    }

    /**
     * Sets the audio volume, with 0 being silence and 1 being unity gain.
     *
     * @param audioVolume The audio volume.
     */
    fun setVolume(audioVolume: Float) {
        val volume = if (isDeviceMuted) 0f else audioVolume
        player?.audioComponent?.volume = volume
        listeners.forEach {
            it(VideoPlayerState.MuteChanged(volume > 0))
        }
    }

    fun setVideoSurfaceView(surfaceView: SurfaceView?) {
        player?.setVideoSurfaceView(surfaceView)
    }

    fun setVideoTextureView(textureView: TextureView?) {
        player?.setVideoTextureView(textureView)
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
            playerView?.visibility = View.GONE
            playerView = view
        }
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

        player = SimpleExoPlayer
            .Builder(playerView!!.context)
            .setTrackSelector(DefaultTrackSelector(playerView!!.context))
            .setLoadControl(loadControl)
            .build()
            .apply {
                addListener(this@VideoPlayer)
                playWhenReady = true
            }
        playerView!!.prepare(videoUrl, this@VideoPlayer)
        player?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT

        if (videoUrl != null) {
            updateRepeatMode()
            startProgressTimer()
            // This is the MediaSource representing the media to be played.
            val extractoryFactory =
                DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true)
            val uri = Uri.parse(videoUrl)
            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setCustomCacheKey(uri.buildUpon().clearQuery().build().toString())
                .build()
            val videoSource =
                ProgressiveMediaSource.Factory(VideoCache.cacheDataSourceFactory, extractoryFactory)
                    .createMediaSource(mediaItem)
            // Prepare the player with the source.
            player?.setMediaSource(videoSource)
            player?.prepare()
            stopListeningToDeviceVolume()
            startListeningToDeviceVolume()
        } else {
            onPlayerError(ExoPlaybackException.createForSource(IOException("Video url is null")))
        }
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

    override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {
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

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        listeners.forEach {
            it(VideoPlayerState.Error(error.localizedMessage ?: "Error occurred"))
        }
    }

    // we remember the state of the player onPause and restore it onResume
    private var lastPlayState = false
    private var lastProgress = 0L

    private var lastVideoUrl: String? = null
    var paused = false

    fun onPause() {
        paused = true
        if (player?.playWhenReady == true) {
            lastPlayState = true
            player?.playWhenReady = false
        }
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
            loadMedia(it, autoPlay = lastPlayState)
        }
        playerView?.onResume()
        lastPlayState = false
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
}
