package com.skyd.imomoe.view.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityRankBinding
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.view.fragment.EverydayAnimeFragment
import com.skyd.imomoe.viewmodel.RankViewModel

class RankActivity : BaseActivity<ActivityRankBinding>() {
    private lateinit var viewModel: RankViewModel
    private lateinit var adapter: EverydayAnimeFragment.Vp2Adapter
    private var offscreenPageLimit = 1
    private var selectedTabIndex = -1
    private var lastRefreshTime: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(RankViewModel::class.java)

        mBinding.run {
            llRankActivityToolbar.tvToolbar1Title.text = getString(R.string.rank_list)
            llRankActivityToolbar.ivToolbar1Back.setOnClickListener { finish() }

            vp2RankActivity.setOffscreenPageLimit(offscreenPageLimit)
            srlRankActivity.setColorSchemeResources(Util.getSkinResourceId(R.color.main_color_skin))
            srlRankActivity.setOnRefreshListener {
                //避免刷新间隔太短
                if (System.currentTimeMillis() - lastRefreshTime > 500) {
                    lastRefreshTime = System.currentTimeMillis()
                    viewModel.getRankData()
                } else {
                    srlRankActivity.isRefreshing = false
                }
            }

            tlRankActivity.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    selectedTabIndex = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                }

            })
        }


        viewModel.mldRankData.observe(this, Observer {
            mBinding.srlRankActivity.isRefreshing = false
            // 如果初始化过了，再对其操作，否则会crash
            if (this::adapter.isInitialized) adapter.notifyDataSetChanged()

            if (it) {
                hideLoadFailedTip()

                val selectedTabIndex = this.selectedTabIndex

                //先隐藏
                ObjectAnimator.ofFloat(mBinding.llRankActivity, "alpha", 1f, 0f)
                    .setDuration(270).start()
                //总动漫排名显示排序序号
                val showRankNumber = BooleanArray(2)
                if (viewModel.rankList.size == 2) {
                    showRankNumber[0] = false
                    showRankNumber[1] = true
                }
                //添加rv
                if (!this::adapter.isInitialized) {
                    adapter =
                        EverydayAnimeFragment.Vp2Adapter(this, viewModel.rankList, showRankNumber)
                    mBinding.vp2RankActivity.setAdapter(adapter)
                }

                val tabLayoutMediator = TabLayoutMediator(
                    mBinding.tlRankActivity, mBinding.vp2RankActivity.getViewPager()
                ) { tab, position ->
                    tab.text = viewModel.tabList[position].title
                }
                tabLayoutMediator.attach()

                if (selectedTabIndex < mBinding.tlRankActivity.tabCount)
                    mBinding.vp2RankActivity.setCurrentItem(selectedTabIndex, false)

                //设置完数据后显示，避免闪烁
                ObjectAnimator.ofFloat(mBinding.llRankActivity, "alpha", 0f, 1f)
                    .setDuration(270).start()
            } else {
                showLoadFailedTip(getString(R.string.load_data_failed_click_to_retry),
                    View.OnClickListener {
                        viewModel.getRankData()
                        hideLoadFailedTip()
                    }
                )
            }
            viewModel.isRequesting = false
        })

        mBinding.srlRankActivity.isRefreshing = true
        viewModel.getRankData()
    }

    override fun getBinding(): ActivityRankBinding = ActivityRankBinding.inflate(layoutInflater)

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_left_out)
    }

    override fun getLoadFailedTipView(): ViewStub? = mBinding.layoutRankActivityLoadFailed
}
