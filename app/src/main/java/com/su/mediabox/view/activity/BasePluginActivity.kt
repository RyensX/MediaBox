package com.su.mediabox.view.activity

import android.app.ActivityManager
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import com.su.mediabox.plugin.PluginManager

abstract class BasePluginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //图标及名称同步
        PluginManager.currentLaunchPlugin.observe(this) {
            it ?: return@observe
            val description = ActivityManager.TaskDescription(it.name, it.icon.toBitmap())
            description.label
            setTaskDescription(description)
        }
    }
}