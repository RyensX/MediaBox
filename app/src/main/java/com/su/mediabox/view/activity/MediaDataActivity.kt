package com.su.mediabox.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.su.mediabox.R
import com.su.mediabox.databinding.ActvityMediaDataBinding
import com.su.mediabox.util.unsafeLazy
import com.su.mediabox.util.viewBind
import com.su.mediabox.viewmodel.MediaDataViewModel
import com.su.mediabox.view.fragment.BaseFragment
import com.su.mediabox.view.fragment.page.MediaFavoriteDataPageFragment
import com.su.mediabox.view.fragment.page.MediaHistoryDataPageFragment

class MediaDataActivity : BasePluginActivity() {

    private val mBinding by viewBind(ActvityMediaDataBinding::inflate)

    private val tlm by unsafeLazy {
        TabLayoutMediator(
            mBinding.mediaDataPagerTabs,
            mBinding.mediaDataPages.getViewPager()
        ) { tab, pos ->
            tab.text = pages[pos].first
        }
    }

    private val pages: Array<Pair<String, BaseFragment>> by unsafeLazy {
        arrayOf(
            Pair(getString(R.string.media_data_page_favorite), MediaFavoriteDataPageFragment()),
            Pair(getString(R.string.media_data_page_history), MediaHistoryDataPageFragment())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(mBinding.mediaDataBack)
        mBinding.mediaDataBack.setNavigationOnClickListener { finish() }
        mBinding.mediaDataPages.setAdapter(ViewPageAdapter(this, pages))
        tlm.apply { if (!isAttached) attach() }
    }

    private class ViewPageAdapter(
        fragmentActivity: FragmentActivity,
        val pages: Array<Pair<String, BaseFragment>>
    ) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = pages.size

        override fun createFragment(position: Int) = pages[position].second
    }

}

