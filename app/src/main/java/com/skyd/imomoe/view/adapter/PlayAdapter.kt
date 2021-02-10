package com.skyd.imomoe.view.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeDetailDataBean
import com.skyd.imomoe.util.*
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getViewHolder
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getItemViewType
import com.skyd.imomoe.view.activity.PlayActivity

class PlayAdapter(
    val activity: PlayActivity,
    private val dataList: List<AnimeDetailDataBean>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val gridItemDecoration = AnimeCoverItemDecoration()

    override fun getItemViewType(position: Int): Int = getItemViewType(dataList[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        getViewHolder(parent, viewType)

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]

        when (holder) {
            is GridRecyclerView1ViewHolder -> {
                item.animeCoverList?.let {
                    val layoutManager = GridLayoutManager(activity, 4)
                    holder.rvGridRecyclerView1.post {
                        holder.rvGridRecyclerView1.setPadding(
                            Util.dp2px(16f), 0,
                            Util.dp2px(16f), 0
                        )
                    }
                    holder.rvGridRecyclerView1.removeItemDecoration(gridItemDecoration)
                    holder.rvGridRecyclerView1.addItemDecoration(gridItemDecoration)
                    holder.rvGridRecyclerView1.layoutManager = layoutManager
                    holder.rvGridRecyclerView1.adapter =
                        AnimeShowAdapter.GridRecyclerView1Adapter(activity, it)
                }
            }
            is Header1ViewHolder -> {
                holder.tvHeader1Title.textSize = 15f
                holder.tvHeader1Title.text = item.title
            }
            is AnimeCover2ViewHolder -> {
                holder.tvAnimeCover1Title.text = item.title
                holder.tvAnimeCover1Episode.gone()
                holder.itemView.setOnClickListener {
                    process(activity, item.actionUrl)
                }
            }
            is AnimeEpisodeFlowLayout1ViewHolder -> {
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
                        tvFlowLayout.setOnClickListener { it1 ->
                            activity.startPlay(it[i].actionUrl, it[i].title)
                        }
                        holder.flAnimeEpisodeFlowLayout1.addView(tvFlowLayout)
                    }
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}