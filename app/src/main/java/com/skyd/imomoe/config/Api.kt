package com.skyd.imomoe.config

import com.skyd.imomoe.model.DataSourceManager

interface Api {
    companion object {
        val MAIN_URL
            get() = (DataSourceManager.getConst() ?: com.skyd.imomoe.model.impls.Const()).MAIN_URL()

        // github
        const val CHECK_UPDATE_URL = "https://api.github.com/repos/SkyD666/Imomoe/releases/latest"

        // 弹幕url
        const val DANMU_URL = "https://yuan.cuan.la/barrage/api"

        // DoH
        const val DOH_URL = "https://1.0.0.1/dns-query"
    }
}