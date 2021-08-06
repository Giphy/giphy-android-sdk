package com.giphy.sdk.uidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.utils.videoAspectRatio
import com.giphy.sdk.ui.utils.videoUrl
import com.giphy.sdk.uidemo.VideoPlayer.VideoPlayer
import com.giphy.sdk.uidemo.databinding.FragmentVideoPlayerBinding

class ClipDialogFragment : androidx.fragment.app.DialogFragment() {

    lateinit var binding: FragmentVideoPlayerBinding

    private var media: Media? = null
    private var videoPlayer: VideoPlayer? = null

    companion object {
        private val KEY_VIDEO_PLAYER = "key_video_player"
        fun newInstance(media: Media): ClipDialogFragment {
            val fragment = ClipDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_VIDEO_PLAYER, media)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getTheme(): Int {
        return R.style.ClipsDialogStyle
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        media = arguments!!.getParcelable(KEY_VIDEO_PLAYER)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        videoPlayer?.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        videoPlayer?.onPause()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer?.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            media?.let { media ->
                videoPlayerView.preloadFirstFrame(media.url)
                videoPlayer?.onDestroy()
                videoPlayer = VideoPlayer(videoPlayerView, true)
                videoPlayer?.loadMedia(media.videoUrl ?: "", videoAspectRatio = media.videoAspectRatio)
            }
        }
    }
}
