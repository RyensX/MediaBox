package com.su.mediabox.v2.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.su.mediabox.databinding.ActivityCustomDataBinding
import com.su.mediabox.pluginapi.v2.action.CustomDataAction
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.util.getAction

class CustomDataActivity : PageLoadActivity<ActivityCustomDataBinding>() {

    override val refreshLayout get() = mBinding.customDataSwipe
    override val dataListView get() = mBinding.customDataList

    companion object {
        var action: CustomDataAction? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.customDataBar.apply {
            setSupportActionBar(this)
            setNavigationOnClickListener { finish() }
        }
        title = action?.title
    }

    override suspend fun load(page: Int): List<BaseData>? {
        return action?.loader?.loadData(page)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        action?.actions?.forEach {
            if (it.extraData is String)
                menu.add(it.extraData as String)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        action?.actions?.forEach {
            if (it.extraData == item.title.toString())
                it.go()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getBinding() = ActivityCustomDataBinding.inflate(layoutInflater)

    override fun onDestroy() {
        action = null
        super.onDestroy()
    }
}