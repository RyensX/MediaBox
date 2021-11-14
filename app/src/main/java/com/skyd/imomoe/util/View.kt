package com.skyd.imomoe.util

import android.view.View
import android.view.animation.AlphaAnimation

fun View.enable() {
    if (isEnabled) return
    isEnabled = true
}

fun View.disable() {
    if (!isEnabled) return
    isEnabled = false
}

fun View.gone(animate: Boolean = false, dur: Long = 500L) {
    if (visibility == View.GONE) return
    if (animate) startAnimation(AlphaAnimation(1f, 0f).apply { duration = dur })
    visibility = View.GONE
}

fun View.visible(animate: Boolean = false, dur: Long = 500L) {
    if (visibility == View.VISIBLE) return
    visibility = View.VISIBLE
    if (animate) startAnimation(AlphaAnimation(0f, 1f).apply { duration = dur })
}

fun View.invisible(animate: Boolean = false, dur: Long = 500L) {
    if (visibility == View.INVISIBLE) return
    visibility = View.INVISIBLE
    if (animate) startAnimation(AlphaAnimation(0f, 1f).apply { duration = dur })
}

fun View.clickScale(scale: Float = 0.75f, duration: Long = 100) {
    animate().scaleX(scale).scaleY(scale).setDuration(duration)
        .withEndAction {
            animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
        }.start()
}
