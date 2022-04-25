package com.su.mediabox.bean

import android.graphics.drawable.Drawable

open class PluginInfo(
    val apiVersion: Int,
    val apiImpl: String,
    var packageName: String,
    val name: String,
    //这里获取的是versionName
    val version: String,
    val icon: Drawable,
    val sourcePath: String,
    var signature: String,
    var isExternalPlugin: Boolean = false
) {

    //当前绑定插件标识，目前为插件包名
    val id: String
        get() = "$packageName#$signature"
}