package com.su.mediabox.view.component.bannerview.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_ID
import com.su.mediabox.view.adapter.SkinRvAdapter
import com.su.mediabox.view.component.bannerview.BannerUtil.getPosition

/**
 * Created by Sky_D on 2021-02-08.
 */
abstract class CycleBannerAdapter : SkinRvAdapter() {
    final override fun getItemViewType(position: Int): Int =
        getItemType(getPosition(position, getCount()))

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        onBind(holder, getPosition(position, getCount()))
    }

    /**
     * 获取真实的页面个数（加2后的）
     */
    final override fun getItemCount(): Int = if (getCount() > 1) getCount() + 2 else getCount()

    final override fun getItemId(position: Int): Long =
        getBannerItemId(getPosition(position, getCount()))

    /**
     * @param position 虚假的Item位置
     * 对应onBindViewHolder
     */
    abstract fun onBind(holder: RecyclerView.ViewHolder, position: Int)

    /**
     * 获取虚假的页面个数（没有加2的）
     * 对应getItemCount
     */
    abstract fun getCount(): Int

    /**
     * 对应getItemViewType
     */
    open fun getItemType(position: Int): Int = 0

    /**
     * 对应getItemId
     */
    open fun getBannerItemId(position: Int): Long {
        return NO_ID
    }
}