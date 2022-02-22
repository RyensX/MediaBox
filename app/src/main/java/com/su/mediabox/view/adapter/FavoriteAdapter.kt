package com.su.mediabox.view.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager.process
import com.su.mediabox.R
import com.su.mediabox.bean.FavoriteAnimeBean
import com.su.mediabox.config.Api
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.Text.buildRouteActionUrl
import com.su.mediabox.util.AnimeCover8ViewHolder
import com.su.mediabox.util.showToast
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.view.activity.FavoriteActivity

class FavoriteAdapter(
    val activity: FavoriteActivity,
    private val dataList: List<FavoriteAnimeBean>
) : BaseRvAdapter(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, viewType).also {
            when (it) {
                is AnimeCover8ViewHolder -> {
                    //点击
                    it.itemView.setOnClickListener { _ ->
                        val item = dataList[it.bindingAdapterPosition]
                        if (item.lastEpisodeUrl != null)
                            process(buildRouteActionUrl(Constant.ActionUrl.ANIME_PLAY,item.lastEpisodeUrl!!,item.cover,item.animeUrl))
                        else
                            process(buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL,item.animeUrl))
                    }
                    //长度跳转详情页
                    it.itemView.setOnLongClickListener { _ ->
                        val item = dataList[it.bindingAdapterPosition]
                        process(buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL,item.animeUrl))
                        true
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is AnimeCover8ViewHolder -> {
                holder.ivAnimeCover8Cover.loadImage(
                    url = item.cover,
                    referer = Api.refererProcessor?.processor(item.cover)
                )
                holder.tvAnimeCover8Title.text = item.animeTitle
                holder.tvAnimeCover8Episodes.text = item.lastEpisode?.let {
                    activity.getString(R.string.already_seen_episode_x, it)
                } ?: activity.getString(R.string.have_not_watched_this_anime)
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}