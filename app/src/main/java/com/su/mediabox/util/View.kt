package com.su.mediabox.util

import android.view.View
import android.view.ViewStub
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue

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

inline fun RecyclerView.ViewHolder.setOnClickListener(
    target: View,
    crossinline onClick: RecyclerView.ViewHolder.(position: Int) -> Unit
) {
    target.setOnClickListener {
        onClick(bindingAdapterPosition)
    }
}

inline fun RecyclerView.ViewHolder.setOnLongClickListener(
    target: View,
    crossinline onLongClick: RecyclerView.ViewHolder.(position: Int) -> Boolean
) {
    target.setOnLongClickListener {
        onLongClick(bindingAdapterPosition)
    }
}

fun View.OnClickListener.setViewsOnClickListener(vararg views: View) {
    views.forEach { it.setOnClickListener(this) }
}

/**
 * 仅在有数据时visible，否则gone
 */
inline fun <T, V : View> V.displayOnlyIfHasData(data: T?, hasData: V.(T) -> Unit): V {
    if (data != null && data.toString().isNotBlank()) {
        visible()
        hasData(data)
    } else
        gone()
    return this
}

val ViewStub.isInflate: Boolean
    get() = parent == null

fun ViewStub.smartInflate() {
    if (!isInflate)
        inflate()
}

/**
 * 智能跳转，根据目标位置与当前位置差距对比[smoothLimit]选择直接跳转还是带动画跳转
 */
fun RecyclerView.smartScrollToPosition(position: Int, smoothLimit: Int = 30) {
    var visibilityItemPos = 0
    if (layoutManager is LinearLayoutManager)
        visibilityItemPos = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    if ((position - visibilityItemPos).absoluteValue > smoothLimit)
        scrollToPosition(position)
    else
        smoothScrollToPosition(position)
}