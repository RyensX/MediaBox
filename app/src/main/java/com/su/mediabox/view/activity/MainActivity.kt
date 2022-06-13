package com.su.mediabox.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.R
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ActivityMainBinding
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.util.Util
import com.su.mediabox.util.bindBottomNavigationView
import com.su.mediabox.util.update.AppUpdateHelper
import com.su.mediabox.util.update.AppUpdateStatus
import com.su.mediabox.util.viewBind
import com.su.mediabox.view.fragment.page.DownloadPageFragment
import com.su.mediabox.view.fragment.page.ExplorePageFragment
import com.su.mediabox.view.fragment.page.PluginRepoPageFragment
import com.su.mediabox.view.fragment.page.SettingsPageFragment

class MainActivity : BaseActivity() {

    private val viewBinding by viewBind(ActivityMainBinding::inflate)

    private val pages = listOf(
        ExplorePageFragment(),
        PluginRepoPageFragment(),
        DownloadPageFragment(),
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
            mainPagers.apply {
                val pageAdapter = PageAdapter()
                offscreenPageLimit = pageAdapter.itemCount - 1
                adapter = pageAdapter
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                bindBottomNavigationView(mainBottomNav)
            }
        }

        //检测更新
        AppUpdateHelper.instance.checkUpdate()

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

        //自动刷新
        listenInstallBroadcasts()
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

    override fun onDestroy() {
        unregisterReceiver(installBroadcastReceiver)
        super.onDestroy()
    }

    private inner class PageAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = pages.size
        override fun createFragment(position: Int): Fragment = pages[position]
    }
}