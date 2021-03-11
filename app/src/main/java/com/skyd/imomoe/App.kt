package com.skyd.imomoe

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.liulishuo.filedownloader.FileDownloader
import com.skyd.imomoe.util.CrashHandler
import com.skyd.imomoe.util.sharedPreferences
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this

        // Crash提示
        CrashHandler.getInstance(this)

        // Bugly APP ID
        CrashReport.initCrashReport(applicationContext, "07cdf10759", true)

        // 友盟
        // 初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
        UMConfigure.init(
            this,
            "6049a8dbb8c8d45c1395e180",
            "Github",
            UMConfigure.DEVICE_TYPE_PHONE,
            null
        )
        // 选择AUTO页面采集模式，统计SDK基础指标无需手动埋点可自动采集。
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

        FileDownloader.setup(this)

        //夜间模式
        AppCompatDelegate.setDefaultNightMode(
            if (sharedPreferences("nightMode").getBoolean("isNightMode", false))
                AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        lateinit var context: Context
    }
}