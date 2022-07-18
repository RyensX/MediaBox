package com.su.mediabox.view.activity

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.database.getOfflineDatabase
import com.su.mediabox.databinding.ActvityMediaDataBinding
import com.su.mediabox.databinding.TabMediaDataUpdateBinding
import com.su.mediabox.util.unsafeLazy
import com.su.mediabox.util.viewBind
import com.su.mediabox.viewmodel.MediaDataViewModel
import com.su.mediabox.view.fragment.BaseFragment
import com.su.mediabox.view.fragment.page.MediaFavoriteDataPageFragment
import com.su.mediabox.view.fragment.page.MediaHistoryDataPageFragment
import com.su.mediabox.view.fragment.page.MediaUpdateDataPageFragment

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
        setSupportActionBar(mBinding.mediaDataBack)
        mBinding.mediaDataBack.setNavigationOnClickListener { finish() }

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

                getOfflineDatabase().mediaUpdateDao().getUnConfirmedMediaUpdateRecordCountLiveData()
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

