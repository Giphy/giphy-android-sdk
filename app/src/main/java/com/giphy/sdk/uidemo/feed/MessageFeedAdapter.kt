package com.giphy.sdk.uidemo.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.tracking.isVideo
import com.giphy.sdk.ui.views.GPHVideoPlayer
import com.giphy.sdk.ui.views.GPHVideoPlayerState
import com.giphy.sdk.uidemo.R
import com.giphy.sdk.uidemo.SettingsDialogFragment
import kotlinx.android.synthetic.main.gif_item.view.*
import kotlinx.android.synthetic.main.message_item.view.*

class ClipsAdapterHelper {
    lateinit var player: GPHVideoPlayer
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
            is GifItem -> (p0 as GifViewHolder).bindMessage(items[p1] as GifItem)
            is ClipItem -> { (p0 as ClipViewHolder).bindMessage(items[p1] as ClipItem) }
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
            itemView.textMessage.text = message.text
//            itemView.textMessage.setTextColor(theme.textColor)
//            itemView.textMessage.setBackgroundResource(if (theme == LightTheme) R.drawable.message_background_light else R.drawable.message_background_dark)
//            itemView.timeView.setTextColor(theme.textColor)
        }
    }

    inner class GifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMessage(message: GifItem) {
            itemView.gifView.setMedia(message.media)
            itemView.gifView.isBackgroundVisible = false
            itemView.soundIcon.visibility = if (message.media.isVideo) View.VISIBLE else View.GONE
        }
    }

    inner class ClipViewHolder(itemView: View, private val adapterHelper: ClipsAdapterHelper) :
        RecyclerView.ViewHolder(itemView) {

        lateinit var media: Media
        lateinit var player: GPHVideoPlayer

        fun bindMessage(message: ClipItem) {
            media = message.media
            player = adapterHelper.player

            player.addListener { playerState ->
                when (playerState) {
                    is GPHVideoPlayerState.MuteChanged -> {
                        updateSoundModeIcon()
                    }
                    is GPHVideoPlayerState.MediaChanged -> {
                        updateSoundModeIcon()
                        if (media.id != playerState.media.id) {
                            itemView.gifView.visibility = View.VISIBLE
                            itemView.gphVideoPlayerView.visibility = View.GONE
                        } else {
                            itemView.gphVideoPlayerView.visibility = View.VISIBLE
                            itemView.gifView.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@addListener
                }
            }

            val clickListener: View.OnClickListener = View.OnClickListener {
                if (adapterHelper.clipsPlaybackSetting == SettingsDialogFragment.ClipsPlaybackSetting.popup) {
                    itemSelectedListener(message)
                    pauseVideo()
                } else {
                    playVideo()
                }
            }

            itemView.gifView.setOnClickListener(clickListener)
            itemView.gphVideoPlayerView.setOnClickListener(clickListener)

            itemView.gifView.setMedia(message.media)
            itemView.gifView.isBackgroundVisible = false
            itemView.soundIcon.visibility =
                if (message.media.isVideo) View.VISIBLE else View.GONE
        }

        private fun playVideo() {
            if (this::player.isInitialized) {
                player.loadMedia(media, view = itemView.gphVideoPlayerView)
            }
        }

        private fun pauseVideo() {
            if (this::player.isInitialized) {
                player.onPause()
            }
        }

        private fun updateSoundModeIcon() {
            if (this::player.isInitialized && player.getVolume() > 0 && player.media.id == media.id) {
                itemView.soundIcon.setImageResource(if (player.getVolume() > 0) com.giphy.sdk.ui.R.drawable.gph_ic_sound else com.giphy.sdk.ui.R.drawable.gph_ic_no_sound)
            } else {
                itemView.soundIcon.setImageResource(com.giphy.sdk.ui.R.drawable.gph_ic_no_sound)
            }
        }
    }

    inner class InvalidApiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
