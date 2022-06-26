package com.su.mediabox.config

import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.bean.License
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.view.activity.LicenseActivity
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.linear
import com.su.mediabox.view.adapter.type.registerDataViewMap

interface Const {

    object ViewComponent {
        const val HISTORY_INFO_TAG = "history_info_tag"
        const val EPISODE_LIST_TAG = "episode_list_tag"
        const val DEFAULT_PAGE = 1
        const val PLAY_SPEED_TAG = "play_speed_tag"
    }

    object Player {
        val SELECT_ITEM_COLOR = getResColor(R.color.unchanged_main_color_2_skin)
        val UNSELECT_ITEM_COLOR = getResColor(android.R.color.white)
    }

    interface Common {
        companion object {
            const val GITHUB_URL = "https://github.com/RyensX/MediaBox"
            const val GITHUB_NEW_ISSUE_URL = "https://github.com/RyensX/MediaBox/issues/new"
            const val GITHUB_PLUGIN_REPO_URL = "https://github.com/RyensX/MediaBoxPluginRepository"
            const val GITHUB_PLUGIN_REPO_OFFICE_URL =
                "https://ryensx.github.io/MediaBoxPluginRepository/"
            const val USER_NOTICE_VERSION = 2

            val licenses = listOf(
                License("", "名称", "许可证", true),
                License("https://github.com/SkyD666/Imomoe", "Imomoe", "GPL-3.0 License"),
                License("https://github.com/jhy/jsoup", "jsoup", "MIT License"),
                License("https://github.com/coil-kt/coil", "coil", "Apache-2.0 License"),
                License("https://github.com/CarGuo/GSYVideoPlayer", "GSYVideoPlayer", "Apache-2.0 License"),
                License("https://github.com/square/okhttp", "okhttp", "Apache-2.0 License"),
                License("https://github.com/square/retrofit", "retrofit2", "Apache-2.0 License"),
                License("https://github.com/getActivity/XXPermissions", "XXPermissions", "Apache-2.0 License"),
                License("https://github.com/Kotlin/kotlinx.coroutines", "kotlinx.coroutines", "Apache-2.0 License"),
                License("https://github.com/afollestad/material-dialogs", "material-dialogs", "Apache-2.0 License"),
                License("https://github.com/lingochamp/FileDownloader", "FileDownloader", "Apache-2.0 License"),
                License("https://github.com/4thline/cling", "cling", "LGPL License"),
                License("https://github.com/eclipse/jetty.project", "jetty.project", "EPL-2.0, Apache-2.0 License"),
                License("https://github.com/NanoHttpd/nanohttpd", "nanohttpd", "BSD-3-Clause License"),
                License("https://github.com/greenrobot/EventBus", "EventBus", "Apache-2.0 License"),
                License("https://github.com/scwang90/SmartRefreshLayout", "SmartRefreshLayout", "Apache-2.0 License"),
                License("https://github.com/KwaiAppTeam/AkDanmaku", "AkDanmaku", "MIT License"),
                License("https://github.com/JakeWharton/DiskLruCache", "DiskLruCache", "Apache-2.0 License")
            )
    }
}

object Plugin {
    const val GITHUB_OFFICIAL_REPOSITORY_PLUGIN_INFO_TEMPLATE =
        "https://raw.githubusercontent.com/RyensX/MediaBoxPluginRepository/gh-pages/data/data.json"
    const val GITHUB_OFFICIAL_REPOSITORY_PAGE_PLUGIN_INFO_TEMPLATE =
        "https://raw.githubusercontent.com/RyensX/MediaBoxPluginRepository/gh-pages/data/data_{page}.json"

    const val PLUGIN_STATE_DOWNLOADABLE = 0
    const val PLUGIN_STATE_UPDATABLE = 70
    const val PLUGIN_STATE_DOWNLOADING = 1
    const val PLUGIN_STATE_OPEN = 100
}

interface Setting {
    companion object {
        const val NET_REPO_PROXY = "net_repo_proxy"
        const val SHOW_PLAY_BOTTOM_BAR = "show_play_bottom_bar"
        const val APP_LAUNCH_COUNT = "app_launch_count"
        const val PLAY_ACTION_DEFAULT_CORE = "play_action_default_core"
    }
}

interface Database {
    object AppDataBase {
        const val MEDIA_DB_FILE_NAME_TEMPLATE = "media_plugin_data_%s.db"
        const val FAVORITE_MEDIA_TABLE_NAME = "favorite"
        const val HISTORY_MEDIA_TABLE_NAME = "history"
        const val SEARCH_MEDIA_TABLE_NAME = "search"
    }

    object OfflineDataBase {
        const val OFFLINE_DATA_BASE_FILE_NAME = "offline_data.db"
        const val PLAY_RECORD_TABLE_NAME = "playRecord"
    }
}

interface DownloadAnime {
    companion object {
        val animeFilePath = App.context.getExternalFilesDir(null).toString() + "/DownloadAnime/"
    }
}

}