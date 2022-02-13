package com.su.mediabox.view.adapter.spansize

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.config.Const.ViewHolderTypeInt

class PlaySpanSize(val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
    GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return when(adapter.getItemViewType(position)) {
            ViewHolderTypeInt.HEADER_1 -> 4
            ViewHolderTypeInt.GRID_RECYCLER_VIEW_1 -> 4
            ViewHolderTypeInt.ANIME_EPISODE_FLOW_LAYOUT_1 -> 4
            ViewHolderTypeInt.HORIZONTAL_RECYCLER_VIEW_1 -> 4
            else -> 1
        }
    }
}