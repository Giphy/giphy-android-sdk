package com.giphy.sdk.uidemo.feed

import com.giphy.sdk.core.models.Media

open class FeedDataItem
class MessageItem(val text: String) : FeedDataItem()
class GifItem(val media: Media) : FeedDataItem()
class ClipItem(val media: Media, var autoPlay: Boolean) : FeedDataItem()
class InvalidKeyItem : FeedDataItem()

enum class Author {
    Me,
    GifBot
}
