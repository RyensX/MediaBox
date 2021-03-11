package com.skyd.imomoe.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class BottomSheetRecyclerView : RecyclerView {
    private var mStartY: Float = 0f
    private var mStartX: Float = 0f
    private var actionUpCancel = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                actionUpCancel = false
                mStartX = ev.x
                mStartY = ev.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                actionUpCancel = true
            }
        }
        if (!actionUpCancel) {
            if (!canScrollVertically(-1)) {
                parent.requestDisallowInterceptTouchEvent(false)
            } else {
                // 此处不能加判断abs(mStartY-ev.Y)>0，加上容易下划时把dialog拉消失，
                // 滑动很快时distance->0.0，因此不能判断abs(mStartY-ev.Y)，很奇怪
                val endX = ev.x
                val endY = ev.y
                val distanceX = abs(endX - mStartX)
                val distanceY = abs(endY - mStartY)
                if (distanceX >= 1.0 && distanceX > distanceY) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}