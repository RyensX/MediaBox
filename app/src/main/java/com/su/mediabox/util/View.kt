package com.su.mediabox.util

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewStub
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.annotation.Px
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.su.mediabox.R
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

inline fun <VH : RecyclerView.ViewHolder> VH.setOnClickListener(
    target: View,
    crossinline onClick: VH.(position: Int) -> Unit
) {
    target.setOnClickListener {
        onClick(bindingAdapterPosition)
    }
}

inline fun <VH : RecyclerView.ViewHolder> VH.setOnLongClickListener(
    target: View,
    crossinline onLongClick: VH.(position: Int) -> Boolean
) {
    target.setOnLongClickListener {
        onLongClick(bindingAdapterPosition)
    }
}

@SuppressLint("ClickableViewAccessibility")
inline fun <VH : RecyclerView.ViewHolder> VH.setOnTouchListener(
    target: View,
    crossinline onTouch: VH.(event: MotionEvent, position: Int) -> Boolean
) {
    target.setOnTouchListener { _, e ->
        onTouch(e, bindingAdapterPosition)
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
        visibilityItemPos =
            (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                .coerceAtLeast(visibilityItemPos)
    logD("列表跳转", "目标:$position 当前可见:$visibilityItemPos")
    if ((position - visibilityItemPos).absoluteValue > smoothLimit)
        scrollToPosition(position)
    else
        smoothScrollToPosition(position)
}

@SuppressLint("ClickableViewAccessibility")
fun View.setLongPress() {
    setOnLongClickListener {
        logD("长按", "sdsdsd")
        true
    }
    setOnTouchListener { _, event ->
        if (event.actionMasked == MotionEvent.ACTION_CANCEL) {
            logD("释放按钮", "sdd")
        }
        false
    }
}

/**
 * 根据类型删除ItemDecoration
 */
fun <T : RecyclerView.ItemDecoration> RecyclerView.removeItemDecorations(target: Class<T>) {
    for (i in 0 until itemDecorationCount)
        if (getItemDecorationAt(i).javaClass == target) {
            logD("删除ItemDecoration", "target:${target.simpleName} rv:${toString()} index=$i")
            removeItemDecorationAt(i)
            //由于没有列表引用不能使用迭代器且重测重绘方法不公开所以只能递归删除了
            removeItemDecorations(target)
            break
        } else
            logD("尝试删除", "$i")
}

/**
 * 根据类型返回第一个ItemDecoration
 *
 * @return 找不得则返回null
 */
inline fun <reified T : RecyclerView.ItemDecoration> RecyclerView.getFirstItemDecorationBy(): T? {
    for (i in 0 until itemDecorationCount) {
        val itemDecoration = getItemDecorationAt(i)
        if (itemDecoration.javaClass == T::class.java)
            return itemDecoration as T
    }
    return null
}

/**
 * 把VP2和BottomNavigationView绑定
 */
fun ViewPager2.bindBottomNavigationView(bottomBav: BottomNavigationView) {
    //索引-ID映射
    val idMap = mutableListOf<Int>()
    for (i in 0 until bottomBav.menu.size())
        idMap.add(bottomBav.menu.getItem(i).itemId)
    //绑定页面滑动
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (position < idMap.size)
                bottomBav.selectedItemId = idMap[position]
        }
    })
    //绑定底栏切换
    bottomBav.setOnNavigationItemSelectedListener { item ->
        idMap.indexOf(item.itemId).also {
            if (it != -1)
                currentItem = it
        }
        true
    }
}

fun getSpecSize(defaultSize: Int, measureSpec: Int): Int {
    val mode = MeasureSpec.getMode(measureSpec)
    val size = MeasureSpec.getSize(measureSpec)
    return when (mode) {
        MeasureSpec.UNSPECIFIED -> defaultSize
        MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> size
        else -> defaultSize
    }
}

//为底栏添加提示徽章
fun BottomNavigationView.addBadge(position: Int): TextView? {
    val menus = getChildAt(0)
    if (menus is BottomNavigationMenuView) {
        val menu = menus.getChildAt(position)
        if (menu is BottomNavigationItemView) {
            val badgeView = context.layoutInflater.inflate(
                R.layout.bottom_nav_menu_item_badge,
                menus,
                false
            )
            menu.addView(badgeView)
            return badgeView.findViewById(R.id.badge_value)
        }
    }
    return null
}

fun View.setPaddingVertical(@Px size: Int) {
    setPadding(paddingLeft, size, paddingRight, size)
}

fun View.setPaddingHorizontal(@Px size: Int) {
    setPadding(size, paddingTop, size, paddingBottom)
}