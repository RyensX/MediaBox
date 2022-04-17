package com.su.mediabox.view.adapter.type

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 只有使用[GridLayoutManager]才有效
 *
 * 根据spanSize和spanCount自动计算每个item需要附加的间隔
 *
 * 注意：边距和间隔不相等时可能会导致右侧View空间被挤占
 */
class DynamicGridItemDecoration(
    var spacing: Int,
    var leftEdge: Int = spacing,
    var rightEdge: Int = spacing
) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager

        if (layoutManager is GridLayoutManager) {

            val position = parent.getChildAdapterPosition(view)

            val lp = view.layoutParams as GridLayoutManager.LayoutParams
            val spanIndex = lp.spanIndex
            val spanSize = lp.spanSize

            val spanCount = layoutManager.spanCount / spanSize

            val column = spanIndex / spanSize

            //Log.d(
            //    "布局",
            //    "spanIndex=$spanIndex spanSize=$spanSize spanCount=$spanCount column=$column"
            //)

            outRect.left = if (column == 0) leftEdge else
                spacing - column * spacing / spanCount
            outRect.right = if (column == spanCount - 1) rightEdge else
                (column + 1) * spacing / spanCount

            if (position < spanCount)
                outRect.top = spacing

            outRect.bottom = spacing
        } else
            super.getItemOffsets(outRect, view, parent, state)
    }
}