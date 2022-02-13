package com.skyd.imomoe.config

import com.skyd.imomoe.PluginManager
import com.su.mediabox.plugin.interfaces.IConst

interface Api {
    companion object {

        //这里不能写死
        val MAIN_URL: String
            get() = PluginManager.acquireComponent(IConst::class.java).MAIN_URL()

        // github
        const val CHECK_UPDATE_URL = "https://api.github.com/repos/Ryensu/MediaBox/releases/latest"

        // 弹幕url
        const val DANMU_URL = "https://yuan.cuan.la/barrage/api"

        // DoH
        const val DOH_URL = "https://1.0.0.1/dns-query"
    }
}