package com.su.mediabox

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.liulishuo.filedownloader.FileDownloader
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.su.appcrashhandler.AppCatchException
import com.su.mediabox.plugin.AppAction
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.util.AppUtil
import com.su.mediabox.pluginapi.util.WebUtilIns
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.util.html.WebUtilImpl
import com.su.mediabox.util.release


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this

        AppUtil.init(this)
        AppAction.init()
        WebUtilIns = WebUtilImpl

        PluginManager.scanPlugin()

        Pref.appLaunchCount.apply {
            saveData(value + 1)
        }

        //AppCenter分析
        AppCenter.start(
            this, "6ec214bc-e6df-48d8-85e1-d2ee3b5d8522",
            Analytics::class.java, Crashes::class.java
        )
        AppCenter.setLogLevel(Log.DEBUG)

        Analytics.trackEvent("应用启动")

        release {
            // Crash提示
            AppCatchException.bindCrashHandler(this)
        }

        FileDownloader.setup(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        init {
            // 防止内存泄漏
            // 设置全局默认配置（优先级最低，会被其他设置覆盖）
            SmartRefreshLayout.setDefaultRefreshInitializer { context, layout -> //开始设置全局的基本参数（可以被下面的DefaultRefreshHeaderCreator覆盖）
                layout.setReboundDuration(150)
                layout.setFooterHeight(100f)
                layout.setHeaderTriggerRate(0.5f)
                layout.setDisableContentWhenLoading(false)
            }

            // 全局设置默认的 Header
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout -> //开始设置全局的基本参数（这里设置的属性只跟下面的MaterialHeader绑定，其他Header不会生效，能覆盖DefaultRefreshInitializer的属性和Xml设置的属性）
                layout.setEnableHeaderTranslationContent(true)
                    .setHeaderHeight(70f)
                    .setDragRate(0.6f)
                MaterialHeader(context).setColorSchemeResources(R.color.main_color_2_skin)
                    .setShowBezierWave(true)
            }

            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                layout.setEnableFooterTranslationContent(true)
                BallPulseFooter(context).setAnimatingColor(getResColor(R.color.main_color_2_skin))
            }
        }
    }
}