package com.su.mediabox.bean

import android.graphics.drawable.Drawable

data class PluginInfo(
    val packageName: String,
    val pageActivity: String,
    val name: String,
    val icon: Drawable,
    val sourcePath: String,
    val signature: String,
)