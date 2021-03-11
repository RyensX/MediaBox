package com.skyd.imomoe.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.BaseBean
import com.skyd.imomoe.view.widget.bannerview.BannerView
import com.skyd.imomoe.view.widget.textview.TypefaceTextView
import com.skyd.imomoe.view.widget.FlowLayout

class ViewHolderUtil {
    companion object {

        fun getItemViewType(item: BaseBean): Int = when (item.type) {
            "header1" -> HEADER_1
            "animeCover1" -> ANIME_COVER_1
            "animeCover2" -> ANIME_COVER_2
            "animeCover3" -> ANIME_COVER_3
            "animeCover4" -> ANIME_COVER_4
            "animeCover5" -> ANIME_COVER_5
            "animeCover6" -> ANIME_COVER_6
            "animeCover7" -> ANIME_COVER_7
            "animeCover8" -> ANIME_COVER_8
            "animeCover9" -> ANIME_COVER_9
            "animeEpisode2" -> ANIME_EPISODE_2
            "animeEpisodeFlowLayout1" -> ANIME_EPISODE_FLOW_LAYOUT_1
            "animeEpisodeFlowLayout2" -> ANIME_EPISODE_FLOW_LAYOUT_2
            "animeDescribe1" -> ANIME_DESCRIBE_1
            "gridRecyclerView1" -> GRID_RECYCLER_VIEW_1
            "banner1" -> BANNER_1
            "licenseHeader1" -> LICENSE_HEADER_1
            "license1" -> LICENSE_1
            "searchHistoryHeader1" -> SEARCH_HISTORY_HEADER_1
            "searchHistory1" -> SEARCH_HISTORY_1
            "animeInfo1" -> ANIME_INFO_1
            "horizontalRecyclerView1" -> HORIZONTAL_RECYCLER_VIEW_1
            "upnpDevice1" -> UPNP_DEVICE_1
            "More1" -> MORE_1
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
            ANIME_COVER_6 -> AnimeCover6ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_cover_6, parent, false)
            )
            ANIME_COVER_7 -> AnimeCover7ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_cover_7, parent, false)
            )
            ANIME_EPISODE_FLOW_LAYOUT_1 -> AnimeEpisodeFlowLayout1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_episode_flow_layout_1, parent, false)
            )
            ANIME_EPISODE_FLOW_LAYOUT_2 -> AnimeEpisodeFlowLayout2ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_episode_flow_layout_2, parent, false)
            )
            ANIME_DESCRIBE_1 -> AnimeDescribe1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_describe_1, parent, false)
            )
            GRID_RECYCLER_VIEW_1 -> GridRecyclerView1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_grid_recycler_view_1, parent, false)
            )
            BANNER_1 -> Banner1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_banner_1, parent, false)
            )
            LICENSE_HEADER_1 -> LicenseHeader1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_license_header_1, parent, false)
            )
            LICENSE_1 -> License1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_license_1, parent, false)
            )
            SEARCH_HISTORY_HEADER_1 -> SearchHistoryHeader1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_search_history_header_1, parent, false)
            )
            SEARCH_HISTORY_1 -> SearchHistory1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_search_history_1, parent, false)
            )
            ANIME_INFO_1 -> AnimeInfo1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_info_1, parent, false)
            )
            HORIZONTAL_RECYCLER_VIEW_1 -> HorizontalRecyclerView1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_horizontal_recycler_view_1, parent, false)
            )
            ANIME_EPISODE_2 -> AnimeEpisode2ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_episode_2, parent, false)
            )
            UPNP_DEVICE_1 -> UpnpDevice1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_dlna_device_1, parent, false)
            )
            MORE_1 -> More1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_more_1, parent, false)
            )
            ANIME_COVER_8 -> AnimeCover8ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_cover_8, parent, false)
            )
            ANIME_COVER_9 -> AnimeCover9ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_anime_cover_9, parent, false)
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
        const val BANNER_1 = 9
        const val ANIME_COVER_6 = 10
        const val LICENSE_HEADER_1 = 11
        const val LICENSE_1 = 12
        const val SEARCH_HISTORY_HEADER_1 = 13
        const val SEARCH_HISTORY_1 = 14
        const val ANIME_COVER_7 = 15
        const val ANIME_EPISODE_FLOW_LAYOUT_2 = 16
        const val ANIME_INFO_1 = 17
        const val HORIZONTAL_RECYCLER_VIEW_1 = 18
        const val ANIME_EPISODE_2 = 19
        const val UPNP_DEVICE_1 = 20
        const val MORE_1 = 21
        const val ANIME_COVER_8 = 22
        const val ANIME_COVER_9 = 23
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
    val ivAnimeCover1Cover = view.findViewById<ImageView>(R.id.iv_anime_cover_1_cover)
    val tvAnimeCover1Title = view.findViewById<TextView>(R.id.tv_anime_cover_1_title)
    val tvAnimeCover1Episode = view.findViewById<TextView>(R.id.tv_anime_cover_1_episode)
    val viewAnimeCover1Night = view.findViewById<View>(R.id.view_anime_cover_1_night)
}

class AnimeCover2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover1Title = view.findViewById<TextView>(R.id.tv_anime_cover_2_title)
    val tvAnimeCover1Episode = view.findViewById<TextView>(R.id.tv_anime_cover_2_episode)
}

class AnimeCover3ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover3Cover = view.findViewById<ImageView>(R.id.iv_anime_cover_3_cover)
    val tvAnimeCover3Title = view.findViewById<TextView>(R.id.tv_anime_cover_3_title)
    val tvAnimeCover3Episode = view.findViewById<TextView>(R.id.tv_anime_cover_3_episode)
    val flAnimeCover3Type = view.findViewById<FlowLayout>(R.id.fl_anime_cover_3_type)
    val tvAnimeCover3Describe = view.findViewById<TextView>(R.id.tv_anime_cover_3_describe)
    val tvAnimeCover3Alias = view.findViewById<TextView>(R.id.tv_anime_cover_3_alias)
    val viewAnimeCover3Night = view.findViewById<View>(R.id.view_anime_cover_3_night)
}

class AnimeCover4ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover4Cover = view.findViewById<ImageView>(R.id.iv_anime_cover_4_cover)
    val tvAnimeCover4Title = view.findViewById<TextView>(R.id.tv_anime_cover_4_title)
    val viewAnimeCover4Night = view.findViewById<View>(R.id.view_anime_cover_4_night)
}

class AnimeCover5ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover5Title = view.findViewById<TextView>(R.id.tv_anime_cover_5_title)
    val tvAnimeCover5Area = view.findViewById<TextView>(R.id.tv_anime_cover_5_area)
    val tvAnimeCover5Date = view.findViewById<TextView>(R.id.tv_anime_cover_5_date)
    val tvAnimeCover5Episode = view.findViewById<TextView>(R.id.tv_anime_cover_5_episode)
    val tvAnimeCover5Rank = view.findViewById<TextView>(R.id.tv_anime_cover_5_rank)
}

class AnimeCover6ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover6Cover = view.findViewById<ImageView>(R.id.iv_anime_cover_6_cover)
    val tvAnimeCover6Title = view.findViewById<TextView>(R.id.tv_anime_cover_6_title)
    val tvAnimeCover6Episode = view.findViewById<TextView>(R.id.tv_anime_cover_6_episode)
    val tvAnimeCover6Describe = view.findViewById<TextView>(R.id.tv_anime_cover_6_describe)
    val tvAnimeCover6Night = view.findViewById<View>(R.id.view_anime_cover_6_night)
}

class AnimeCover7ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover7Title = view.findViewById<TypefaceTextView>(R.id.tv_anime_cover_7_title)
    val tvAnimeCover7Size = view.findViewById<TypefaceTextView>(R.id.tv_anime_cover_7_size)
    val tvAnimeCover7Episodes = view.findViewById<TextView>(R.id.tv_anime_cover_7_episodes)
}

class AnimeCover8ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover8Title = view.findViewById<TypefaceTextView>(R.id.tv_anime_cover_8_title)
    val tvAnimeCover8Episodes = view.findViewById<TypefaceTextView>(R.id.tv_anime_cover_8_episode)
    val ivAnimeCover8Cover = view.findViewById<ImageView>(R.id.iv_anime_cover_8_cover)
}

class AnimeCover9ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover9Title = view.findViewById<TypefaceTextView>(R.id.tv_anime_cover_9_title)
    val tvAnimeCover9Episodes = view.findViewById<TypefaceTextView>(R.id.tv_anime_cover_9_episode)
    val tvAnimeCover9Time = view.findViewById<TypefaceTextView>(R.id.tv_anime_cover_9_time)
    val tvAnimeCover9DetailPage =
        view.findViewById<TypefaceTextView>(R.id.tv_anime_cover_9_detail_page)
    val ivAnimeCover9Cover = view.findViewById<ImageView>(R.id.iv_anime_cover_9_cover)
    val ivAnimeCover9Delete = view.findViewById<ImageView>(R.id.iv_anime_cover_9_delete)
}

class AnimeEpisodeFlowLayout1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val flAnimeEpisodeFlowLayout1 = view.findViewById<FlowLayout>(R.id.fl_anime_episode)
}

class AnimeEpisodeFlowLayout2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rvAnimeEpisodeFlowLayout2 = view.findViewById<RecyclerView>(R.id.fl_anime_episode_2)
}

class AnimeDescribe1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeDescribe1 = view.findViewById<TextView>(R.id.tv_anime_describe)
}

class Banner1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val banner1 = view.findViewById<BannerView>(R.id.banner_1)
}

class LicenseHeader1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvLicenseHeader1Name = view.findViewById<TextView>(R.id.tv_license_header_1_name)
    val tvLicenseHeader1License = view.findViewById<TextView>(R.id.tv_license_header_1_license)
}

class License1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvLicense1Name = view.findViewById<TextView>(R.id.tv_license_1_name)
    val tvLicense1License = view.findViewById<TextView>(R.id.tv_license_1_license)
}

class SearchHistoryHeader1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvSearchHistoryHeader1Title =
        view.findViewById<TextView>(R.id.tv_search_history_header_1_title)
}

class SearchHistory1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvSearchHistory1Title = view.findViewById<TextView>(R.id.tv_search_history_1_title)
    val ivSearchHistory1Delete = view.findViewById<ImageView>(R.id.iv_search_history_1_delete)
}

class AnimeInfo1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeInfo1Cover = view.findViewById<ImageView>(R.id.iv_anime_info_1_cover)
    val tvAnimeInfo1Title = view.findViewById<TextView>(R.id.tv_anime_info_1_title)
    val tvAnimeInfo1Alias = view.findViewById<TextView>(R.id.tv_anime_info_1_alias)
    val tvAnimeInfo1Area = view.findViewById<TextView>(R.id.tv_anime_info_1_area)
    val tvAnimeInfo1Year = view.findViewById<TextView>(R.id.tv_anime_info_1_year)
    val tvAnimeInfo1Index = view.findViewById<TextView>(R.id.tv_anime_info_1_index)
    val tvAnimeInfo1Type = view.findViewById<TextView>(R.id.tv_anime_info_1_type)
    val flAnimeInfo1Type = view.findViewById<FlowLayout>(R.id.fl_anime_info_1_type)
    val tvAnimeInfo1Tag = view.findViewById<TextView>(R.id.tv_anime_info_1_tag)
    val flAnimeInfo1Tag = view.findViewById<FlowLayout>(R.id.fl_anime_info_1_tag)
    val tvAnimeInfo1Info = view.findViewById<TextView>(R.id.tv_anime_info_1_info)
}

class HorizontalRecyclerView1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rvHorizontalRecyclerView1 =
        view.findViewById<RecyclerView>(R.id.rv_horizontal_recycler_view_1)
    val ivHorizontalRecyclerView1More =
        view.findViewById<ImageView>(R.id.iv_horizontal_recycler_view_1_more)
}

class AnimeEpisode2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeEpisode2 = view.findViewById<TextView>(R.id.tv_anime_episode_2)
}

class UpnpDevice1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvUpnpDevice1Title = view.findViewById<TextView>(R.id.tv_upnp_device_1_title)
}

class More1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivMore1 = view.findViewById<ImageView>(R.id.iv_more_1)
    val tvMore1 = view.findViewById<TextView>(R.id.tv_more_1)
}
