package com.skyd.imomoe.view.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import kotlin.math.atan2
import kotlin.math.sqrt

class ZoomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var scale = 1f      // 伸缩比例
    private var mTranslationX = 0f // 移动X
    private var mTranslationY = 0f // 移动Y
    private var mRotation = 0f // 旋转角度

    // 移动过程中临时变量
    private var actionX = 0f
    private var actionY = 0f
    private var spacing = 0f
    private var degree = 0f
    private var moveType = 0 // 0=未选择，2=缩放

    // 恢复初始
    fun restore() {
        scale = 1f
        mTranslationX = 0f
        mTranslationY = 0f
        mRotation = 0f
        actionX = 0f
        actionY = 0f
        spacing = 0f
        degree = 0f
        moveType = 0

        translationX = 0f
        translationY = 0f
        scaleX = 1f
        scaleY = 1f
        rotation = 0f
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                moveType = 1
                val centerX = getCenterX(event)
                val centerY = getCenterY(event)
                actionX = centerX
                actionY = centerY
                spacing = getSpacing(event)
                degree = getDegree(event)
            }
            MotionEvent.ACTION_MOVE -> if (moveType == 1) {
                val centerX = getCenterX(event)
                val centerY = getCenterY(event)
                mTranslationX = mTranslationX + centerX - actionX
                mTranslationY = mTranslationY + centerY - actionY
                translationX = mTranslationX
                translationY = mTranslationY
                actionX = centerX
                actionY = centerY

                scale = scale * getSpacing(event) / spacing
                scaleX = scale
                scaleY = scale
                mRotation = mRotation + getDegree(event) - degree
                if (mRotation > 360) {
                    mRotation -= 360
                }
                if (mRotation < -360) {
                    mRotation += 360
                }
                rotation = mRotation
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                moveType = 0
            }
        }
        return super.onTouchEvent(event)
    }

    // 触碰两点间中心点X
    private fun getCenterX(event: MotionEvent): Float {
        return (event.getRawX(0) + event.getRawX(1)) / 2f
    }

    // 触碰两点间中心点Y
    private fun getCenterY(event: MotionEvent): Float {
        return (event.getRawY(0) + event.getRawY(1)) / 2f
    }

    // 触碰两点间距离
    private fun getSpacing(event: MotionEvent): Float {
        //通过三角函数得到两点间的距离
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y.toDouble()).toFloat()
    }

    // 取旋转角度
    private fun getDegree(event: MotionEvent): Float {
        //得到两个手指间的旋转角度
        val deltaX = event.getX(0) - event.getX(1).toDouble()
        val deltaY = event.getY(0) - event.getY(1).toDouble()
        val radians = atan2(deltaY, deltaX)
        return Math.toDegrees(radians).toFloat()
    }

    init {
        isClickable = true
    }
}