package com.su.mediabox.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.bean.DataSourceFileBean
import com.su.mediabox.util.*
import com.su.mediabox.view.activity.ConfigDataSourceActivity

class ConfigDataSourceAdapter(
    val activity: ConfigDataSourceActivity,
    private val dataList: List<DataSourceFileBean>
) : BaseRvAdapter(dataList) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is DataSource1ViewHolder -> {
                holder.tvDataSource1Name.text = item.file.name
                holder.tvDataSource1Size.text = item.file.formatSize()
                if (item.selected) holder.ivDataSource1Selected.visible()
                else holder.ivDataSource1Selected.gone()
                holder.itemView.setOnClickListener {
                    if (item.selected) {
                        activity.getString(R.string.the_data_source_is_using_now).showSnackbar(activity)
                    } else {
                        activity.setDataSource(item.file.name)
                    }
                }
                holder.itemView.setOnLongClickListener {
                    activity.deleteDataSource(item)
                    true
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}