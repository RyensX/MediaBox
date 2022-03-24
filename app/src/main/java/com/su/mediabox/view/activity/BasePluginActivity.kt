package com.su.mediabox.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.plugin.PluginManager.getPluginIndex
import com.su.mediabox.plugin.PluginManager.setPluginInfo

abstract class BasePluginActivity<VB : ViewBinding> : BaseActivity<VB>() {

    companion object {
        const val PLUGIN_INFO_INDEX = "pluginIndex"
    }

    private val pluginInfoViewModel by viewModels<PluginInfoViewModel>()

    /**
     * 传染式传递当前所属插件
     *
     * 根据打开插件时第一个传递的[PLUGIN_INFO_INDEX]决定
     */
    override fun startActivity(intent: Intent?, options: Bundle?) {
        intent?.apply {
            getPluginIndex().also {
                if (it != -1)
                    setPluginInfo(it)
            }
        }
        super.startActivity(intent, options)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //接入路由控制
        AppRouteProcessor.init(this)
        //在重建时恢复数据
        val index = getPluginIndex()
        if (index == -1)
            intent.setPluginInfo(pluginInfoViewModel.index)
        else
            pluginInfoViewModel.index = index
    }

    override fun onResume() {
        super.onResume()
        //接入路由控制
        AppRouteProcessor.updateTarget(this)
    }

    class PluginInfoViewModel : ViewModel() {
        //虽然目前只是简单的数据
        var index: Int = -1
    }
}