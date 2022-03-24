package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.su.mediabox.databinding.ViewComponentGridBinding
import com.su.mediabox.pluginapi.v2.been.GridData
import com.su.mediabox.pluginapi.v2.been.GridItemData
import com.su.mediabox.view.adapter.decoration.AnimeCoverItemDecoration
import com.su.mediabox.view.adapter.type.TypeViewHolder
import com.su.mediabox.view.adapter.type.grid
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.typeAdapter

class GridViewHolder private constructor(private val binding: ViewComponentGridBinding) :
    TypeViewHolder<GridData>(binding.root) {

    private var data: GridData? = null

    constructor(parent: ViewGroup) : this(
        ViewComponentGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        binding.root
            .grid(4)
            .apply {
                addItemDecoration(AnimeCoverItemDecoration())
                //动态大小
                (layoutManager as GridLayoutManager).spanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int) =
                            data?.gridItemList?.get(position)?.spanSize
                                ?: GridItemData.DEFAULT_SPAN_SIZE
                    }
            }
            .initTypeList { }
    }

    override fun onBind(data: GridData) {
        this.data = data
        binding.root.apply {
            (layoutManager as GridLayoutManager).spanCount = data.spanCount
        }.typeAdapter().submitList(data.gridItemList)
    }

}