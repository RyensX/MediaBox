package com.su.mediabox.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import com.su.mediabox.PluginManager
import com.su.mediabox.R
import com.su.mediabox.databinding.ActivityPluginBinding
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
            adapter.submitList(it)
        }
        PluginManager.scanPlugin(packageManager)

        AppUpdateHelper.instance.apply {
            getUpdateStatus().observe(this@StartActivity) {
                noticeUpdate(this@StartActivity)
            }
            checkUpdate()
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
}