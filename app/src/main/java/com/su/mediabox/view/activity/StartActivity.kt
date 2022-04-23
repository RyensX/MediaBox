package com.su.mediabox.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Html
import android.view.*
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.R
import com.su.mediabox.bean.PluginInfo
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ActivityPluginBinding
import com.su.mediabox.util.Util
import com.su.mediabox.util.goActivity
import com.su.mediabox.util.showToast
import com.su.mediabox.util.update.AppUpdateHelper
import com.su.mediabox.util.update.AppUpdateStatus
import com.su.mediabox.v2.view.activity.PluginInstallerActivity
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.viewcomponents.ItemPluginViewHolder

class StartActivity : BaseActivity<ActivityPluginBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.apply {
            setSupportActionBar(startPluginBar)
            startPluginList.grid(4)
                .initTypeList(DataViewMapList().registerDataViewMap<PluginInfo, ItemPluginViewHolder>()) {}
        }

        PluginManager.pluginLiveData.observe(this) {
            if (it.isEmpty())
                showLoadFailedTip(Html.fromHtml("""<p>没有插件！前往<a href="https://github.com/RyensX/MediaBoxPlugin">插件API</a>查看示例</p>""")) {}
            else
                mBinding.startPluginList.submitList(it)
        }

        //检测更新
        AppUpdateHelper.instance.apply {
            getUpdateStatus().observe(this@StartActivity) {
                if (it == AppUpdateStatus.DATED)
                    noticeUpdate(this@StartActivity)
            }
            checkUpdate()
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

        //自动刷新
        listenInstallBroadcasts()
    }

    override fun onResume() {
        PluginManager.initPluginEnv()
        super.onResume()
    }

    private val installBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            PluginManager.scanPlugin()
        }
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

    override fun getBinding() = ActivityPluginBinding.inflate(layoutInflater)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.start_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.start_menu_skin -> goActivity<SkinActivity>()
            R.id.start_menu_settings -> goActivity<SettingActivity>()
            R.id.start_menu_about -> goActivity<AboutActivity>()
        }
        return true
    }

    override fun getLoadFailedTipView(): ViewStub = mBinding.startPluginEmpty
}