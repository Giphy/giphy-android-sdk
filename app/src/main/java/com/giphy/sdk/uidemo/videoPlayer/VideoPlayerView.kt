package com.giphy.sdk.uidemo.videoPlayer

import android.content.Context
import android.content.res.Resources
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.SystemClock
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import com.giphy.sdk.ui.utils.px
import com.giphy.sdk.uidemo.R
import com.giphy.sdk.uidemo.databinding.VideoPlayerViewBinding
import timber.log.Timber

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

open class VideoPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var isFirstLoading = false

    private var prepareTime = 0L

    var showControls: Boolean = true
    private var loopCount = 0
    var maxLoopsBeforeMute = 3

    var cornerRadius = 0.px.toFloat()
    var desiredWidth: Int = 200.px
    var desiredHeight: Int = 112.px

    private lateinit var player: VideoPlayer

    private var videoUrl: String? = null
    private var previewUrl: String? = null

    var aspectRatio: Float? = null

    private val listener: PlayerStateListener = {
        when (it) {
            is VideoPlayerState.Error -> {
                viewBinding.bufferingAnimation.visibility = View.GONE
                viewBinding.videoControls.visibility = View.GONE
                viewBinding.errorView.visibility = View.VISIBLE
                viewBinding.bufferingAnimation.visibility = View.GONE
            }
            VideoPlayerState.Ready -> {
                viewBinding.bufferingAnimation.visibility = View.GONE
                if (isFirstLoading) {
                    Timber.d("initialLoadTime=${SystemClock.elapsedRealtime() - prepareTime}")
                    isFirstLoading = false
                    viewBinding.videoControls.visibility = if (showControls) View.VISIBLE else View.GONE
                    viewBinding.initialImage.visibility = View.GONE
                }
            }
            VideoPlayerState.Buffering -> {
                viewBinding.bufferingAnimation.visibility = View.VISIBLE
            }
            VideoPlayerState.Repeated -> {
                if (loopCount + 1 > maxLoopsBeforeMute - 1) {
                    player.setVolume(0f)
                } else if (player.getVolume() > 0f) {
                    loopCount += 1
                }
            }
            is VideoPlayerState.MuteChanged -> {
                if (!it.muted) {
                    loopCount = 0
                }
            }
            is VideoPlayerState.CaptionsTextChanged -> {
                if (it.subtitle.isEmpty()) {
                    viewBinding.subtitles.setPadding(0.px, 0.px, 0.px, 0.px)
                } else {
                    viewBinding.subtitles.setPadding(13.px, 5.px, 13.px, 8.px)
                }
                viewBinding.subtitles.text = it.subtitle
            }
            is VideoPlayerState.CaptionsVisibilityChanged -> {
                viewBinding.subtitles.visibility = if (it.visible) View.VISIBLE else View.INVISIBLE
            }
            else -> {}
        }
    }

    private val viewBinding: VideoPlayerViewBinding =
        VideoPlayerViewBinding.bind(View.inflate(context, R.layout.video_player_view, this))

    init {
        viewBinding.initialImage.setLegacyVisibilityHandlingEnabled(true)

        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(0xAA000000.toInt())
            // make it rounded corners
            this.cornerRadius = 8f
            viewBinding.subtitles.background = this
        }
        viewBinding.subtitles.textSize = 13f

        val array = context.obtainStyledAttributes(attrs, R.styleable.VideoPlayerView, 0, 0)
        showControls = array.getBoolean(R.styleable.VideoPlayerView_showControls, true)
        array.recycle()
    }

    fun preloadFirstFrame(url: String?) {
        this.previewUrl = url
        Timber.d("preloadFirstFrame $url")
        viewBinding.initialImage.setImageURI(url)
        viewBinding.initialImage.visibility = View.VISIBLE
    }

    fun prepare(videoUrl: String, player: VideoPlayer) {
        loopCount = 0
        this.player = player
        this.videoUrl = videoUrl
        prepareTime = SystemClock.elapsedRealtime()
        player.setVideoSurfaceView(viewBinding.surfaceView)
        isFirstLoading = true
        viewBinding.subtitles.text = ""
        viewBinding.errorView.visibility = View.GONE
        viewBinding.bufferingAnimation.visibility = View.GONE
        viewBinding.videoControls.visibility = View.GONE
        viewBinding.initialImage.visibility = View.VISIBLE
        requestLayout()

        player.addListener(listener)
        viewBinding.subtitles.visibility = if (player.showCaptions) View.VISIBLE else View.INVISIBLE
        if (showControls) {
            viewBinding.videoControls.prepare(videoUrl, player)
        }
    }

    /**
     * This trick is required to support ReactNative wrapper.
     * This view relies on a measure + layout pass happening after it calls requestLayout().
     * https://github.com/facebook/react-native/issues/4990#issuecomment-180415510
     * https://stackoverflow.com/questions/39836356/react-native-resize-custom-ui-component
     */
    override fun requestLayout() {
        super.requestLayout()

        post(measureAndLayout)
    }

    private val measureAndLayout = Runnable {
        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        layout(left, top, right, bottom)
    }

    /**
     * Adjust height so the view match the aspect ratio of the video
     */
    var params = LayoutParams(0, 0, Gravity.CENTER)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (aspectRatio == null) {
            super.onMeasure(
                widthMeasureSpec,
                heightMeasureSpec
            )
            return
        }

        val aspectRatio = this.aspectRatio!!

        var height = MeasureSpec.getSize(heightMeasureSpec)
        var width = (height * aspectRatio).toInt()

        if (height == 0) {
            if (width == 0) {
                width = desiredWidth
            }
            height = (width / aspectRatio).toInt()
        } else if (width == 0) {
            if (height == 0) {
                height = desiredHeight
            }
            width = (height * aspectRatio).toInt()
        }

        val widthSpec = MeasureSpec.getSize(widthMeasureSpec)
        if (widthSpec > 0 && width > widthSpec) {
            width = MeasureSpec.getSize(widthMeasureSpec)
            height = (width / aspectRatio).toInt()
        }

        params.width = width
        params.height = height

        viewBinding.surfaceView.layoutParams = params

        viewBinding.initialImage.layoutParams = params
        viewBinding.bufferingAnimation.layoutParams = params
        viewBinding.videoControls.layoutParams = params
        viewBinding.errorView.layoutParams = params
        viewBinding.subtitlesView.layoutParams = params
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        addOutline()
    }

    private fun addOutline() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && cornerRadius > 0) {
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, width, height, cornerRadius)
                }
            }
            clipToOutline = true
        }
    }

    fun onProgress(position: Long) {
        viewBinding.videoControls.updateProgress(position)
    }

    fun enterSharingMode() {
        viewBinding.videoControls.visibility = View.GONE
    }

    fun exitSharingMode() {
        viewBinding.videoControls.visibility = View.VISIBLE
    }

    fun onResume() {
        viewBinding.videoControls.visibility = View.VISIBLE
        viewBinding.videoControls.onResume()
    }

    fun onDestroy() {
        if (this::player.isInitialized) {
            player.removeListener(listener)
        }
    }

    fun onPause() {
        player.removeListener(listener)
        viewBinding.videoControls.visibility = View.GONE
        viewBinding.videoControls.onPause()
    }
}

const val PROGRESS_INTERVAL = 40L
