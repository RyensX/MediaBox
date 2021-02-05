package com.skyd.imomoe.view.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.util.AnimeCover3ViewHolder

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
            0 -> {
                outRect.left = 0
                outRect.right = 10
            }
            1, 2 -> {
                outRect.left = 10
                outRect.right = 10
            }
            3 -> {
                outRect.left = 10
                outRect.right = 0
            }
        }
    }
}