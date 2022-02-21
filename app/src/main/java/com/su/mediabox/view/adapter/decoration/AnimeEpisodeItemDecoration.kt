package com.su.mediabox.view.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil

class AnimeEpisodeItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val childPosition = parent.getChildAdapterPosition(view)
        val childCount = parent.adapter?.itemCount ?: 0
        when (childPosition % 3) {
            // 一共15*2px的间距，3个item，2个空白间距
            // 每个item空白区域总宽度要一样才能让imageView图片宽度一样
            0 -> {
                outRect.left = 0
                outRect.right = 18
            }
            1 -> {
                outRect.left = 9
                outRect.right = 9
            }
            2 -> {
                outRect.left = 18
                outRect.right = 0
            }
        }
        when (ceil((childPosition + 1) / 3.0).toInt()) {
            ceil(childCount / 3.0).toInt() -> {
                outRect.top = 0
                outRect.bottom = 0
            }
            else -> {
                outRect.top = 0
                outRect.bottom = 18
            }
        }
    }
}