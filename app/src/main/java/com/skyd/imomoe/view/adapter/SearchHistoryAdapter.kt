package com.skyd.imomoe.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.util.SearchHistory1ViewHolder
import com.skyd.imomoe.util.SearchHistoryHeader1ViewHolder
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.activity.SearchActivity

class SearchHistoryAdapter(
    val activity: SearchActivity,
    private val dataList: List<SearchHistoryBean>
) : BaseRvAdapter(dataList) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is SearchHistoryHeader1ViewHolder -> {
                holder.tvSearchHistoryHeader1Title.text = item.title
            }
            is SearchHistory1ViewHolder -> {
                holder.tvSearchHistory1Title.text = item.title
                holder.ivSearchHistory1Delete.setOnClickListener {
                    // 用holder.bindingAdapterPosition代替position，因为在removed后position会变
                    activity.deleteSearchHistory(holder.bindingAdapterPosition)
                }
                holder.itemView.setOnClickListener {
                    activity.search(item.title)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}