package com.su.mediabox.view.activity

import android.content.Intent
import android.os.Bundle
import com.su.mediabox.R
import com.su.mediabox.databinding.ActivityPluginBinding
import com.su.mediabox.view.activity.BasePluginActivity.Companion.PLUGIN_NAME

class StartActivity : BaseActivity<ActivityPluginBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plugin)
        //TODO 暂时这样，其他部分还未准备完
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra(PLUGIN_NAME, "CustomDataSourceSample1.mpp")
        })
        finish()
        overridePendingTransition(0, 0)
    }

    override fun getBinding() = ActivityPluginBinding.inflate(layoutInflater)
}