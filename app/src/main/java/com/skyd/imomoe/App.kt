package com.skyd.imomoe

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.liulishuo.filedownloader.FileDownloader
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.skyd.imomoe.util.CrashHandler
import com.skyd.imomoe.util.PushHelper
import com.skyd.imomoe.util.Util.getManifestMetaValue
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.getSkinResourceId
import com.skyd.imomoe.util.release
import com.skyd.skin.core.attrs.SrlPrimaryColorAttr
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.PushAgent


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this

        release {
            // Crash提示
            CrashHandler.getInstance(this)

            // 友盟
            // 初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
            UMConfigure.init(
                this,
                getManifestMetaValue("UMENG_APPKEY"),
                getManifestMetaValue("UMENG_CHANNEL"),
                UMConfigure.DEVICE_TYPE_PHONE,
                BuildConfig.UMENG_MESSAGE_SECRET
            )
            UMConfigure.setLogEnabled(BuildConfig.DEBUG)

            // 选择AUTO页面采集模式，统计SDK基础指标无需手动埋点可自动采集。
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

            PushAgent.getInstance(context).resourcePackageName = BuildConfig.APPLICATION_ID
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
                val colorSchemeResources = R.color.main_color_skin
                SrlPrimaryColorAttr.materialHeaderColorSchemeRes = colorSchemeResources
                layout.setEnableHeaderTranslationContent(true)
                    .setHeaderHeight(70f)
                    .setDragRate(0.6f)
                MaterialHeader(context).setColorSchemeResources(
                    getSkinResourceId(
                        colorSchemeResources
                    )
                )
                    .setShowBezierWave(true)
            }

            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                val animatingColor = R.color.foreground_main_color_2_skin
                SrlPrimaryColorAttr.ballPulseFooterAnimatingColorRes = animatingColor
                layout.setEnableFooterTranslationContent(true)
                BallPulseFooter(context).setAnimatingColor(
                    context.getResColor(animatingColor)
                )
            }
        }
    }
}