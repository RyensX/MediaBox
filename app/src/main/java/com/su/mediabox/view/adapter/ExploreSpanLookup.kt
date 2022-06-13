package com.su.mediabox.view.adapter

import androidx.recyclerview.widget.GridLayoutManager
import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.model.PluginManageModel

class ExploreSpanLookup(val getData: (Int) -> Any?) : GridLayoutManager.SpanSizeLookup() {

    companion object {
        const val SPAN_COUNT = 12
    }

    override fun getSpanSize(position: Int): Int = when (getData(position)) {
        is PluginManageModel -> SPAN_COUNT
        is MediaFavorite -> SPAN_COUNT / 4
        else -> SPAN_COUNT
    }
}