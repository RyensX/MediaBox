package com.su.mediabox.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Html
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.microsoft.appcenter.analytics.Analytics
import com.su.mediabox.BuildConfig
import com.su.mediabox.Pref
import com.su.mediabox.R
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ActivityMainBinding
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.util.*
import com.su.mediabox.util.update.AppUpdateHelper
import com.su.mediabox.view.adapter.type.TypeAdapter
import com.su.mediabox.view.fragment.page.*
import com.su.mediabox.viewmodel.PluginUpdateViewModel
import com.su.mediabox.work.checkBatteryOptimizations


class MainActivity : BaseActivity() {

    private val viewBinding by viewBind(ActivityMainBinding::inflate)

    private val pluginUpdateVM by viewModels<PluginUpdateViewModel>()

    private val pages = listOf(
        ExplorePageFragment(),
        PluginRepoPageFragment(),
        MediaCombineSearchPageFragment(),
        SettingsPageFragment()
    )

    private val installBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            PluginManager.scanPlugin()
        }
    }

    override fun onResume() {
        PluginManager.initPluginEnv()
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding.apply {
            setSupportActionBar(mainBar)
            mainPagers.getViewPager().apply {
                val pageAdapter = PageAdapter()
                offscreenPageLimit = pageAdapter.itemCount - 1
                adapter = pageAdapter
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                bindBottomNavigationView(mainBottomNav)
            }

            //为插件仓库注入小红点提示
            pages.find { it.javaClass == PluginRepoPageFragment::class.java }
                ?.let { pages.indexOf(it) }?.also { pos ->
                    mainBottomNav.addBadge(pos)?.also { badge ->
                        pluginUpdateVM.repoAvailableData.observe(this@MainActivity) {
                            badge.backgroundTintList = ColorStateList.valueOf(it.second)
                            badge.text = it.first.toString()
                            badge.isVisible = it.first > 0
                        }
                    }
                }
        }

        //检测更新
        if (BuildConfig.DEBUG) {
            //测试版通道
            AppUpdateHelper.instance.checkDebugUpdate(this)
        } else {
            //正式版通道
            AppUpdateHelper.instance.checkUpdate()
        }

        //使用须知
        if (Util.lastReadUserNoticeVersion() < Const.Common.USER_NOTICE_VERSION) {
            MaterialDialog(this).show {
                title(res = R.string.user_notice_update)
                message(text = Html.fromHtml(Util.getUserNoticeContent()))
                cancelable(false)
                positiveButton(res = R.string.ok) {
                    Util.setReadUserNoticeVersion(Const.Common.USER_NOTICE_VERSION)
                }
            }
        }

        //支持开发
        Pref.appLaunchCount.apply {
            if (value == 10) {
                MaterialDialog(this@MainActivity).show {
                    title(res = R.string.support_title)
                    message(res = R.string.app_recommend)
                    cancelable(false)
                    positiveButton(text = "Github") { Util.openBrowser(Const.Common.GITHUB_URL) }
                    negativeButton(res = R.string.cancel) { dismiss() }
                    countdownActionButton(WhichButton.NEGATIVE, durationSeconds = 5)
                }
                saveData(value + 1)
            }
        }

        //自动刷新
        listenInstallBroadcasts()

        checkBatteryOptimizations()
    }

    private fun listenInstallBroadcasts() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        registerReceiver(installBroadcastReceiver, intentFilter)
    }


    override fun onStop() {
        Analytics.trackEvent("主界面停止")
        TypeAdapter.apply {
            clearAllTypeRecycledViewPool()
            globalTypeRecycledViewPool.clear()
        }
        super.onStop()
    }

    override fun onDestroy() {
        unregisterReceiver(installBroadcastReceiver)
        super.onDestroy()
    }

    private inner class PageAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = pages.size
        override fun createFragment(position: Int): Fragment = pages[position]
    }
}