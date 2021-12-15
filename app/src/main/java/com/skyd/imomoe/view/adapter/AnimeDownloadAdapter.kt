package com.skyd.imomoe.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.util.AnimeCover7ViewHolder
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.invisible
import com.skyd.imomoe.util.visible
import com.skyd.imomoe.view.activity.AnimeDownloadActivity

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
                if (item.path == 1) {
                    holder.tvAnimeCover7OldPath.text = activity.getString(R.string.old_path)
                    holder.tvAnimeCover7OldPath.visible()
                } else {
                    holder.tvAnimeCover7OldPath.gone()
                }
                if (item.actionUrl.startsWith(Const.ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE)) {
                    holder.tvAnimeCover7Episodes.text = item.episodeCount
                    holder.tvAnimeCover7Episodes.visible()
                } else {
                    holder.tvAnimeCover7Episodes.invisible()
                }
                holder.itemView.setOnClickListener {
                    process(activity, item.actionUrl + "/" + item.path)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}