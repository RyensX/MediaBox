package com.su.mediabox.plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
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

    private inline fun <T : Action, reified A : Activity> T.routeToComponentPage(context: Context) {
        context.goActivity<A>(Intent().putAction(this))
    }
}