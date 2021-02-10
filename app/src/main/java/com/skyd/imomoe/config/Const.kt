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
        }
    }

    interface Update {
        companion object {
            val updateFilePath = Environment.getExternalStorageDirectory().toString() + "/" + "Download/"
            const val updateFileName = "com.skyd.imomoe.apk"
            val updateFile get() = File(updateFilePath + updateFileName)
        }
    }
}