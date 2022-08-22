package com.giphy.sdk.uidemo.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.utils.GPHAbstractVideoPlayer
import com.giphy.sdk.ui.utils.px
import com.giphy.sdk.uidemo.R
import com.giphy.sdk.uidemo.SettingsDialogFragment
import com.giphy.sdk.uidemo.databinding.GifItemBinding
import com.giphy.sdk.uidemo.databinding.MessageItemBinding

class ClipsAdapterHelper {
    lateinit var player: GPHAbstractVideoPlayer
    lateinit var clipsPlaybackSetting: SettingsDialogFragment.ClipsPlaybackSetting
}

class MessageFeedAdapter(val items: MutableList<FeedDataItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_MESSAGE = 100
    private val ITEM_GIF = 101
    private val ITEM_NONE = 102
    private val ITEM_INVALID_API = 103
    private val ITEM_CLIP = 104

    var adapterHelper = ClipsAdapterHelper()

    var itemSelectedListener: (item: FeedDataItem) -> Unit = { _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_MESSAGE -> MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false))
            ITEM_GIF -> GifViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gif_item, parent, false))
            ITEM_CLIP -> ClipViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gif_item, parent, false), adapterHelper)
            ITEM_INVALID_API -> InvalidApiViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_api_key, parent, false))
            else -> throw RuntimeException("unsupported type")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        when (items[p1]) {
            is MessageItem -> (p0 as MessageViewHolder).bindMessage(items[p1] as MessageItem)
            is GifItem -> {
                (p0 as GifViewHolder).bindMessage(items[p1] as GifItem)
            }
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
            is MessageItem -> ITEM_MESSAGE
            is GifItem -> ITEM_GIF
            is ClipItem -> ITEM_CLIP
            is InvalidKeyItem -> ITEM_INVALID_API
            else -> ITEM_NONE
        }
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMessage(message: MessageItem) {
            MessageItemBinding.bind(itemView).textMessage.text = message.text
        }
    }

    inner class GifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMessage(message: GifItem) {
            GifItemBinding.bind(itemView).apply {
                gifView.setOnClickListener {
                    itemSelectedListener(message)
                }
                gifView.cornerRadius = 4.px.toFloat()
                gifView.setMedia(message.media)
                gifView.isBackgroundVisible = false
            }
        }
    }

    inner class ClipViewHolder(itemView: View, private val adapterHelper: ClipsAdapterHelper) :
        RecyclerView.ViewHolder(itemView) {

        lateinit var media: Media
        lateinit var player: GPHAbstractVideoPlayer

        val viewBinding = GifItemBinding.bind(itemView)

        fun bindMessage(message: ClipItem) {
            media = message.media
            player = adapterHelper.player

            val clickListener: View.OnClickListener = View.OnClickListener {
                if (adapterHelper.clipsPlaybackSetting == SettingsDialogFragment.ClipsPlaybackSetting.popup) {
                    itemSelectedListener(message)
                    pauseVideo()
                } else {
                    playVideo()
                }
            }
            if (adapterHelper.clipsPlaybackSetting == SettingsDialogFragment.ClipsPlaybackSetting.inline) {
                viewBinding.apply {
                    soundIcon.visibility = View.GONE
                    videoPlayerView.desiredWidth = 200.px
                    videoPlayerView.cornerRadius = 4.px.toFloat()
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
                player.loadMedia(media, view = viewBinding.videoPlayerView)
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
