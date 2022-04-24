package com.su.mediabox.bean

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import com.su.mediabox.App
import com.su.mediabox.R

data class PluginInfo(
    val apiVersion: Int,
    val apiImpl: String,
    val packageName: String,
    val name: String,
    //这里获取的是versionName
    val version: String,
    val icon: Drawable,
    val sourcePath: String,
    val signature: String,
    var isExternalPlugin: Boolean = false
) {

    @SuppressLint("UseCompatLoadingForDrawables")
    constructor() : this(
        1, "", "", "", "",
        App.context.getDrawable(R.mipmap.ic_mediabox)!!, "", ""
    )

    //当前绑定插件标识，目前为插件包名
    val id: String
        get() = "$packageName#$signature"
}