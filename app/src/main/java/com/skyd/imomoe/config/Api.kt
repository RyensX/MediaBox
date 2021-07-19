package com.skyd.imomoe.config

import com.skyd.imomoe.App
import com.skyd.imomoe.util.editor
import com.skyd.imomoe.util.sharedPreferences

interface Api {
    companion object {
        const val DEFAULT_MAIN_URL = "http://www.yhdm.io"
        var MAIN_URL
            get() = App.context.sharedPreferences("url")
                .getString("mainUrl", DEFAULT_MAIN_URL) ?: DEFAULT_MAIN_URL
            set(value) {
                App.context.sharedPreferences("url").editor {
                    putString("mainUrl", value)
                }
            }

        //github
        const val CHECK_UPDATE_URL = "https://api.github.com/repos/SkyD666/Imomoe/releases/latest"

        //gitee
        const val CHECK_UPDATE_URL_2 =
            "https://gitee.com/api/v5/repos/SkyD666/Imomoe/releases/latest"

        //弹幕url
        const val DANMU_URL = "https://yuan.cuan.la/barrage/api"

    }
}