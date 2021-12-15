package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.ViewStub
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityRankBinding
import com.skyd.imomoe.view.fragment.RankFragment
import com.skyd.imomoe.viewmodel.RankViewModel

class RankActivity : BaseActivity<ActivityRankBinding>() {
    private lateinit var viewModel: RankViewModel
    private lateinit var adapter: VpAdapter
    private var offscreenPageLimit = 1
    private var selectedTabIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(RankViewModel::class.java)

        adapter = VpAdapter(this)
        mBinding.run {
            atbRankActivityToolbar.setBackButtonClickListener { finish() }

            vp2RankActivity.setOffscreenPageLimit(offscreenPageLimit)

            tlRankActivity.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    selectedTabIndex = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                }

            })

            //添加rv
            vp2RankActivity.setAdapter(adapter)
            val tabLayoutMediator = TabLayoutMediator(
                tlRankActivity, vp2RankActivity.getViewPager()
            ) { tab, position ->
                if (position < viewModel.tabList.size)
                    tab.text = viewModel.tabList[position].title
            }
            tabLayoutMediator.attach()
        }


        viewModel.mldRankData.observe(this, Observer {
            adapter.clearAllFragment()
            if (it) {
                hideLoadFailedTip()
                viewModel.tabList.size.let { size ->
                    if (size > 0) mBinding.vp2RankActivity.setOffscreenPageLimit(size)
                }
                for (i in viewModel.tabList.indices) {
                    val fragment = RankFragment()
                    val bundle = Bundle()
                    bundle.putString("partUrl", viewModel.tabList[i].actionUrl)
                    fragment.arguments = bundle
                    adapter.addFragment(fragment)
                }
            } else {
                showLoadFailedTip(getString(R.string.load_data_failed_click_to_retry)) {
                    viewModel.getRankTabData()
                    hideLoadFailedTip()
                }
            }
            adapter.notifyDataSetChanged()
            viewModel.isRequesting = false
        })

        viewModel.getRankTabData()
    }

    override fun getBinding(): ActivityRankBinding = ActivityRankBinding.inflate(layoutInflater)

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_left_out)
    }

    override fun getLoadFailedTipView(): ViewStub? = mBinding.layoutRankActivityLoadFailed

    class VpAdapter : FragmentStateAdapter {

        constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity)

        constructor(fragment: Fragment) : super(fragment)

        private val fragments = mutableListOf<RankFragment>()

        fun clearAllFragment() {
            fragments.clear()
        }

        fun addFragment(fragment: RankFragment) {
            fragments.add(fragment)
        }

        override fun getItemCount() = fragments.size

        override fun createFragment(position: Int) = fragments[position]
    }
}
