package com.su.mediabox.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.su.mediabox.databinding.ActivityPluginBinding
import com.su.mediabox.view.adapter.PluginAdapter
import com.su.mediabox.viewmodel.PluginViewModel

class StartActivity : BaseActivity<ActivityPluginBinding>() {

    private val pluginViewModel by viewModels<PluginViewModel>()
    private val adapter = PluginAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.apply {
            setSupportActionBar(startPluginBar)
            startPluginList.layoutManager = GridLayoutManager(this@StartActivity, 4)
            startPluginList.adapter = adapter
        }

        pluginViewModel.pluginLiveData.observe(this) {
            adapter.submitList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        pluginViewModel.scanPlugin(packageManager)
    }

    override fun getBinding() = ActivityPluginBinding.inflate(layoutInflater)
}