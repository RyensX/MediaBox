package com.su.mediabox.util

import android.content.Context
import android.view.View
import android.view.View.MeasureSpec
import android.widget.FrameLayout
import android.widget.ListAdapter

fun ListAdapter.measureWidth(context: Context): Int {
    val parent = FrameLayout(context)

    val widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    val heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

    var itemView: View? = null
    var maxWidth = 0
    var itemType = 0

    for (i in 0 until count) {
        val positionType = getItemViewType(i)
        if (positionType != itemType) {
            itemType = positionType
            itemView = null
        }

        itemView = getView(i, itemView, parent)
        itemView.measure(widthMeasureSpec, heightMeasureSpec)

        val itemWidth: Int = itemView.measuredWidth

        if (itemWidth > maxWidth) {
            maxWidth = itemWidth
        }
    }

    return maxWidth
}