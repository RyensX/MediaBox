package com.skyd.imomoe.view.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.LicenseBean
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.util.AnimeCover7ViewHolder
import com.skyd.imomoe.util.License1ViewHolder
import com.skyd.imomoe.util.LicenseHeader1ViewHolder
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.invisible
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.Util.visible
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getItemViewType
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getViewHolder
import com.skyd.imomoe.view.activity.AnimeDownloadActivity
import com.skyd.imomoe.view.activity.LicenseActivity

class AnimeDownloadAdapter(
    val activity: AnimeDownloadActivity,
    private val dataList: List<AnimeCoverBean>
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
            is AnimeCover7ViewHolder -> {
                holder.tvAnimeCover7Title.isFocused = true
                holder.tvAnimeCover7Title.text = item.title
                holder.tvAnimeCover7Size.isFocused = true
                holder.tvAnimeCover7Size.text = item.size
                if (item.actionUrl.startsWith(Const.ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE)) {
                    holder.tvAnimeCover7Episodes.text = item.episodeCount
                    holder.tvAnimeCover7Episodes.visible()
                } else {
                    holder.tvAnimeCover7Episodes.invisible()
                }
                holder.itemView.setOnClickListener {
                    process(activity, item.actionUrl)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}