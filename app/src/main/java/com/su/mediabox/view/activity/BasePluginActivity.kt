package com.su.mediabox.view.activity

import android.app.ActivityManager
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.plugin.PluginManager.launchPlugin
import com.su.mediabox.pluginapi.action.Action
import com.su.mediabox.util.actionPoolMap
import com.su.mediabox.util.getAction
import com.su.mediabox.util.showToast

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
        //自动绑定插件
        runCatching {
            actionPoolMap.values.last().also {
                if (PluginManager.currentLaunchPlugin.value == null && it.extraData is PluginInfo)
                    launchPlugin(it.extraData as PluginInfo, isLaunchInitAction = false)
            }
        }
    }
}