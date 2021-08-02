package com.skyd.imomoe.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.skin.SkinManager
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getViewHolder

abstract class SkinRvAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getViewHolder(parent, viewType).apply {
            SkinManager.setSkin(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        SkinManager.applyViews(holder.itemView)
    }
}