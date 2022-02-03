package com.skyd.imomoe.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.skyd.skin.SkinManager
import com.skyd.imomoe.config.Const
import com.su.mediabox.plugin.standard.been.BaseBean

abstract class BaseRvAdapter(
    private val dataList: List<BaseBean>
) : SkinRvAdapter() {

    override fun getItemViewType(position: Int): Int {
        return if (position < dataList.size) com.skyd.imomoe.util.ViewHolderUtil.getItemViewType(dataList[position])
        else Const.ViewHolderTypeInt.UNKNOWN
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        SkinManager.applyViews(holder.itemView)
    }

    override fun getItemCount(): Int = dataList.size
}