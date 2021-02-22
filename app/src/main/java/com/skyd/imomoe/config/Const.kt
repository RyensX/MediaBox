package com.skyd.imomoe.config

import android.os.Environment
import java.io.File

interface Const {
    interface ActionUrl {
        companion object {
            const val ANIME_DETAIL = "/show/"
            const val ANIME_PLAY = "/v/"
            const val ANIME_SEARCH = "/search/"
            const val ANIME_TOP = "/top/"
            const val ANIME_CLASSIFY = "/app/classify"      //此常量为自己定义，与服务器无关
            const val ANIME_BROWSER = "/app/browser"      //此常量为自己定义，与服务器无关
            const val ANIME_ANIME_DOWNLOAD_EPISODE = "/app/animeDownloadEpisode"      //此常量为自己定义，转到下载的每一集
            const val ANIME_ANIME_DOWNLOAD_PLAY = "/app/animeDownloadPlay"      //此常量为自己定义，播放这一集
            const val ANIME_ANIME_DOWNLOAD_M3U8 = "/app/animeDownloadM3U8"      //此常量为自己定义，m3u8格式
        }
    }

    interface Update {
        companion object {
            val updateFilePath = Environment.getExternalStorageDirectory().toString() + "/" + "Download/"
            const val updateFileName = "com.skyd.imomoe.apk"
            val updateFile get() = File(updateFilePath + updateFileName)
        }
    }

    interface DownloadAnime {
        companion object {
            val animeFilePath = Environment.getExternalStorageDirectory().toString() + "/Imomoe/DownloadAnime/"
        }
    }

    interface Request {
        companion object {
            const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36"
        }
    }
}