package com.su.mediabox.plugin

import android.app.Activity
import android.content.Intent
import com.su.mediabox.pluginapi.v2.action.*
import com.su.mediabox.util.goActivity
import com.su.mediabox.util.putAction
import com.su.mediabox.v2.view.activity.VideoDetailActivity
import com.su.mediabox.v2.view.activity.VideoMediaPlayActivity
import com.su.mediabox.v2.view.activity.VideoSearchActivity
import com.su.mediabox.view.activity.ClassifyActivity
import com.su.mediabox.view.activity.WebViewActivity
import kotlin.concurrent.thread

/**
 * 内置数据动作
 */
object AppAction {

    //初始化内置动作
    fun init() {
        DetailAction.GO = {
            routeToComponentPage<DetailAction, VideoDetailActivity>()
        }
        PlayAction.GO = {
            routeToComponentPage<PlayAction, VideoMediaPlayActivity>()
        }
        ClassifyAction.GO = {
            routeToComponentPage<ClassifyAction, ClassifyActivity>()
        }
        SearchAction.GO = {
            routeToComponentPage<SearchAction, VideoSearchActivity>()
        }
        WebBrowserAction.GO = {
            routeToComponentPage<WebBrowserAction, WebViewActivity>()
        }
    }

    private inline fun <T : Action, reified A : Activity> T.routeToComponentPage() {
        AppRouteProcessor.currentActivity?.get()
            ?.goActivity<A>(Intent().putAction(this))
    }
}