package com.su.mediabox.view.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.util.Util.dp


class AnimeShowItemDecoration : RecyclerView.ItemDecoration() {
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
         * 然后判断是左边还有右边显示，分别设置间距为15
         */
        if (spanSize == 1) {
            when (spanIndex) {
                // 16+x=y+5/2
                // x+y=5
                // x=-4.25 y=9.25
                0 -> {
                    outRect.left = 16.dp
                    outRect.right = -(4.25f.dp).toInt()   // -4.25
                }
                1 -> {
                    outRect.left = 9.25f.dp.toInt()   // 9.25
                    outRect.right = 2.5f.dp.toInt()   // 测试机5dp==15px
                }
                2 -> {
                    outRect.left = 2.5f.dp.toInt()
                    outRect.right = 9.25f.dp.toInt()  // 9.25
                }
                3 -> {
                    outRect.left = -(4.25f.dp).toInt()    // -4.25
                    outRect.right = 16.dp
                }
            }
        }
    }
}