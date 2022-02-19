package com.su.mediabox.view.activity

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.ViewStub
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.PluginManager
import com.su.mediabox.R
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ActivityPluginBinding
import com.su.mediabox.util.Util
import com.su.mediabox.util.goActivity
import com.su.mediabox.util.update.AppUpdateHelper
import com.su.mediabox.view.adapter.PluginAdapter

class StartActivity : BaseActivity<ActivityPluginBinding>() {

    private val adapter = PluginAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.apply {
            setSupportActionBar(startPluginBar)
            startPluginList.layoutManager = GridLayoutManager(this@StartActivity, 4)
            startPluginList.adapter = adapter
        }

        PluginManager.pluginLiveData.observe(this) {
            if (it.isEmpty())
                showLoadFailedTip(Html.fromHtml("""<p>没有插件！前往<a href="https://github.com/Ryensu/MediaBoxPlugin">插件API</a>查看示例</p>""")) {}
            else
                adapter.submitList(it)
        }
        PluginManager.scanPlugin(packageManager)

        AppUpdateHelper.instance.apply {
            getUpdateStatus().observe(this@StartActivity) {
                noticeUpdate(this@StartActivity)
            }
            checkUpdate()
        }

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