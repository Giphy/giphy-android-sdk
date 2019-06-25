package com.giphy.sdk.uidemo.feed

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.giphy.sdk.ui.themes.LightTheme
import com.giphy.sdk.ui.themes.Theme
import com.giphy.sdk.uidemo.R
import kotlinx.android.synthetic.main.gif_item.view.*
import kotlinx.android.synthetic.main.message_item.view.*


class MessageFeedAdapter(val items: MutableList<FeedDataItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_MESSAGE = 100
    private val ITEM_GIF = 101
    private val ITEM_NONE = 102

    var theme: Theme = LightTheme

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_MESSAGE -> MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false))
            ITEM_GIF -> GifViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gif_item, parent, false))
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
            else -> throw RuntimeException("type not allowed")
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (items[position]) {
            is MessageItem -> return ITEM_MESSAGE
            is GifItem -> return ITEM_GIF
            else -> return ITEM_NONE
        }
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMessage(message: MessageItem) {
            itemView.textMessage.text = message.text
            itemView.textMessage.setTextColor(theme.textColor)
            itemView.textMessage.setBackgroundResource(if (theme == LightTheme) R.drawable.message_background_light else R.drawable.message_background_dark)
            itemView.timeView.setTextColor(theme.textColor)
        }
    }

    inner class GifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMessage(message: GifItem) {
            itemView.gifView.setMedia(message.media, placeholderColor = Color.BLUE)
        }
    }
}
