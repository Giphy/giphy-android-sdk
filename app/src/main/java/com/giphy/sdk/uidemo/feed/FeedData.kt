package com.giphy.sdk.uidemo.feed

import com.giphy.sdk.core.models.Media

open class FeedDataItem(val author: Author)
class MessageItem(val text: String, author: Author) : FeedDataItem(author)
class GifItem(val media: Media, author: Author) : FeedDataItem(author)
class InvalidKeyItem(author: Author): FeedDataItem(author)

enum class Author {
    Me,
    GifBot
}