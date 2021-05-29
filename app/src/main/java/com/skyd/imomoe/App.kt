package com.skyd.imomoe

import android.app.Application
import android.content.Context
import com.liulishuo.filedownloader.FileDownloader
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.skyd.imomoe.util.CrashHandler
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.setNightMode
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this

        // Crash提示
//        CrashHandler.getInstance(this)

        // Bugly APP ID
//        CrashReport.initCrashReport(applicationContext, BuildConfig.BUGLY_APP_ID, true)

        // 友盟
        // 初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
        UMConfigure.init(
            this,
            BuildConfig.UM_APP_KEY,
            "Github",
            UMConfigure.DEVICE_TYPE_PHONE,
            null
        )
        // 选择AUTO页面采集模式，统计SDK基础指标无需手动埋点可自动采集。
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

        FileDownloader.setup(this)

        // 夜间模式
        setNightMode()
    }

    companion object {
        lateinit var context: Context

        init {
            // 防止内存泄漏
            // 设置全局默认配置（优先级最低，会被其他设置覆盖）
            SmartRefreshLayout.setDefaultRefreshInitializer { context, layout -> //开始设置全局的基本参数（可以被下面的DefaultRefreshHeaderCreator覆盖）
                layout.setReboundDuration(150)
                layout.setFooterHeight(100f)
                layout.setHeaderTriggerRate(0.5f)
                layout.setDisableContentWhenLoading(false)
                layout.setPrimaryColorsId(R.color.main_color_3)
            }

            // 全局设置默认的 Header
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout -> //开始设置全局的基本参数（这里设置的属性只跟下面的MaterialHeader绑定，其他Header不会生效，能覆盖DefaultRefreshInitializer的属性和Xml设置的属性）
                layout.setEnableHeaderTranslationContent(true)
                    .setHeaderHeight(70f)
                    .setDragRate(0.6f)
                MaterialHeader(context).setColorSchemeResources(R.color.main_color)
                    .setShowBezierWave(true)
            }

            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                layout.setEnableFooterTranslationContent(true)
                BallPulseFooter(context).setAnimatingColor(
                    context.getResColor(R.color.foreground_main_color_2)
                )
            }
        }
    }
}