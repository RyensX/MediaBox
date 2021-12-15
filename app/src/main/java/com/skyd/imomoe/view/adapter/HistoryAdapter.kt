package com.skyd.imomoe.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.HistoryBean
import com.skyd.imomoe.util.AnimeCover9ViewHolder
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.Util.time2Now
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.activity.HistoryActivity

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
                    url = item.cover.url,
                    referer = item.cover.referer
                )
                holder.tvAnimeCover9Title.text = item.animeTitle
                holder.tvAnimeCover9Episodes.text = item.lastEpisode
                holder.tvAnimeCover9Time.text = time2Now(item.time)
                holder.tvAnimeCover9DetailPage.setOnClickListener {
                    process(activity, item.animeUrl, item.animeUrl)
                }
                holder.ivAnimeCover9Delete.setOnClickListener {
                    activity.deleteHistory(item)
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