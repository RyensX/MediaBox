package com.skyd.imomoe.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.MoreBean
import com.skyd.imomoe.util.More1ViewHolder
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.clickScale
import com.skyd.imomoe.view.fragment.MoreFragment

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
                    process(fragment, item.actionUrl)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}