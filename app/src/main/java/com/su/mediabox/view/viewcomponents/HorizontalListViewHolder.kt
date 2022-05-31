package com.su.mediabox.view.viewcomponents

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.App
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.data.HorizontalListData
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.showToast
import com.su.mediabox.view.adapter.type.*

/**
 * 水平列表视图组件
 */
class HorizontalListViewHolder(parent: ViewGroup) :
    TypeViewHolder<HorizontalListData>(RecyclerView(parent.context)) {

    private val list = itemView as RecyclerView
    private var data: HorizontalListData? = null

    init {
        list.linear(RecyclerView.HORIZONTAL).initTypeList(useSharedRecycledViewPool = false) {
            vHCreateDSL<TypeViewHolder<Any>> {
                data?.also {
                    itemView.layoutParams.apply {
                        width = it.itemWidthLimit
                    }
                }
            }
        }
    }

    override fun onBind(data: HorizontalListData) {
        super.onBind(data)
        this.data = data
        list.submitList(data.listData)
    }
}