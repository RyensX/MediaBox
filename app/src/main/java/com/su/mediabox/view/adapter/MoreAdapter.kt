package com.su.mediabox.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.App
import com.su.mediabox.PluginManager.process
import com.su.mediabox.R
import com.su.mediabox.bean.MoreBean
import com.su.mediabox.util.More1ViewHolder
import com.su.mediabox.util.Util.getResDrawable
import com.su.mediabox.util.showToast
import com.su.mediabox.util.clickScale
import com.su.mediabox.view.fragment.MoreFragment

class MoreAdapter(
    val fragment: MoreFragment,
    private val dataList: List<MoreBean>
) : BaseRvAdapter(dataList) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is More1ViewHolder -> {
                holder.ivMore1.setImageDrawable(getResDrawable(item.image))
                holder.tvMore1.text = item.title
                holder.itemView.setOnClickListener {
                    it.clickScale()
                    process(item.actionUrl)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}