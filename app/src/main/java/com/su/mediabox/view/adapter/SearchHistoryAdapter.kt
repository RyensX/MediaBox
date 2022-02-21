package com.su.mediabox.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.bean.SearchHistoryBean
import com.su.mediabox.util.SearchHistory1ViewHolder
import com.su.mediabox.util.SearchHistoryHeader1ViewHolder
import com.su.mediabox.util.showToast
import com.su.mediabox.view.activity.SearchActivity

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