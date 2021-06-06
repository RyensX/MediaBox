package com.skyd.imomoe.view.adapter.spansize

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.config.Const.ViewHolderTypeInt

class AnimeDetailSpanSize(val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
    GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return when(adapter.getItemViewType(position)) {
            ViewHolderTypeInt.HEADER_1 -> 4
            ViewHolderTypeInt.GRID_RECYCLER_VIEW_1 -> 4
            ViewHolderTypeInt.HORIZONTAL_RECYCLER_VIEW_1 -> 4
            ViewHolderTypeInt.ANIME_DESCRIBE_1 -> 4
            ViewHolderTypeInt.ANIME_INFO_1 -> 4
            else -> 1
        }
    }
}