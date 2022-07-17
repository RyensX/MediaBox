package com.su.mediabox.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.databinding.ActvityMediaDataBinding
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

        viewModel.mediaUpdateDataComponent?.also {
            pages.add(
                Pair(getString(R.string.media_data_page_update), MediaUpdateDataPageFragment())
            )
        }
        mBinding.mediaDataPages.setAdapter(ViewPageAdapter(this, pages))
        tlm.apply { if (!isAttached) attach() }

        viewModel.updateState.observe(this) {
            mBinding.mediaDataPagerUpdate.isVisible = it
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

