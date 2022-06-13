package com.su.mediabox.model

import androidx.appcompat.content.res.AppCompatResources
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.model.PluginInfo

class PreviewPluginInfo :
    PluginInfo(
        1, "", "", "", "",
        AppCompatResources.getDrawable(App.context, R.mipmap.ic_mediabox)!!,
        "", ""
    ) {
    val iconBase64 = ""
    val repoUrl = ""
    val repoDesc = ""

    var state: Int = 0

    fun mergeLocalData(pluginInfo: PluginInfo) {
        icon = pluginInfo.icon
        apiImpl = pluginInfo.apiImpl
        sourcePath = pluginInfo.sourcePath
        signature = pluginInfo.signature
    }
}