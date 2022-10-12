package com.su.mediabox.plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.plugin.PluginManager.launchPlugin
import com.su.mediabox.pluginapi.action.*
import com.su.mediabox.util.goActivity
import com.su.mediabox.util.putAction
import com.su.mediabox.view.activity.*

/**
 * 内置数据动作
 */
object AppAction {

    //初始化内置动作
    fun init() {
        HomeAction.GO = {
            it.goActivity<HomeActivity>()
        }
        DetailAction.GO = {
            routeToComponentPage<DetailAction, MediaDetailActivity>(it)
        }
        PlayAction.GO = {
            routeToComponentPage<PlayAction, VideoMediaPlayActivity>(it)
        }
        ClassifyAction.GO = {
            routeToComponentPage<ClassifyAction, MediaClassifyActivity>(it)
        }
        SearchAction.GO = {
            routeToComponentPage<SearchAction, MediaSearchActivity>(it)
        }
        WebBrowserAction.GO = {
            routeToComponentPage<WebBrowserAction, WebViewActivity>(it)
        }
        CustomPageAction.GO = {
            CustomDataActivity.action = this
            it.goActivity<CustomDataActivity>()
        }
    }

    var preRouteTargetPlugin: PluginInfo? = null

    private inline fun <T : Action, reified A : Activity> T.routeToComponentPage(context: Context) {
        //自动绑定插件
        runCatching {
            if (PluginManager.currentLaunchPlugin.value == null &&
                //TODO 这里仅仅对第一层数据有效，如果存在多层Action则无法自动启动插件，如搜索项的tag点击切换分类是无法正确启动的
                extraData is PluginInfo
            )
                context.launchPlugin(extraData as PluginInfo, isLaunchInitAction = false)
        }
        //开始路由
        putAction(this)
        context.goActivity<A>()
    }
}