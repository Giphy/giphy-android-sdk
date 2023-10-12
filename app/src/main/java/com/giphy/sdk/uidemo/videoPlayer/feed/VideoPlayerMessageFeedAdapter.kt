package com.giphy.sdk.uidemo.videoPlayer.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.utils.aspectRatio
import com.giphy.sdk.ui.utils.px
import com.giphy.sdk.ui.utils.videoUrl
import com.giphy.sdk.uidemo.R
import com.giphy.sdk.uidemo.videoPlayer.VideoPlayer
import com.giphy.sdk.uidemo.VideoPlayerSettingsDialogFragment
import com.giphy.sdk.uidemo.databinding.GifVideoItemBinding
import com.giphy.sdk.uidemo.feed.ClipItem
import com.giphy.sdk.uidemo.feed.FeedDataItem
import com.giphy.sdk.uidemo.feed.InvalidKeyItem

class VideoPlayerAdapterHelper {
    lateinit var player: VideoPlayer
    lateinit var videoPlaybackSetting: VideoPlayerSettingsDialogFragment.VideoPlaybackSetting
}

class VideoPlayerMessageFeedAdapter(private val items: MutableList<FeedDataItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemNone = 102
    private val itemInvalidAPI = 103
    private val itemClip = 104

    var adapterHelper = VideoPlayerAdapterHelper()

    var itemSelectedListener: (item: FeedDataItem) -> Unit = { _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemClip -> ClipViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gif_video_item, parent, false), adapterHelper)
            itemInvalidAPI -> InvalidApiViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_api_key, parent, false))
            else -> throw RuntimeException("unsupported type")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        when (items[p1]) {
            is ClipItem -> {
                (p0 as ClipViewHolder).bindMessage(items[p1] as ClipItem)
            }
            is InvalidKeyItem -> {
                // Nothing to do
            }
            else -> throw RuntimeException("type not allowed")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ClipItem -> itemClip
            is InvalidKeyItem -> itemInvalidAPI
            else -> itemNone
        }
    }

    inner class ClipViewHolder(itemView: View, private val adapterHelper: VideoPlayerAdapterHelper) :
        RecyclerView.ViewHolder(itemView) {

        lateinit var media: Media
        private lateinit var player: VideoPlayer

        private val viewBinding = GifVideoItemBinding.bind(itemView)

        fun bindMessage(message: ClipItem) {
            media = message.media
            player = adapterHelper.player

            val clickListener: View.OnClickListener = View.OnClickListener {
                if (adapterHelper.videoPlaybackSetting == VideoPlayerSettingsDialogFragment.VideoPlaybackSetting.popup) {
                    itemSelectedListener(message)
                    pauseVideo()
                } else {
                    playVideo()
                }
            }
            if (adapterHelper.videoPlaybackSetting == VideoPlayerSettingsDialogFragment.VideoPlaybackSetting.inline) {
                viewBinding.apply {
                    soundIcon.visibility = View.GONE
                    videoPlayerView.visibility = View.VISIBLE
                    gifView.visibility = View.GONE
                    videoPlayerView.setOnClickListener(clickListener)
                }
                if (message.autoPlay) {
                    playVideo()
                    if (this::player.isInitialized) {
                        player.setVolume(0f)
                    }
                }
            } else {
                viewBinding.apply {
                    gifView.setOnClickListener {
                        itemSelectedListener(message)
                    }
                    gifView.cornerRadius = 4.px.toFloat()
                    gifView.setMedia(message.media)
                    gifView.isBackgroundVisible = false
                    soundIcon.visibility = View.VISIBLE
                    gifView.visibility = View.VISIBLE
                    videoPlayerView.visibility = View.GONE
                }
            }
        }

        private fun playVideo() {
            if (this::player.isInitialized) {
                media.videoUrl?.let { player.loadMedia(it, view = viewBinding.videoPlayerView, videoAspectRatio = media.aspectRatio) }
            }
        }

        private fun pauseVideo() {
            if (this::player.isInitialized) {
                player.onPause()
            }
        }
    }

    inner class InvalidApiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
