package com.su.mediabox.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.su.mediabox.PluginManager
import com.su.mediabox.databinding.ActivityPluginBinding
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
    }

    override fun getBinding() = ActivityPluginBinding.inflate(layoutInflater)
}