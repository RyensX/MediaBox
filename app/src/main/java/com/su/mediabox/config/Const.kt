package com.su.mediabox.config

import android.os.Environment
import com.su.mediabox.App
import com.su.mediabox.R

interface Const {
    interface Common {
        companion object {
            const val GITHUB_URL = "https://github.com/Ryensu/MediaBox"
            const val GITHUB_NEW_ISSUE_URL = "https://github.com/Ryensu/MediaBox/issues/new"
            const val USER_NOTICE_VERSION = 2
        }
    }

    interface ShortCuts {
        companion object {
            const val ID_FAVORITE = "favorite"
            const val ID_EVERYDAY = "everyday"
            const val ID_DOWNLOAD = "download"
            const val ACTION_EVERYDAY = "everyday"
        }
    }

    interface Database {
        object AppDataBase {
            const val APP_DATA_BASE_FILE_NAME = "app.db"
            const val ANIME_DOWNLOAD_TABLE_NAME = "animeDownloadList"
            const val FAVORITE_ANIME_TABLE_NAME = "favoriteAnimeList"
            const val HISTORY_TABLE_NAME = "historyList"
            const val SEARCH_HISTORY_TABLE_NAME = "searchHistoryList"
        }

        object OfflineDataBase {
            const val OFFLINE_DATA_BASE_FILE_NAME = "offline_data.db"
            const val PLAY_RECORD_TABLE_NAME = "playRecord"
        }
    }

    interface ViewHolderTypeInt {
        companion object {
            const val UNKNOWN = -1              //未知类型，使用EmptyViewHolder容错处理。
            const val HEADER_1 = R.layout.item_header_1
            const val ANIME_COVER_1 = R.layout.item_anime_cover_1
            const val ANIME_COVER_2 = R.layout.item_anime_cover_2
            const val ANIME_COVER_3 = R.layout.item_anime_cover_3
            const val ANIME_COVER_4 = R.layout.item_anime_cover_4
            const val ANIME_COVER_5 = R.layout.item_anime_cover_5
            const val ANIME_COVER_6 = R.layout.item_anime_cover_6
            const val ANIME_COVER_7 = R.layout.item_anime_cover_7
            const val ANIME_COVER_8 = R.layout.item_anime_cover_8
            const val ANIME_COVER_9 = R.layout.item_anime_cover_9
            const val GRID_RECYCLER_VIEW_1 = R.layout.item_grid_recycler_view_1
            const val BANNER_1 = R.layout.item_banner_1
            const val LICENSE_HEADER_1 = R.layout.item_license_header_1
            const val LICENSE_1 = R.layout.item_license_1
            const val SEARCH_HISTORY_HEADER_1 = R.layout.item_search_history_header_1
            const val SEARCH_HISTORY_1 = R.layout.item_search_history_1
            const val ANIME_EPISODE_FLOW_LAYOUT_1 = R.layout.item_anime_episode_flow_layout_1
            const val ANIME_EPISODE_FLOW_LAYOUT_2 = R.layout.item_anime_episode_flow_layout_2
            const val ANIME_DESCRIBE_1 = R.layout.item_anime_describe_1
            const val ANIME_INFO_1 = R.layout.item_anime_info_1
            const val HORIZONTAL_RECYCLER_VIEW_1 = R.layout.item_horizontal_recycler_view_1
            const val ANIME_EPISODE_2 = R.layout.item_anime_episode_2
            const val UPNP_DEVICE_1 = R.layout.item_dlna_device_1
            const val MORE_1 = R.layout.item_more_1
            const val SKIN_COVER_1 = R.layout.item_skin_cover_1
            const val DATA_SOURCE_1 = R.layout.item_data_source_1
        }
    }

    interface DownloadAnime {
        companion object {
            var new: Boolean = true
            val animeFilePath: String
                get() {
                    return if (new) App.context.getExternalFilesDir(null)
                        .toString() + "/DownloadAnime/"
                    else Environment.getExternalStorageDirectory()
                        .toString() + "/Imomoe/DownloadAnime/"
                }
        }
    }

}