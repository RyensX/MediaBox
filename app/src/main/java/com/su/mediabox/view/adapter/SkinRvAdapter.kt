package com.su.mediabox.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.su.skin.SkinManager
import com.su.mediabox.util.ViewHolderUtil.Companion.getViewHolder

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