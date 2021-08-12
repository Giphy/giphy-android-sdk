package com.giphy.sdk.uidemo.VideoPlayer

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoBufferingIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val colorAnimation: ValueAnimator
    var visible = false

    init {
        val colorFrom = Color.argb(0, 0, 0, 0)
        val colorTo = Color.argb(64, 0, 0, 0)
        colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.repeatCount = ValueAnimator.INFINITE
        colorAnimation.repeatMode = ValueAnimator.REVERSE
        colorAnimation.duration = 1000 // milliseconds
        colorAnimation.addUpdateListener { animator -> setBackgroundColor(animator.animatedValue as Int) }
    }

    override fun setVisibility(visibility: Int) {
        if (visibility == View.VISIBLE) {
            visible = true
            // use a 500ms delay before showing the indicator
            GlobalScope.launch(context = Dispatchers.Main) {
                delay(500)
                if (visible) {
                    super.setVisibility(visibility)
                    colorAnimation.start()
                }
            }
        } else {
            visible = false
            super.setVisibility(visibility)
            colorAnimation.cancel()
        }
    }
}
