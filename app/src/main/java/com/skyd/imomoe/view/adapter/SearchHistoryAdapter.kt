package com.skyd.imomoe.view.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.util.SearchHistory1ViewHolder
import com.skyd.imomoe.util.SearchHistoryHeader1ViewHolder
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getItemViewType
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getViewHolder
import com.skyd.imomoe.view.activity.SearchActivity

class SearchHistoryAdapter(
    val activity: SearchActivity,
    private val dataList: List<SearchHistoryBean>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = getItemViewType(dataList[position])

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        getViewHolder(parent, viewType)

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]

        when (holder) {
            is SearchHistoryHeader1ViewHolder -> {
                holder.tvSearchHistoryHeader1Title.text = item.title
            }
            is SearchHistory1ViewHolder -> {
                holder.tvSearchHistory1Title.text = item.title
                holder.ivSearchHistory1Delete.setOnClickListener {
                    activity.deleteSearchHistory(position)
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