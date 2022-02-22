package com.su.mediabox.config

import com.su.mediabox.PluginManager
import com.su.mediabox.pluginapi.components.IConstComponent

interface Api {
    companion object {

        //这里不能写死
        val MAIN_URL: String
            get() = PluginManager.acquireComponent(IConstComponent::class.java).host

        val refererProcessor
            get() = PluginManager.acquireComponent(IConstComponent::class.java).refererProcessor

        // github
        const val CHECK_UPDATE_URL = "https://api.github.com/repos/RyensX/MediaBox/releases/latest"

        // 弹幕url
        const val DANMU_URL = "https://yuan.cuan.la/barrage/api"

        // DoH
        const val DOH_URL = "https://1.0.0.1/dns-query"
    }
}