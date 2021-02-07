package com.skyd.imomoe.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs

//解决外层SwipeRefreshLayout内有Vp的滑动冲突
class VpSwipeRefreshLayout(context: Context, attrs: AttributeSet) :
    SwipeRefreshLayout(context, attrs) {
    private var startY = 0f
    private var startX = 0f

    //vp是否拖拽
    private var isVpSlop = false
    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = ev.y
                startX = ev.x
                isVpSlop = false
            }
            MotionEvent.ACTION_MOVE -> {
                //vp正在滑动
                if (isVpSlop) return false

                //vp没有滑动
                val endY = ev.y
                val endX = ev.x
                val distanceX = abs(endX - startX)
                val distanceY = abs(endY - startY)
                //水平距离>纵向距离，事件给viewPager
                if (distanceX > touchSlop && distanceX > distanceY) {
                    isVpSlop = true
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                isVpSlop = false
        }
        //水平距离<=纵向距离，事件给swipeRefreshLayout
        return super.onInterceptTouchEvent(ev)
    }

}