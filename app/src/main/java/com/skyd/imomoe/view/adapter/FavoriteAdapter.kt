package com.skyd.imomoe.view.adapter

import android.view.View
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
                if (item.lastEpisode == null) {
                    holder.tvAnimeCover8Episodes.gone()
                } else {
                    holder.tvAnimeCover8Episodes.visible()
                    holder.tvAnimeCover8Episodes.text = item.lastEpisode
                }
                holder.itemView.setOnClickListener {
                    if (item.lastEpisodeUrl != null)
                        process(
                            activity,
                            item.lastEpisodeUrl + item.animeUrl,
                            item.lastEpisodeUrl ?: ""
                        )
                    else
                        process(activity, item.animeUrl, item.animeUrl)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}