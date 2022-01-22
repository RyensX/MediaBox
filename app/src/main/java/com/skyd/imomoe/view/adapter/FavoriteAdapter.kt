package com.skyd.imomoe.view.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.util.AnimeCover8ViewHolder
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.visible
import com.skyd.imomoe.view.activity.FavoriteActivity

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
                            process(
                                activity,
                                item.lastEpisodeUrl + item.animeUrl,
                                item.lastEpisodeUrl ?: ""
                            )
                        else
                            process(activity, item.animeUrl, item.animeUrl)
                    }
                    //长度跳转详情页
                    it.itemView.setOnLongClickListener { _ ->
                        val item = dataList[it.bindingAdapterPosition]
                        process(activity, item.animeUrl, item.animeUrl)
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
                    url = item.cover.url,
                    referer = item.cover.referer
                )
                holder.tvAnimeCover8Title.text = item.animeTitle
                holder.tvAnimeCover8Episodes.text = item.lastEpisode?.let { "已看到 $it" } ?: "未看"
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}