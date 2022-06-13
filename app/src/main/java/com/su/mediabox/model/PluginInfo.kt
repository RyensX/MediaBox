package com.su.mediabox.model

import android.graphics.drawable.Drawable

open class PluginInfo(
    var apiVersion: Int,
    var apiImpl: String,
    var packageName: String,
    var name: String,
    //这里获取的是versionName
    var version: String,
    var icon: Drawable,
    var sourcePath: String,
    var signature: String,
    var isExternalPlugin: Boolean = false
) {

    //当前绑定插件标识，目前为插件包名
    val id: String
        get() = "$packageName#$signature"
}