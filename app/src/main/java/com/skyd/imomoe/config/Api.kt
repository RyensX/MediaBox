package com.skyd.imomoe.config

import com.skyd.imomoe.model.DataSourceManager

interface Api {
    companion object {
        const val DEFAULT_MAIN_URL = "http://www.yhdm.io"
        var MAIN_URL = DEFAULT_MAIN_URL
            get() = (DataSourceManager.getConst() ?: com.skyd.imomoe.model.impls.Const())
                .MAIN_URL()
            //                return App.context.sharedPreferences("url")
//                    .getString("mainUrl", DEFAULT_MAIN_URL) ?: DEFAULT_MAIN_URL
            private set/*(value) {
                App.context.sharedPreferences("url").editor {
                    putString("mainUrl", value)
                }
            }*/

        //github
        const val CHECK_UPDATE_URL = "https://api.github.com/repos/SkyD666/Imomoe/releases/latest"

        //gitee
        const val CHECK_UPDATE_URL_2 =
            "https://gitee.com/api/v5/repos/SkyD666/Imomoe/releases/latest"

        //弹幕url
        const val DANMU_URL = "https://yuan.cuan.la/barrage/api"

    }
}