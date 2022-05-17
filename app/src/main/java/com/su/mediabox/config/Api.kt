package com.su.mediabox.config

import com.su.mediabox.plugin.PluginManager

interface Api {
    companion object {

        //这里不能写死
        val MAIN_URL: String
            get() = PluginManager.acquirePluginFactory().host

        val refererProcessor
            get() = PluginManager.acquirePluginFactory().imageRefererProcessor

        // github
        const val CHECK_UPDATE_URL = "https://api.github.com/repos/RyensX/MediaBox/releases/latest"

        // DoH
        const val DOH_URL = "https://1.0.0.1/dns-query"
    }
}