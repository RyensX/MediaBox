package com.skyd.imomoe.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import kotlin.math.abs

class ViewPager2View(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val mViewPager2: ViewPager2 = ViewPager2(context)

    private var mStartX = 0f
    private var mStartY = 0f
    private var mTouchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop

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

    fun setCurrentItem(item: Int) {
        mViewPager2.currentItem = item
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        mViewPager2.setCurrentItem(item, smoothScroll)
    }

    fun getOrientation() = mViewPager2.orientation

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = ev.x
                mStartY = ev.y
                mViewPager2.isUserInputEnabled = true
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x
                val endY = ev.y
                val disX = abs(endX - mStartX)
                val disY = abs(endY - mStartY)
                mViewPager2.isUserInputEnabled =
                    !(disX * 0.6 < disY && mViewPager2.scrollState != SCROLL_STATE_DRAGGING)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                mViewPager2.isUserInputEnabled = true
        }
        return super.dispatchTouchEvent(ev)
    }
}