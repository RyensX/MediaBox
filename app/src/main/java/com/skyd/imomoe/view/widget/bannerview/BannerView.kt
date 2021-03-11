package com.skyd.imomoe.view.widget.bannerview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.skyd.imomoe.R
import com.skyd.imomoe.view.widget.bannerview.BannerUtil.getPosition
import com.skyd.imomoe.view.widget.bannerview.BannerUtil.getRealPosition
import com.skyd.imomoe.view.widget.bannerview.indicator.Indicator
import com.skyd.imomoe.view.widget.bannerview.indicator.SimpleIndicator
import java.util.*
import kotlin.math.abs


/**
 * Created by Sky_D on 2021-02-08.
 */
open class BannerView(mContext: Context, attrs: AttributeSet?) :
    RelativeLayout(mContext, attrs) {

    companion object {
        /**
         * Value to indicate that the default caching mechanism of RecyclerView should be used instead
         * of explicitly prefetch and retain pages to either side of the current page.
         */
        const val OFFSCREEN_PAGE_LIMIT_DEFAULT = -1
    }

    private val mViewPager2: ViewPager2 = ViewPager2(mContext)

    // 自动轮播
    private var autoPlay: Boolean = false

    // 轮播是否暂停
    private var isPause: Boolean = false
    private var mAutoPlayInterval: Long = 0L
    private var mTimer: Timer = Timer()
    private val mHandler = Handler(Looper.getMainLooper())
    private val mAutoPlayRunnable: Runnable = object : Runnable {
        override fun run() {
            nextPage()
            mHandler.postDelayed(this, mAutoPlayInterval)
        }
    }
//    private var mAutoPlayTask: TimerTask = object : TimerTask() {
//        override fun run() {
//            mHandler.post {
//                nextPage()
//            }
//        }
//    }

    private var mStartX = 0f
    private var mStartY = 0f

    private var mOffscreenPageLimit = 1
    private var mIndicator: Indicator = SimpleIndicator()

    // 各个Item之间的间隔
    private var mPageMargin: Int = 0

    // 用于显示多个Item（左右padding）
    private var mBannerPaddingStart: Int = 0
    private var mBannerPaddingEnd: Int = 0
    private var mBannerPaddingTop: Int = 0
    private var mBannerPaddingBottom: Int = 0

    init {
        mViewPager2.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(mViewPager2)
        mViewPager2.offscreenPageLimit = mOffscreenPageLimit
        // 初始化自定义属性
        initAttr(attrs)

        mViewPager2.registerOnPageChangeCallback(CycleOnPageChangeCallback())
    }

    /**
     * 初始化自定义属性
     * @param attributeSet 属性集合
     */
    private fun initAttr(attributeSet: AttributeSet?) {
        attributeSet?.run {
            val attrs = context.obtainStyledAttributes(this, R.styleable.BannerView)
            setBannerPadding(
                attrs.getDimensionPixelSize(R.styleable.BannerView_bannerPaddingStart, 0),
                attrs.getDimensionPixelSize(R.styleable.BannerView_bannerPaddingEnd, 0),
                attrs.getDimensionPixelSize(R.styleable.BannerView_bannerPaddingTop, 0),
                attrs.getDimensionPixelSize(R.styleable.BannerView_bannerPaddingBottom, 0)
            )
            setPageMargin(attrs.getDimensionPixelSize(R.styleable.BannerView_pageMargin, 0))
            attrs.recycle()
        }
    }

    // 窗体不可见时暂停轮播
    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility != View.VISIBLE) {
            if (autoPlay) {
                mHandler.removeCallbacks(mAutoPlayRunnable)
                isPause = true
            }
        } else {
            if (autoPlay && isPause) {
                mHandler.postDelayed(mAutoPlayRunnable, mAutoPlayInterval)
                isPause = false
            }
        }
    }

    // 当View离开附着的窗口时停止轮播
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 防止内存泄漏
        mHandler.removeCallbacks(mAutoPlayRunnable)
        autoPlay = false
    }

    /**
     * 设置ViewPager2的Adapter
     * @param adapter ViewPager2的Adapter
     */
    fun <T : RecyclerView.ViewHolder> setAdapter(adapter: RecyclerView.Adapter<T>) {
        mViewPager2.adapter = adapter
        setCurrentItem(0, false)
    }

    /**
     * 获取ViewPager2的Adapter
     */
    fun getAdapter() = mViewPager2.adapter

    /**
     * Set the number of pages that should be retained to either side of the currently visible
     * page(s)
     * @param limit How many pages will be kept offscreen on either side. Valid values are all
     *        values {@code >= 1} and {@link #OFFSCREEN_PAGE_LIMIT_DEFAULT}
     */
    fun setOffscreenPageLimit(limit: Int) {
        mOffscreenPageLimit = limit
        mViewPager2.offscreenPageLimit = mOffscreenPageLimit
    }

    /**
     * 设置Indicator
     * @param indicator Indicator
     */
    fun setIndicator(indicator: Indicator) {
        if (mIndicator === indicator) return
        removeIndicatorView()
        mIndicator = indicator
        addView(mIndicator.getView())
        mIndicator.onChanged(getPageCount(), getCurrentItem())
    }

    /**
     * 移除当前Indicator
     */
    private fun removeIndicatorView() {
        removeView(mIndicator.getView())
    }

    /**
     * 获取所有页面虚假的个数，即不包括第0个和第size-1个
     */
    private fun getPageCount(): Int {
        val count = getRealPageCount() - 2
        return if (count < 0) 0 else count
    }

    /**
     * 获取所有页面真实的个数，即包括第0个和第size-1个
     */
    private fun getRealPageCount(): Int {
        mViewPager2.adapter?.let {
            return it.itemCount
        }
        return 0
    }

    /**
     * 设置当前页面
     * @param index 虚假的下标，即不包括0和size-1
     * @param smoothScroll 切换页面是否附带动画
     */
    fun setCurrentItem(index: Int, smoothScroll: Boolean) {
        mViewPager2.setCurrentItem(getRealPosition(index), smoothScroll)
        mIndicator.onPageSelected(getCurrentItem())
    }

    /**
     * 设置当前页面
     * @param index 真实的下标，即包括0和size-1
     * @param smoothScroll 切换页面是否附带动画
     */
    fun setRealCurrentItem(index: Int, smoothScroll: Boolean) {
        mViewPager2.setCurrentItem(index, smoothScroll)
        mIndicator.onPageSelected(getCurrentItem())
    }

    /**
     * 获取当前页面下标（虚假的下标，即不包括0和size-1）
     */
    fun getCurrentItem(): Int =
        if (getRealCurrentItem() >= 1) getRealCurrentItem() - 1 else getRealCurrentItem()

    /**
     * 获取当前页面下标（真实的下标，即包括0和size-1）
     */
    fun getRealCurrentItem(): Int = mViewPager2.currentItem

    /**
     * 设置相邻两页之间的间隔
     * @param pageMargin 相邻两页之间的间隔
     */
    fun setPageMargin(pageMargin: Int) {
        mPageMargin = pageMargin
        mViewPager2.setPageTransformer(MarginPageTransformer(mPageMargin))
    }

    /**
     * 开始自动轮播
     * @param interval 轮播间隔，单位：毫秒
     */
    fun startPlay(interval: Long) {
        if (!autoPlay && mViewPager2.adapter?.itemCount ?: 0 > 1) {
            mAutoPlayInterval = interval
            mHandler.postDelayed(mAutoPlayRunnable, mAutoPlayInterval)
//            mTimer.schedule(mAutoPlayTask, mAutoPlayInterval, mAutoPlayInterval)
            autoPlay = true
        }
    }

    /**
     * 停止自动轮播
     */
    fun stopPlay() {
        if (autoPlay) {
//            mTimer.cancel()
            autoPlay = false
            mHandler.removeCallbacks(mAutoPlayRunnable)
        }
    }

    /*
     * 是否在自动轮播
     */
    fun isAutoPlay(): Boolean = autoPlay

    /**
     * 切换到下一页（不会循环）
     */
    private fun nextPage() {
        if (mViewPager2.adapter?.itemCount ?: 0 > 1 && autoPlay) {
            setCurrentItem(getCurrentItem() + 1, true)
        }
    }

    fun setBannerPadding(
        bannerPaddingStart: Int,
        bannerPaddingEnd: Int,
        bannerPaddingTop: Int,
        bannerPaddingBottom: Int
    ) {
        mBannerPaddingStart = bannerPaddingStart
        mBannerPaddingEnd = bannerPaddingEnd
        mBannerPaddingTop = bannerPaddingTop
        mBannerPaddingBottom = bannerPaddingBottom
        mViewPager2.setPadding(
            mBannerPaddingStart, mBannerPaddingTop,
            mBannerPaddingEnd, mBannerPaddingBottom
        )
        // 需要显示多个Item
        mViewPager2.clipToPadding = false
    }

    /**
     * 处理滑动冲突
     */
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!mViewPager2.isUserInputEnabled || (mViewPager2.adapter?.itemCount ?: 0) <= 1) {
            return super.onInterceptTouchEvent(event)
        }
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
                if (mViewPager2.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                    if (abs(distanceX) < 0.0001) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        if (distanceX > distanceY) {
                            parent.requestDisallowInterceptTouchEvent(true)
                        } else {
                            parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
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

    private inner class CycleOnPageChangeCallback : OnPageChangeCallback() {
        var stateChangedItemRealPosition = -1

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            mIndicator.onPageScrolled(
                getPosition(position, getAdapter()?.itemCount ?: 0),
                positionOffset,
                positionOffsetPixels
            )
        }

        override fun onPageSelected(position: Int) {
            val maxPosition = (getAdapter()?.itemCount ?: 1) - 1
            if (position == 0) {
                stateChangedItemRealPosition = if (maxPosition == 0) 0 else maxPosition - 1
            } else if (position == maxPosition) {
                stateChangedItemRealPosition = 1
            }

            mIndicator.onPageSelected(getCurrentItem())
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                if (stateChangedItemRealPosition != -1) {
                    setRealCurrentItem(stateChangedItemRealPosition, false)
                    stateChangedItemRealPosition = -1
                }
            }

            mIndicator.onPageScrollStateChanged(getPosition(state, getAdapter()?.itemCount ?: 0))
        }
    }
}