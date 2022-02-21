package com.su.mediabox.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.App
import com.su.mediabox.PluginManager.process
import com.su.mediabox.R
import com.su.mediabox.bean.HistoryBean
import com.su.mediabox.config.Api
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.Text
import com.su.mediabox.pluginapi.Text.buildRouteActionUrl
import com.su.mediabox.util.AnimeCover9ViewHolder
import com.su.mediabox.util.showToast
import com.su.mediabox.util.Util.time2Now
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.view.activity.HistoryActivity

class HistoryAdapter(
    val activity: HistoryActivity,
    private val dataList: List<HistoryBean>
) : BaseRvAdapter(dataList) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is AnimeCover9ViewHolder -> {
                holder.ivAnimeCover9Cover.loadImage(
                    url = item.cover,
                    referer = Api.refererProcessor?.processor(item.cover)
                )
                holder.tvAnimeCover9Title.text = item.animeTitle
                holder.tvAnimeCover9Episodes.text = item.lastEpisode
                holder.tvAnimeCover9Time.text = time2Now(item.time)
                holder.tvAnimeCover9DetailPage.setOnClickListener {
                    process(buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL, item.animeUrl))
                }
                holder.ivAnimeCover9Delete.setOnClickListener {
                    activity.deleteHistory(item)
                }
                holder.itemView.setOnClickListener {
                    process(
                        buildRouteActionUrl(
                            Constant.ActionUrl.ANIME_PLAY,
                            item.lastEpisodeUrl!!,
                            item.cover,
                            item.animeUrl
                        )
                    )
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}