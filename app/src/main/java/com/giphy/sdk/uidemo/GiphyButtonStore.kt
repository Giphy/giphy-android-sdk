package com.giphy.sdk.uidemo

import android.view.View
import com.giphy.sdk.ui.views.buttons.GPHContentTypeButtonStyle
import com.giphy.sdk.ui.views.buttons.GPHGifButtonColor
import com.giphy.sdk.ui.views.buttons.GPHGifButtonStyle
import com.giphy.sdk.ui.views.buttons.GPHGiphyButtonStyle

class GPHButtonConfig(val type: Class<View>) {
    var brandButtonStyle: GPHGiphyButtonStyle? = null
    var gifButtonStyle: GPHGifButtonStyle? = null
    var contentTypeStyle: GPHContentTypeButtonStyle? = null
    var color: GPHGifButtonColor? = null
}