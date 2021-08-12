package com.skyd.imomoe.view.component.bannerview.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.widget.RelativeLayout
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.dp
import com.skyd.imomoe.util.Util.getResColor

/**
 * Created by Sky_D on 2021-02-08.
 */
class DotIndicator : View, Indicator {
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)        //抗锯齿
    private var mRadius: Float = 3f.dp
    private var mDotsPadding = 3.dp
    private var mSelectedColor: Int = context.getResColor(R.color.main_color_2_skin)
    private var mUnSelectedColor: Int = context.getResColor(R.color.foreground_white_skin)
    private var mDotsCount = 0
    private var mCurrentPosition = 0

    private val mMarginStart = 0
    private val mMarginEnd = 10.dp
    private val mMarginTop = 10.dp
    private val mMarginBottom = 0

    constructor(context: Context) : super(context) {
        val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
        layoutParams.leftMargin = mMarginStart
        layoutParams.rightMargin = mMarginEnd
        layoutParams.topMargin = mMarginTop
        layoutParams.bottomMargin = mMarginBottom
        setLayoutParams(layoutParams)
    }

    override fun getView(): View {
        return this
    }

    override fun onChanged(itemCount: Int, currentPosition: Int) {
        mDotsCount = itemCount
        mCurrentPosition = currentPosition
        requestLayout()
        postInvalidate()
    }

    override fun onPageSelected(position: Int) {
        mCurrentPosition = position
        requestLayout()
        postInvalidate()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mDotsCount > 1) {
            val width = (mDotsCount * mRadius * 2 + (mDotsCount - 1) * mDotsPadding).toInt()
            val height = mRadius.toInt() * 2
            setMeasuredDimension(width, height)
            return
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (mDotsCount > 1) {
            var cx: Float = mRadius
            for (i in 0 until mDotsCount) {
                mPaint.color = if (mCurrentPosition == i) mSelectedColor else mUnSelectedColor
                canvas?.drawCircle(cx, mRadius, mRadius, mPaint)
                cx += mRadius * 2 + mDotsPadding
            }
        }
    }
}