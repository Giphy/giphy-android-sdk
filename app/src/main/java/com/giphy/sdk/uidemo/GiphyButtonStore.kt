package com.giphy.sdk.uidemo

import android.view.View
import com.giphy.sdk.ui.views.GPHBrandButtonFill
import com.giphy.sdk.ui.views.GPHGenericButtonGradient
import com.giphy.sdk.ui.views.GPHGenericButtonStyle
import kotlin.math.floor

object GiphyButtonStore {

    val itemCount: Int
        get() {
            return ButtonItems.values().map { it.itemCount }.sum()
        }

    fun getButtonType(position: Int): ButtonItems {
        if (position < ButtonItems.branded.itemCount) {
            return ButtonItems.branded
        } else if (position < (ButtonItems.branded.itemCount + ButtonItems.generic.itemCount)) {
            return ButtonItems.generic
        } else {
            return ButtonItems.genericRounded
        }
    }

    fun getGradientIndex(button: ButtonItems, position: Int): Int {
        var classPosition = 0
        if (button == ButtonItems.generic) {
            classPosition = position - ButtonItems.branded.itemCount
        } else if (button == ButtonItems.genericRounded) {
            classPosition = position - ButtonItems.branded.itemCount - ButtonItems.generic.itemCount
        }
        return floor(classPosition.toFloat() / 3F).toInt()
    }
}

enum class ButtonItems {
    branded,
    generic,
    genericRounded;

    val itemCount: Int
        get() {
            return when (this) {
                branded -> 3
                else -> 9
            }
        }
}

class GPHButtonConfig(val type: Class<View>) {
    var gphBrandFill: GPHBrandButtonFill? = null
    var rounded: Boolean? = null
    var gphGenericGradient: GPHGenericButtonGradient? = null
    var gphGenericStyle: GPHGenericButtonStyle? = null
}