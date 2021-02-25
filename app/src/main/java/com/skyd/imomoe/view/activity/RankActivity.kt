package com.skyd.imomoe.view.activity

import android.animation.ObjectAnimator
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.util.GridRecyclerView1ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.ViewHolderUtil
import com.skyd.imomoe.util.ViewHolderUtil.Companion.GRID_RECYCLER_VIEW_1
import com.skyd.imomoe.view.adapter.AnimeDetailAdapter
import com.skyd.imomoe.view.adapter.AnimeShowAdapter
import com.skyd.imomoe.view.fragment.EverydayAnimeFragment
import com.skyd.imomoe.viewmodel.AnimeDetailViewModel
import com.skyd.imomoe.viewmodel.RankViewModel
import kotlinx.android.synthetic.main.activity_rank.*
import kotlinx.android.synthetic.main.fragment_everyday_anime.*
import kotlinx.android.synthetic.main.layout_toolbar_1.*

class RankActivity : BaseActivity() {
    private lateinit var viewModel: RankViewModel
    private lateinit var adapter: EverydayAnimeFragment.Vp2Adapter
    private var offscreenPageLimit = 1
    private var selectedTabIndex = -1
    private var lastRefreshTime: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)

        viewModel = ViewModelProvider(this).get(RankViewModel::class.java)

        tv_toolbar_1_title.text = getString(R.string.rank_list)
        iv_toolbar_1_back.setOnClickListener { finish() }

        vp2_rank_activity.offscreenPageLimit = offscreenPageLimit
        srl_rank_activity.setColorSchemeResources(R.color.main_color)
        srl_rank_activity.setOnRefreshListener {
            //避免刷新间隔太短
            if (System.currentTimeMillis() - lastRefreshTime > 500) {
                lastRefreshTime = System.currentTimeMillis()
                viewModel.getRankData()
            } else {
                srl_rank_activity.isRefreshing = false
            }
        }

        tl_rank_activity.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedTabIndex = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }

        })

        viewModel.mldRankData.observe(this, Observer {
            val selectedTabIndex = this.selectedTabIndex
            srl_rank_activity.isRefreshing = false

            //先隐藏
            ObjectAnimator.ofFloat(ll_rank_activity, "alpha", 1f, 0f)
                .setDuration(270).start()
            //总动漫排名显示排序序号
            val showRankNumber = BooleanArray(2)
            if (viewModel.rankList.size == 2) {
                showRankNumber[0] = false
                showRankNumber[1] = true
            }
            //添加rv
            adapter = EverydayAnimeFragment.Vp2Adapter(this, viewModel.rankList, showRankNumber)
            vp2_rank_activity.adapter = adapter

            val tabLayoutMediator = TabLayoutMediator(
                tl_rank_activity, vp2_rank_activity
            ) { tab, position ->
                tab.text = viewModel.tabList[position].title
            }
            tabLayoutMediator.attach()

            if (selectedTabIndex < tl_rank_activity.tabCount)
                vp2_rank_activity.setCurrentItem(selectedTabIndex, false)

            //设置完数据后显示，避免闪烁
            ObjectAnimator.ofFloat(ll_rank_activity, "alpha", 0f, 1f)
                .setDuration(270).start()
        })

        srl_rank_activity.isRefreshing = true
        viewModel.getRankData()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_left_out)
    }
}