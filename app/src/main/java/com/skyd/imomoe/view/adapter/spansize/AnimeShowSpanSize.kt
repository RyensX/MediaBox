package com.skyd.imomoe.view.adapter.spansize

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.config.Const.ViewHolderTypeInt

class AnimeShowSpanSize(val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
    GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return when (adapter.getItemViewType(position)) {
            ViewHolderTypeInt.GRID_RECYCLER_VIEW_1 -> 4
            ViewHolderTypeInt.HEADER_1 -> 4
            ViewHolderTypeInt.BANNER_1 -> 4
            ViewHolderTypeInt.ANIME_COVER_3 -> 4
            ViewHolderTypeInt.ANIME_COVER_5 -> 4
            else -> 1
        }
    }
}