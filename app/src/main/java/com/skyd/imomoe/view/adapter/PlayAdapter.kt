package com.skyd.imomoe.view.adapter

import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.IAnimeDetailBean
import com.skyd.imomoe.util.*
import com.skyd.imomoe.util.Util.dp
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.Util.sp
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.activity.PlayActivity
import com.skyd.imomoe.view.adapter.decoration.AnimeCoverItemDecoration

class PlayAdapter(
    val activity: PlayActivity,
    private val dataList: List<IAnimeDetailBean>
) : BaseRvAdapter(dataList) {

    private val gridItemDecoration = AnimeCoverItemDecoration()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when {
            holder is GridRecyclerView1ViewHolder -> {
                item.animeCoverList?.let {
                    val layoutManager = GridLayoutManager(activity, 4)
                    holder.rvGridRecyclerView1.post {
                        holder.rvGridRecyclerView1.setPadding(16.dp, 0, 16.dp, 0)
                    }
                    holder.rvGridRecyclerView1.removeItemDecoration(gridItemDecoration)
                    holder.rvGridRecyclerView1.addItemDecoration(gridItemDecoration)
                    holder.rvGridRecyclerView1.layoutManager = layoutManager
                    holder.rvGridRecyclerView1.adapter =
                        AnimeShowAdapter.GridRecyclerView1Adapter(activity, it)
                }
            }
            holder is Header1ViewHolder -> {
                holder.tvHeader1Title.text = item.title
            }
            holder is AnimeCover2ViewHolder -> {
                holder.tvAnimeCover1Title.text = item.title
                holder.tvAnimeCover1Episode.gone()
                holder.itemView.setOnClickListener {
                    process(activity, item.actionUrl)
                }
            }
            holder is AnimeEpisodeFlowLayout1ViewHolder -> {
                item.episodeList?.let {
                    holder.flAnimeEpisodeFlowLayout1.removeAllViews()
                    for (i in it.indices) {
                        val tvFlowLayout: TextView = activity.layoutInflater
                            .inflate(
                                R.layout.item_anime_episode_1,
                                holder.flAnimeEpisodeFlowLayout1,
                                false
                            ) as TextView
                        tvFlowLayout.text = it[i].title
                        tvFlowLayout.setOnClickListener { _ ->
                            activity.startPlay(it[i].actionUrl, position, it[i].title)
                        }
                        holder.flAnimeEpisodeFlowLayout1.addView(tvFlowLayout)
                    }
                }
            }
            holder is HorizontalRecyclerView1ViewHolder -> {
                item.episodeList?.let {
                    val dialog = activity.getSheetDialog("play")
                    if (holder.rvHorizontalRecyclerView1.adapter == null) {
                        holder.rvHorizontalRecyclerView1.adapter =
                            PlayActivity.EpisodeRecyclerViewAdapter(
                                activity, it, dialog, 0, "play"
                            )
                    } else holder.rvHorizontalRecyclerView1.adapter?.notifyDataSetChanged()
                    holder.ivHorizontalRecyclerView1More.setImageDrawable(getResDrawable(R.drawable.ic_keyboard_arrow_down_main_color_2_24_skin))
                    holder.ivHorizontalRecyclerView1More.imageTintList =
                        ColorStateList.valueOf(activity.getResColor(R.color.foreground_main_color_2_skin))
                    holder.ivHorizontalRecyclerView1More.setOnClickListener { dialog.show() }
                }
            }
            holder is AnimeCover1ViewHolder && item is AnimeCoverBean -> {
                holder.ivAnimeCover1Cover.setTag(R.id.image_view_tag, item.cover?.url)
                if (holder.ivAnimeCover1Cover.getTag(R.id.image_view_tag) == item.cover?.url) {
                    holder.ivAnimeCover1Cover.loadImage(
                        item.cover?.url ?: "",
                        referer = item.cover?.referer
                    )
                }
                holder.tvAnimeCover1Title.text = item.title
                if (item.episode.isBlank()) {
                    holder.tvAnimeCover1Episode.gone()
                } else {
                    holder.tvAnimeCover1Episode.visible()
                    holder.tvAnimeCover1Episode.text = item.episode
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