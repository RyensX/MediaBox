package com.su.mediabox

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.efs.sdk.launch.LaunchManager
import com.liulishuo.filedownloader.FileDownloader
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.su.mediabox.plugin.AppAction
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.util.AppUtil
import com.su.mediabox.pluginapi.util.WebUtilIns
import com.su.mediabox.util.CrashHandler
import com.su.mediabox.util.PushHelper
import com.su.mediabox.util.Util.getManifestMetaValue
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.util.html.WebUtilImpl
import com.su.mediabox.util.release
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.PushAgent
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.entity.UMessage

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        LaunchManager.onTraceApp(this, LaunchManager.APP_ATTACH_BASE_CONTEXT, true)
        super.attachBaseContext(base)
        LaunchManager.onTraceApp(this, LaunchManager.APP_ATTACH_BASE_CONTEXT, false)
    }

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

        release {
            // Crash提示
            CrashHandler.getInstance(this)

            //TODO 满足工信部相关合规要求preInit
            // 友盟
            // 初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
            UMConfigure.init(
                this,
                getManifestMetaValue("UMENG_APPKEY"),
                getManifestMetaValue("UMENG_CHANNEL"),
                UMConfigure.DEVICE_TYPE_PHONE,
                getManifestMetaValue("UMENG_MESSAGE_SECRET")
            )
            UMConfigure.setLogEnabled(BuildConfig.DEBUG)

            // 选择AUTO页面采集模式，统计SDK基础指标无需手动埋点可自动采集。
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

            PushAgent.getInstance(context).apply {
                resourcePackageName = BuildConfig.APPLICATION_ID
                notificationClickHandler = object : UmengNotificationClickHandler() {
                    override fun dealWithCustomAction(context: Context, msg: UMessage) {
                        super.dealWithCustomAction(context, msg)
                        //TODO
                    }
                }
            }
            PushHelper.init(applicationContext)
            Thread { PushHelper.init(applicationContext) }.start()
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