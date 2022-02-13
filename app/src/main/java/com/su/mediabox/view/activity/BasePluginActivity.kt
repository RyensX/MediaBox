package com.su.mediabox.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.su.mediabox.AppRouteProcessor
import com.su.mediabox.PluginManager.getPluginName
import com.su.mediabox.PluginManager.getPluginPath
import com.su.mediabox.PluginManager.setPluginInfo

abstract class BasePluginActivity<VB : ViewBinding> : BaseActivity<VB>() {

    companion object {
        const val PLUGIN_NAME = "pluginName"
        const val PLUGIN_PATH = "pluginPath"
    }

    /**
     * 传染式传递当前所属插件
     *
     * 根据打开插件时第一个传递的[PLUGIN_NAME]决定
     */
    override fun startActivity(intent: Intent?, options: Bundle?) {
        intent?.setPluginInfo(getPluginName(), getPluginPath())
        super.startActivity(intent, options)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //接入路由控制
        AppRouteProcessor.init(this)
    }

    override fun onResume() {
        super.onResume()
        //接入路由控制
        AppRouteProcessor.updateTarget(this)
    }
}