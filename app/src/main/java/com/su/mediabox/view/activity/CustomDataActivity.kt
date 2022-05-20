package com.su.mediabox.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.su.mediabox.App
import com.su.mediabox.databinding.ActivityCustomDataBinding
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.action.CustomPageAction
import com.su.mediabox.pluginapi.components.ICustomPageDataComponent
import com.su.mediabox.util.Util
import com.su.mediabox.util.viewBind

class CustomDataActivity : PageLoadActivity() {

    private val mBinding by viewBind(ActivityCustomDataBinding::inflate)

    override val refreshLayout get() = mBinding.customDataSwipe
    override val dataListView get() = mBinding.customDataList

    companion object {
        var action: CustomPageAction? = null
    }

    private val customPageComponent by lazy(LazyThreadSafetyMode.NONE) {
        val customComponentClazz =
            action?.targetPageComponent as? Class<ICustomPageDataComponent>
        customComponentClazz?.let {
            Util.withoutExceptionGet {
                PluginManager.acquireComponent(it)
            }
        }
    }

    private val menus by lazy(LazyThreadSafetyMode.NONE) {
        customPageComponent!!.menus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (customPageComponent == null) {
            Toast.makeText(App.context, "找不到对应自定义页面数据组件", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        mBinding.customDataBar.apply {
            setSupportActionBar(this)
            if (customPageComponent!!.isShowBack()) {
                setNavigationOnClickListener { finish() }
            } else {
                navigationIcon = null
            }
        }
        title = customPageComponent!!.pageName
    }

    override suspend fun load(page: Int) = customPageComponent!!.getData(page)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menus?.forEach {
            if (it.extraData is String)
                menu.add(it.extraData as String)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menus?.forEach {
            if (it.extraData == item.title.toString())
                it.go(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        action = null
        super.onDestroy()
    }
}