package com.su.mediabox.view.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.microsoft.appcenter.analytics.Analytics
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.database.getOfflineDatabase
import com.su.mediabox.databinding.ActvityMediaDataBinding
import com.su.mediabox.databinding.TabMediaDataUpdateBinding
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.plugin.PluginManager.launchPlugin
import com.su.mediabox.util.*
import com.su.mediabox.viewmodel.MediaDataViewModel
import com.su.mediabox.view.fragment.BaseFragment
import com.su.mediabox.view.fragment.page.MediaFavoriteDataPageFragment
import com.su.mediabox.view.fragment.page.MediaHistoryDataPageFragment
import com.su.mediabox.view.fragment.page.MediaUpdateDataPageFragment
import com.su.mediabox.work.MEDIA_UPDATE_CHECK_TARGET_PLUGIN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MediaDataActivity : BasePluginActivity() {

    private val mBinding by viewBind(ActvityMediaDataBinding::inflate)
    private val viewModel by viewModels<MediaDataViewModel>()

    private val tlm by unsafeLazy {
        TabLayoutMediator(
            mBinding.mediaDataPagerTabs,
            mBinding.mediaDataPages.getViewPager()
        ) { tab, pos ->
            tab.text = pages[pos].first
        }
    }

    private val pages: MutableList<Pair<String, BaseFragment>> by unsafeLazy {
        mutableListOf(
            Pair(getString(R.string.media_data_page_favorite), MediaFavoriteDataPageFragment()),
            Pair(getString(R.string.media_data_page_history), MediaHistoryDataPageFragment()),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics.trackEvent("功能：媒体记录管理")

        setSupportActionBar(mBinding.mediaDataBack)
        mBinding.mediaDataBack.setNavigationOnClickListener { finish() }

        //从更新通知中启动
        intent.getStringExtra(MEDIA_UPDATE_CHECK_TARGET_PLUGIN)?.also { pluginPackageName ->
            lifecycleScope.launch(Dispatchers.IO) {
                logI("更新通知打开", pluginPackageName)
                //由于可能是冷启动，所以需要等待插件扫描完毕
                PluginManager.pluginFlow.first()
                PluginManager.queryPluginInfo(pluginPackageName)?.also {
                    launchPlugin(it, false)
                }
                launch(Dispatchers.Main) { initUI() }
            }
        } ?: initUI()

    }

    private fun initUI() {
        mBinding.mediaDataPluginName.displayOnlyIfHasData(PluginManager.currentLaunchPlugin.value?.name) {
            text = it
        }

        //只有存在媒体更新组件才显示
        viewModel.mediaUpdateDataComponent?.also {
            pages.add(
                Pair(getString(R.string.media_data_page_update), MediaUpdateDataPageFragment())
            )
        }
        mBinding.mediaDataPages.setAdapter(ViewPageAdapter(this, pages))
        tlm.apply { if (!isAttached) attach() }

        viewModel.updateCount.observe(this) {
            mBinding.apply {
                mediaDataPagerUpdate.isVisible = it > 0
                mediaDataPagerUpdateHint.apply {
                    isVisible = it > 0
                    text = getString(R.string.media_data_page_update_check_hint_format, it)
                }
            }
        }


        mBinding.mediaDataPagerTabs.run { getTabAt(tabCount - 1) }?.apply {
            if (text == getString(R.string.media_data_page_update)) {
                val tabBinding = TabMediaDataUpdateBinding.inflate(layoutInflater)

                tabBinding.tabUpdateTitle.text = text
                customView = tabBinding.root

                getOfflineDatabase().mediaUpdateDao().getUnConfirmedMediaUpdateRecordCountFlow().asLiveData()
                    .observe(this@MediaDataActivity) {
                        tabBinding.tabUpdateCount.text = it.toString()
                    }
                mBinding.mediaDataPagerTabs.addOnTabSelectedListener(object :
                    TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {}
                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        if (tab == this@apply)
                            viewModel.checkMediaUpdate()
                    }
                })
            }
        }

        if (intent.getStringExtra(MEDIA_UPDATE_CHECK_TARGET_PLUGIN) == null) {
            if (viewModel.mediaUpdateDataComponent != null)
                Snackbar.make(mBinding.root, R.string.media_update_check, Snackbar.LENGTH_LONG)
                    .setAction(R.string.media_data_page_update) {
                        viewModel.checkMediaUpdate()
                    }
                    .show()
        } else {
            if (viewModel.mediaUpdateDataComponent != null)
                mBinding.mediaDataPages.getViewPager().currentItem =
                    mBinding.mediaDataPagerTabs.tabCount - 1
        }
    }

    private class ViewPageAdapter(
        fragmentActivity: FragmentActivity,
        val pages: List<Pair<String, BaseFragment>>
    ) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = pages.size

        override fun createFragment(position: Int) = pages[position].second
    }

}

