package com.skyd.imomoe.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.BaseBean
import com.skyd.imomoe.view.component.bannerview.BannerView
import com.skyd.imomoe.view.component.textview.TypefaceTextView
import com.skyd.imomoe.view.component.FlowLayout
import com.skyd.imomoe.config.Const.ViewHolderTypeInt
import com.skyd.imomoe.config.Const.ViewHolderTypeString

class ViewHolderUtil {
    companion object {

        fun getItemViewType(item: BaseBean): Int = when (item.type) {
            ViewHolderTypeString.HEADER_1 -> ViewHolderTypeInt.HEADER_1
            ViewHolderTypeString.ANIME_COVER_1 -> ViewHolderTypeInt.ANIME_COVER_1
            ViewHolderTypeString.ANIME_COVER_2 -> ViewHolderTypeInt.ANIME_COVER_2
            ViewHolderTypeString.ANIME_COVER_3 -> ViewHolderTypeInt.ANIME_COVER_3
            ViewHolderTypeString.ANIME_COVER_4 -> ViewHolderTypeInt.ANIME_COVER_4
            ViewHolderTypeString.ANIME_COVER_5 -> ViewHolderTypeInt.ANIME_COVER_5
            ViewHolderTypeString.ANIME_COVER_6 -> ViewHolderTypeInt.ANIME_COVER_6
            ViewHolderTypeString.ANIME_COVER_7 -> ViewHolderTypeInt.ANIME_COVER_7
            ViewHolderTypeString.ANIME_COVER_8 -> ViewHolderTypeInt.ANIME_COVER_8
            ViewHolderTypeString.ANIME_COVER_9 -> ViewHolderTypeInt.ANIME_COVER_9
            ViewHolderTypeString.ANIME_EPISODE_2 -> ViewHolderTypeInt.ANIME_EPISODE_2
            ViewHolderTypeString.ANIME_EPISODE_FLOW_LAYOUT_1 -> ViewHolderTypeInt.ANIME_EPISODE_FLOW_LAYOUT_1
            ViewHolderTypeString.ANIME_EPISODE_FLOW_LAYOUT_2 -> ViewHolderTypeInt.ANIME_EPISODE_FLOW_LAYOUT_2
            ViewHolderTypeString.ANIME_DESCRIBE_1 -> ViewHolderTypeInt.ANIME_DESCRIBE_1
            ViewHolderTypeString.GRID_RECYCLER_VIEW_1 -> ViewHolderTypeInt.GRID_RECYCLER_VIEW_1
            ViewHolderTypeString.BANNER_1 -> ViewHolderTypeInt.BANNER_1
            ViewHolderTypeString.LICENSE_HEADER_1 -> ViewHolderTypeInt.LICENSE_HEADER_1
            ViewHolderTypeString.LICENSE_1 -> ViewHolderTypeInt.LICENSE_1
            ViewHolderTypeString.SEARCH_HISTORY_HEADER_1 -> ViewHolderTypeInt.SEARCH_HISTORY_HEADER_1
            ViewHolderTypeString.SEARCH_HISTORY_1 -> ViewHolderTypeInt.SEARCH_HISTORY_1
            ViewHolderTypeString.ANIME_INFO_1 -> ViewHolderTypeInt.ANIME_INFO_1
            ViewHolderTypeString.HORIZONTAL_RECYCLER_VIEW_1 -> ViewHolderTypeInt.HORIZONTAL_RECYCLER_VIEW_1
            ViewHolderTypeString.UPNP_DEVICE_1 -> ViewHolderTypeInt.UPNP_DEVICE_1
            ViewHolderTypeString.MORE_1 -> ViewHolderTypeInt.MORE_1
            ViewHolderTypeString.SKIN_COVER_1 -> ViewHolderTypeInt.SKIN_COVER_1
            ViewHolderTypeString.DATA_SOURCE_1 -> ViewHolderTypeInt.DATA_SOURCE_1
            else -> ViewHolderTypeInt.UNKNOWN
        }

        fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = if (viewType == ViewHolderTypeInt.UNKNOWN) View(parent.context)
            else LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return when (viewType) {
                ViewHolderTypeInt.HEADER_1 -> Header1ViewHolder(view)
                ViewHolderTypeInt.ANIME_COVER_1 -> AnimeCover1ViewHolder(view)
                ViewHolderTypeInt.ANIME_COVER_2 -> AnimeCover2ViewHolder(view)
                ViewHolderTypeInt.ANIME_COVER_3 -> AnimeCover3ViewHolder(view)
                ViewHolderTypeInt.ANIME_COVER_4 -> AnimeCover4ViewHolder(view)
                ViewHolderTypeInt.ANIME_COVER_5 -> AnimeCover5ViewHolder(view)
                ViewHolderTypeInt.ANIME_COVER_6 -> AnimeCover6ViewHolder(view)
                ViewHolderTypeInt.ANIME_COVER_7 -> AnimeCover7ViewHolder(view)
                ViewHolderTypeInt.ANIME_EPISODE_FLOW_LAYOUT_1 ->
                    AnimeEpisodeFlowLayout1ViewHolder(view)
                ViewHolderTypeInt.ANIME_EPISODE_FLOW_LAYOUT_2 ->
                    AnimeEpisodeFlowLayout2ViewHolder(view)
                ViewHolderTypeInt.ANIME_DESCRIBE_1 -> AnimeDescribe1ViewHolder(view)
                ViewHolderTypeInt.GRID_RECYCLER_VIEW_1 -> GridRecyclerView1ViewHolder(view)
                ViewHolderTypeInt.BANNER_1 -> Banner1ViewHolder(view)
                ViewHolderTypeInt.LICENSE_HEADER_1 -> LicenseHeader1ViewHolder(view)
                ViewHolderTypeInt.LICENSE_1 -> License1ViewHolder(view)
                ViewHolderTypeInt.SEARCH_HISTORY_HEADER_1 -> SearchHistoryHeader1ViewHolder(view)
                ViewHolderTypeInt.SEARCH_HISTORY_1 -> SearchHistory1ViewHolder(view)
                ViewHolderTypeInt.ANIME_INFO_1 -> AnimeInfo1ViewHolder(view)
                ViewHolderTypeInt.HORIZONTAL_RECYCLER_VIEW_1 ->
                    HorizontalRecyclerView1ViewHolder(view)
                ViewHolderTypeInt.ANIME_EPISODE_2 -> AnimeEpisode2ViewHolder(view)
                ViewHolderTypeInt.UPNP_DEVICE_1 -> UpnpDevice1ViewHolder(view)
                ViewHolderTypeInt.MORE_1 -> More1ViewHolder(view)
                ViewHolderTypeInt.ANIME_COVER_8 -> AnimeCover8ViewHolder(view)
                ViewHolderTypeInt.ANIME_COVER_9 -> AnimeCover9ViewHolder(view)
                ViewHolderTypeInt.SKIN_COVER_1 -> SkinCover1ViewHolder(view)
                ViewHolderTypeInt.DATA_SOURCE_1 -> DataSource1ViewHolder(view)
                else -> EmptyViewHolder(view)
            }
        }
    }
}

class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

class DataSource1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvDataSource1Name: TextView = view.findViewById(R.id.tv_data_source_1_name)
    val tvDataSource1Size: TextView = view.findViewById(R.id.tv_data_source_1_size)
    val ivDataSource1Selected: ImageView = view.findViewById(R.id.iv_data_source_1_selected)
}

class GridRecyclerView1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rvGridRecyclerView1: RecyclerView = view.findViewById(R.id.rv_grid_recycler_view_1)
}

class Header1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvHeader1Title: TextView = view.findViewById(R.id.tv_header_1_title)
}

class AnimeCover1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover1Cover: ImageView = view.findViewById(R.id.iv_anime_cover_1_cover)
    val tvAnimeCover1Title: TextView = view.findViewById(R.id.tv_anime_cover_1_title)
    val tvAnimeCover1Episode: TextView = view.findViewById(R.id.tv_anime_cover_1_episode)
    val viewAnimeCover1Night: View = view.findViewById(R.id.view_anime_cover_1_night)
}

class AnimeCover2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover1Title: TextView = view.findViewById(R.id.tv_anime_cover_2_title)
    val tvAnimeCover1Episode: TextView = view.findViewById(R.id.tv_anime_cover_2_episode)
}

class AnimeCover3ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover3Cover: ImageView = view.findViewById(R.id.iv_anime_cover_3_cover)
    val tvAnimeCover3Title: TextView = view.findViewById(R.id.tv_anime_cover_3_title)
    val tvAnimeCover3Episode: TextView = view.findViewById(R.id.tv_anime_cover_3_episode)
    val flAnimeCover3Type: FlowLayout = view.findViewById(R.id.fl_anime_cover_3_type)
    val tvAnimeCover3Describe: TextView = view.findViewById(R.id.tv_anime_cover_3_describe)
    val tvAnimeCover3Alias: TextView = view.findViewById(R.id.tv_anime_cover_3_alias)
    val viewAnimeCover3Night: View = view.findViewById(R.id.view_anime_cover_3_night)
}

class AnimeCover4ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover4Cover: ImageView = view.findViewById(R.id.iv_anime_cover_4_cover)
    val tvAnimeCover4Title: TextView = view.findViewById(R.id.tv_anime_cover_4_title)
    val viewAnimeCover4Night: View = view.findViewById(R.id.view_anime_cover_4_night)
}

class AnimeCover5ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover5Title: TextView = view.findViewById(R.id.tv_anime_cover_5_title)
    val tvAnimeCover5Area: TextView = view.findViewById(R.id.tv_anime_cover_5_area)
    val tvAnimeCover5Date: TextView = view.findViewById(R.id.tv_anime_cover_5_date)
    val tvAnimeCover5Episode: TextView = view.findViewById(R.id.tv_anime_cover_5_episode)
    val tvAnimeCover5Rank: TextView = view.findViewById(R.id.tv_anime_cover_5_rank)
}

class AnimeCover6ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover6Cover: ImageView = view.findViewById(R.id.iv_anime_cover_6_cover)
    val tvAnimeCover6Title: TextView = view.findViewById(R.id.tv_anime_cover_6_title)
    val tvAnimeCover6Episode: TextView = view.findViewById(R.id.tv_anime_cover_6_episode)
    val tvAnimeCover6Describe: TextView = view.findViewById(R.id.tv_anime_cover_6_describe)
    val tvAnimeCover6Night: View = view.findViewById(R.id.view_anime_cover_6_night)
}

class AnimeCover7ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover7Title: TypefaceTextView = view.findViewById(R.id.tv_anime_cover_7_title)
    val tvAnimeCover7Size: TypefaceTextView = view.findViewById(R.id.tv_anime_cover_7_size)
    val tvAnimeCover7Episodes: TextView = view.findViewById(R.id.tv_anime_cover_7_episodes)
    val tvAnimeCover7OldPath: TextView = view.findViewById(R.id.tv_anime_cover_7_old_path)
}

class AnimeCover8ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover8Title: TypefaceTextView = view.findViewById(R.id.tv_anime_cover_8_title)
    val tvAnimeCover8Episodes: TypefaceTextView = view.findViewById(R.id.tv_anime_cover_8_episode)
    val ivAnimeCover8Cover: ImageView = view.findViewById(R.id.iv_anime_cover_8_cover)
}

class AnimeCover9ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover9Title: TypefaceTextView = view.findViewById(R.id.tv_anime_cover_9_title)
    val tvAnimeCover9Episodes: TypefaceTextView = view.findViewById(R.id.tv_anime_cover_9_episode)
    val tvAnimeCover9Time: TypefaceTextView = view.findViewById(R.id.tv_anime_cover_9_time)
    val tvAnimeCover9DetailPage: TypefaceTextView =
        view.findViewById(R.id.tv_anime_cover_9_detail_page)
    val ivAnimeCover9Cover: ImageView = view.findViewById(R.id.iv_anime_cover_9_cover)
    val ivAnimeCover9Delete: ImageView = view.findViewById(R.id.iv_anime_cover_9_delete)
}

class AnimeEpisodeFlowLayout1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val flAnimeEpisodeFlowLayout1: FlowLayout = view.findViewById(R.id.fl_anime_episode)
}

class AnimeEpisodeFlowLayout2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rvAnimeEpisodeFlowLayout2: RecyclerView = view.findViewById(R.id.fl_anime_episode_2)
}

class AnimeDescribe1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeDescribe1: TextView = view.findViewById(R.id.tv_anime_describe)
}

class Banner1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val banner1: BannerView = view.findViewById(R.id.banner_1)
}

class LicenseHeader1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvLicenseHeader1Name: TextView = view.findViewById(R.id.tv_license_header_1_name)
    val tvLicenseHeader1License: TextView = view.findViewById(R.id.tv_license_header_1_license)
}

class License1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvLicense1Name: TextView = view.findViewById(R.id.tv_license_1_name)
    val tvLicense1License: TextView = view.findViewById(R.id.tv_license_1_license)
}

class SearchHistoryHeader1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvSearchHistoryHeader1Title: TextView =
        view.findViewById(R.id.tv_search_history_header_1_title)
}

class SearchHistory1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvSearchHistory1Title: TextView = view.findViewById(R.id.tv_search_history_1_title)
    val ivSearchHistory1Delete: ImageView = view.findViewById(R.id.iv_search_history_1_delete)
}

class AnimeInfo1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeInfo1Cover: ImageView = view.findViewById(R.id.iv_anime_info_1_cover)
    val tvAnimeInfo1Title: TextView = view.findViewById(R.id.tv_anime_info_1_title)
    val tvAnimeInfo1Alias: TextView = view.findViewById(R.id.tv_anime_info_1_alias)
    val tvAnimeInfo1Area: TextView = view.findViewById(R.id.tv_anime_info_1_area)
    val tvAnimeInfo1Year: TextView = view.findViewById(R.id.tv_anime_info_1_year)
    val tvAnimeInfo1Index: TextView = view.findViewById(R.id.tv_anime_info_1_index)
    val tvAnimeInfo1Type: TextView = view.findViewById(R.id.tv_anime_info_1_type)
    val flAnimeInfo1Type: FlowLayout = view.findViewById(R.id.fl_anime_info_1_type)
    val tvAnimeInfo1Tag: TextView = view.findViewById(R.id.tv_anime_info_1_tag)
    val flAnimeInfo1Tag: FlowLayout = view.findViewById(R.id.fl_anime_info_1_tag)
    val tvAnimeInfo1Info: TextView = view.findViewById(R.id.tv_anime_info_1_info)
}

class HorizontalRecyclerView1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rvHorizontalRecyclerView1: RecyclerView =
        view.findViewById(R.id.rv_horizontal_recycler_view_1)
    val ivHorizontalRecyclerView1More: ImageView =
        view.findViewById(R.id.iv_horizontal_recycler_view_1_more)
}

class AnimeEpisode2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeEpisode2: TextView = view.findViewById(R.id.tv_anime_episode_2)
}

class UpnpDevice1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvUpnpDevice1Title: TextView = view.findViewById(R.id.tv_upnp_device_1_title)
}

class More1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivMore1: ImageView = view.findViewById(R.id.iv_more_1)
    val tvMore1: TextView = view.findViewById(R.id.tv_more_1)
}

class SkinCover1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivSkinCover1Cover: ImageView = view.findViewById(R.id.iv_skin_cover_1_cover)
    val tvSkinCover1Title: TypefaceTextView = view.findViewById(R.id.tv_skin_cover_1_title)
    val ivSkinCover1Selected: ImageView = view.findViewById(R.id.iv_skin_cover_1_selected)
}
