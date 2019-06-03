package com.giphy.sdk.uidemo.feed

import com.giphy.sdk.core.models.Media

open class FeedDataItem
class MessageItem(val text:String): FeedDataItem()
class GifItem(val media: Media): FeedDataItem()