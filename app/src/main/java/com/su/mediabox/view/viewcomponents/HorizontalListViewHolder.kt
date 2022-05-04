package com.su.mediabox.view.viewcomponents

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.pluginapi.v2.been.HorizontalListData
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.*

/**
 * 水平列表视图组件
 */
class HorizontalListViewHolder(parent: ViewGroup) :
    TypeViewHolder<HorizontalListData>(RecyclerView(parent.context)) {

    private val list = itemView as RecyclerView

    init {
        list.linear(RecyclerView.HORIZONTAL).initTypeList { }
        setOnClickListener(itemView) {
            list.typeAdapter().getData<BaseData>(it)?.action?.go(itemView.context)
        }
    }

    override fun onBind(data: HorizontalListData) {
        super.onBind(data)
        list.submitList(data.listData)
    }
}