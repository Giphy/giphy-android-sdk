package com.giphy.sdk.uidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.views.GPHVideoPlayer
import kotlinx.android.synthetic.main.fragment_video_player.*

class ClipDialogFragment : androidx.fragment.app.DialogFragment() {

    private var media: Media? = null
    private var videoPlayer: GPHVideoPlayer? = null

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
        return inflater.inflate(R.layout.fragment_video_player, container, false)
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
        media?.let { media ->
            gphVideoPlayerView.preloadFirstFrame(media)
            videoPlayer?.onDestroy()
            videoPlayer = GPHVideoPlayer(gphVideoPlayerView, true)
            videoPlayer?.loadMedia(media)
        }
    }
}
