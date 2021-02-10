package com.skyd.imomoe.view.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class ViewPager2View(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val mViewPager2: ViewPager2 = ViewPager2(context)

    private var mStartX = 0f
    private var mStartY = 0f
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    init {
        mViewPager2.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        attachViewToParent(mViewPager2, 0, mViewPager2.layoutParams)
    }

    fun <T : RecyclerView.ViewHolder> setAdapter(adapter: RecyclerView.Adapter<T>) {
        mViewPager2.adapter = adapter
    }

    fun setOffscreenPageLimit(limit: Int) {
        mViewPager2.offscreenPageLimit = limit
    }

    fun getViewPager() = mViewPager2

    fun setPageTransformer(@Nullable transformer: ViewPager2.PageTransformer) {
        mViewPager2.setPageTransformer(transformer)
    }

    fun getOrientation() = mViewPager2.orientation

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = event.x
                mStartY = event.y
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = event.x
                val endY = event.y
                val distanceX = abs(endX - mStartX)
                val distanceY = abs(endY - mStartY)
                if (mViewPager2.orientation == RecyclerView.HORIZONTAL &&
                    (distanceX > mTouchSlop && distanceX * 0.5f > distanceY)
                ) {
                    parent.requestDisallowInterceptTouchEvent(true)
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }

            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onInterceptTouchEvent(event)
    }

}