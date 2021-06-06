package com.skyd.imomoe.view.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AnimeCoverItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val childPosition = parent.getChildAdapterPosition(view)
        when (childPosition % 4) {
            // 一共15*3px的间距，四个item，三个空白间距
            // 每个item空白区域总宽度要一样才能让imageView图片宽度一样
            0 -> {
                outRect.left = 0
                outRect.right = 10
            }
            1 -> {
                outRect.left = 5
                outRect.right = (15 / 2.0).toInt()
            }
            2 -> {
                outRect.left = (15 / 2.0).toInt()
                outRect.right = 5
            }
            3 -> {
                outRect.left = 10
                outRect.right = 0
            }
        }
    }
}