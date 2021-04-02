package com.skyd.imomoe.view.component

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.skyd.imomoe.R
import kotlin.math.max


class FlowLayout : ViewGroup {
    companion object {
        private const val DEFAULT_ROW_SPACING = 0
        private const val DEFAULT_CHILD_SPACING = 0
    }

    private var mRowSpacing: Int = DEFAULT_ROW_SPACING
    private var mChildSpacing: Int = DEFAULT_CHILD_SPACING

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        val a: TypedArray =
            context!!.theme.obtainStyledAttributes(attrs, R.styleable.FlowLayout, 0, 0)
        for (i in 0 until a.indexCount) {
            when (a.getIndex(i)) {
                R.styleable.FlowLayout_rowSpacing -> mRowSpacing =
                    a.getDimensionPixelSize(
                        R.styleable.FlowLayout_rowSpacing,
                        DEFAULT_ROW_SPACING
                    )
                R.styleable.FlowLayout_childSpacing -> mChildSpacing =
                    a.getDimensionPixelSize(
                        R.styleable.FlowLayout_childSpacing,
                        DEFAULT_CHILD_SPACING
                    )
            }
        }
        a.recycle()
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        if (childCount <= 0) return

        val width = r - l
        var childLeft = paddingStart
        var childTop = paddingTop
        var eachLineHeight = 0
        var isFirstLeftView = true
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView.visibility == View.GONE) continue
            val childWidth = childView.measuredWidth
            val childHeight = childView.measuredHeight
            eachLineHeight = max(childHeight, eachLineHeight)
            if (childLeft + childWidth + paddingEnd > width && !isFirstLeftView) {
                childLeft = paddingStart
                childTop += mRowSpacing + eachLineHeight
                eachLineHeight = childHeight
            }
            childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
            childLeft += childWidth + mChildSpacing
            isFirstLeftView = false
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        if (heightMode == MeasureSpec.EXACTLY && widthMode == MeasureSpec.EXACTLY)
            setMeasuredDimension(widthSize, heightSize)
        var height = 0
        var width = 0
        var eachLineHeight = 0
        var childLeft = paddingStart
        var childTop = paddingTop
        var rowWidth = 0

        var isFirstLeftView = true
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            val childHeight = childView.measuredHeight
            val childWidth = childView.measuredWidth
            if (childWidth + childLeft + paddingEnd > widthSize && !isFirstLeftView) {
                childLeft = paddingStart
                childTop += mRowSpacing + childHeight
                eachLineHeight = childHeight
                width = max(width, rowWidth - mChildSpacing)
                rowWidth = childWidth + mChildSpacing
            } else {
                eachLineHeight = max(childHeight, eachLineHeight)
                childLeft += childWidth + mChildSpacing
                rowWidth += childWidth + mChildSpacing
            }
            isFirstLeftView = false
        }
        width = max(width, rowWidth - mChildSpacing)
        height += childTop + eachLineHeight + paddingBottom
        when {
            heightMode == MeasureSpec.EXACTLY -> setMeasuredDimension(width, heightSize)
            widthMode == MeasureSpec.EXACTLY -> setMeasuredDimension(widthSize, height)
            else -> setMeasuredDimension(width, height)
        }
    }
}