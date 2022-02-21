package com.su.mediabox.view.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.util.Util.dp


class SkinItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
        val spanSize = layoutParams.spanSize
        val spanIndex = layoutParams.spanIndex
        /**
         * 不相等时说明是Grid形式显示的
         * 然后判断是左边还有右边显示
         */
        if (spanSize == 1) {
            when (spanIndex) {
                // 16+x=2y
                // x+y=14
                // x=4 y=10
                0 -> {
                    outRect.left = 16.dp
                    outRect.right = 4.dp    // 4
                }
                1 -> {
                    outRect.left = 10.dp    // 10
                    outRect.right = 10.dp   // 10
                }
                2 -> {
                    outRect.left = 4.dp     // 4
                    outRect.right = 16.dp
                }
            }
        }
    }
}