package com.su.mediabox.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager.process
import com.su.mediabox.R
import com.su.mediabox.util.AnimeCover7ViewHolder
import com.su.mediabox.pluginapi.Constant.ActionUrl
import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.util.showToast
import com.su.mediabox.util.invisible
import com.su.mediabox.util.visible
import com.su.mediabox.view.activity.AnimeDownloadActivity

class AnimeDownloadAdapter(
    val activity: AnimeDownloadActivity,
    private val dataList: List<AnimeCoverBean>
) : BaseRvAdapter(dataList) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is AnimeCover7ViewHolder -> {
                holder.tvAnimeCover7Title.isFocused = true
                holder.tvAnimeCover7Title.text = item.title
                holder.tvAnimeCover7Size.isFocused = true
                holder.tvAnimeCover7Size.text = item.size
                if (item.actionUrl.startsWith(ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE)) {
                    holder.tvAnimeCover7Episodes.text = item.episodeCount
                    holder.tvAnimeCover7Episodes.visible()
                } else {
                    holder.tvAnimeCover7Episodes.invisible()
                }
                holder.itemView.setOnClickListener {
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