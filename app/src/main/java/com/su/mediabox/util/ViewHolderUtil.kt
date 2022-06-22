package com.su.mediabox.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.R
import com.su.mediabox.view.component.textview.TypefaceTextView
import com.su.mediabox.view.component.FlowLayout

@Deprecated("2.0后删除")
class ViewHolderUtil {

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

class LicenseHeader1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvLicenseHeader1Name: TextView = view.findViewById(R.id.tv_license_header_1_name)
    val tvLicenseHeader1License: TextView = view.findViewById(R.id.tv_license_header_1_license)
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
    val tvAnimeInfoContinuePlay: TextView = view.findViewById(R.id.tv_anime_info_continue_play)
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
