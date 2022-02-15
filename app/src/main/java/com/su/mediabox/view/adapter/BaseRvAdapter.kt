package com.su.mediabox.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.su.skin.SkinManager
import com.su.mediabox.config.Const
import com.su.mediabox.pluginapi.been.BaseBean

abstract class BaseRvAdapter(
    private val dataList: List<BaseBean>
) : SkinRvAdapter() {

    override fun getItemViewType(position: Int): Int {
        return if (position < dataList.size) com.su.mediabox.util.ViewHolderUtil.getItemViewType(dataList[position])
        else Const.ViewHolderTypeInt.UNKNOWN
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        SkinManager.applyViews(holder.itemView)
    }

    override fun getItemCount(): Int = dataList.size
}