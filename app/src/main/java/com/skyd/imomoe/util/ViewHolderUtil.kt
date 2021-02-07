package com.skyd.imomoe.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nex3z.flowlayout.FlowLayout
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.BaseBean
import com.skyd.imomoe.view.widget.RoundCornersImageView

class ViewHolderUtil {
    companion object {

        fun getItemViewType(item: BaseBean): Int = when (item.type) {
            "header1" -> HEADER_1
            "animeCover1" -> ANIME_COVER_1
            "animeCover2" -> ANIME_COVER_2
            "animeCover3" -> ANIME_COVER_3
            "animeCover4" -> ANIME_COVER_4
            "animeCover5" -> ANIME_COVER_5
            "animeEpisodeFlowLayout1" -> ANIME_EPISODE_FLOW_LAYOUT_1
            "animeDescribe1" -> ANIME_DESCRIBE_1
            "gridRecyclerView1" -> GRID_RECYCLER_VIEW_1
            else -> UNKNOWN
        }

        fun getViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
            HEADER_1 -> Header1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_header_1, parent, false)
            )
            ANIME_COVER_1 -> AnimeCover1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_cover_1, parent, false)
            )
            ANIME_COVER_2 -> AnimeCover2ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_cover_2, parent, false)
            )
            ANIME_COVER_3 -> AnimeCover3ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_cover_3, parent, false)
            )
            ANIME_COVER_4 -> AnimeCover4ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_cover_4, parent, false)
            )
            ANIME_COVER_5 -> AnimeCover5ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_cover_5, parent, false)
            )
            ANIME_EPISODE_FLOW_LAYOUT_1 -> AnimeEpisodeFlowLayout1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_episode_flow_layout_1, parent, false)
            )
            ANIME_DESCRIBE_1 -> AnimeDescribe1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_describe_1, parent, false)
            )
            GRID_RECYCLER_VIEW_1 -> GridRecyclerView1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_grid_recycler_view_1, parent, false)
            )
            else -> EmptyViewHolder(View(parent.context))
        }

        const val UNKNOWN = -1              //未知类型，使用EmptyViewHolder容错处理。
        const val HEADER_1 = 0
        const val ANIME_COVER_1 = 1
        const val ANIME_EPISODE_FLOW_LAYOUT_1 = 2
        const val ANIME_DESCRIBE_1 = 3
        const val ANIME_COVER_2 = 4
        const val ANIME_COVER_3 = 6
        const val ANIME_COVER_4 = 7
        const val ANIME_COVER_5 = 8
        const val GRID_RECYCLER_VIEW_1 = 5
        const val MAX = 100   //避免外部其他类型与此处包含的某个类型重复。
    }
}

class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

class GridRecyclerView1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rvGridRecyclerView1 = view.findViewById<RecyclerView>(R.id.rv_grid_recycler_view_1)
}

class Header1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvHeader1Title = view.findViewById<TextView>(R.id.tv_header_1_title)
}

class AnimeCover1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover1Cover = view.findViewById<RoundCornersImageView>(R.id.iv_anime_cover_1_cover)
    val tvAnimeCover1Title = view.findViewById<TextView>(R.id.tv_anime_cover_1_title)
    val tvAnimeCover1Episode = view.findViewById<TextView>(R.id.tv_anime_cover_1_episode)
}

class AnimeCover2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover1Title = view.findViewById<TextView>(R.id.tv_anime_cover_2_title)
    val tvAnimeCover1Episode = view.findViewById<TextView>(R.id.tv_anime_cover_2_episode)
}

class AnimeCover3ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover3Cover = view.findViewById<RoundCornersImageView>(R.id.iv_anime_cover_3_cover)
    val tvAnimeCover3Title = view.findViewById<TextView>(R.id.tv_anime_cover_3_title)
    val tvAnimeCover3Episode = view.findViewById<TextView>(R.id.tv_anime_cover_3_episode)
    val flAnimeCover3Type = view.findViewById<FlowLayout>(R.id.fl_anime_cover_3_type)
    val tvAnimeCover3Describe = view.findViewById<TextView>(R.id.tv_anime_cover_3_describe)
    val tvAnimeCover3Alias = view.findViewById<TextView>(R.id.tv_anime_cover_3_alias)
}

class AnimeCover4ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover4Cover = view.findViewById<RoundCornersImageView>(R.id.iv_anime_cover_4_cover)
    val tvAnimeCover4Title = view.findViewById<TextView>(R.id.tv_anime_cover_4_title)
}

class AnimeCover5ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover5Title = view.findViewById<TextView>(R.id.tv_anime_cover_5_title)
    val tvAnimeCover5Area = view.findViewById<TextView>(R.id.tv_anime_cover_5_area)
    val tvAnimeCover5Date = view.findViewById<TextView>(R.id.tv_anime_cover_5_date)
    val tvAnimeCover5Episode = view.findViewById<TextView>(R.id.tv_anime_cover_5_episode)
    val tvAnimeCover5Rank = view.findViewById<TextView>(R.id.tv_anime_cover_5_rank)
}

class AnimeEpisodeFlowLayout1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val flAnimeEpisodeFlowLayout1 = view.findViewById<FlowLayout>(R.id.fl_anime_episode)
}

class AnimeDescribe1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeDescribe1 = view.findViewById<TextView>(R.id.tv_anime_describe)
}

