package com.skyd.imomoe.view.fragment

import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.config.Const.ViewHolderTypeInt
import com.skyd.imomoe.databinding.FragmentEverydayAnimeBinding
import com.skyd.imomoe.util.GridRecyclerView1ViewHolder
import com.skyd.imomoe.util.eventbus.EventBusSubscriber
import com.skyd.imomoe.util.eventbus.MessageEvent
import com.skyd.imomoe.util.eventbus.RefreshEvent
import com.skyd.imomoe.view.adapter.AnimeShowAdapter
import com.skyd.imomoe.view.adapter.SkinRvAdapter
import com.skyd.imomoe.view.component.WrapLinearLayoutManager
import com.skyd.imomoe.viewmodel.EverydayAnimeViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class EverydayAnimeFragment : BaseFragment<FragmentEverydayAnimeBinding>(), EventBusSubscriber {
    private lateinit var viewModel: EverydayAnimeViewModel
    private lateinit var adapter: Vp2Adapter
    private var offscreenPageLimit = 1
    private var selectedTabIndex = -1
    private var lastRefreshTime: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(EverydayAnimeViewModel::class.java)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEverydayAnimeBinding =
        FragmentEverydayAnimeBinding.inflate(inflater, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding.run {
            vp2EverydayAnimeFragment.setOffscreenPageLimit(offscreenPageLimit)
            srlEverydayAnimeFragment.setOnRefreshListener { refresh() }

            tlEverydayAnimeFragment.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    selectedTabIndex = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                }

            })
        }

        viewModel.mldHeader.observe(viewLifecycleOwner, {
            mBinding.tvEverydayAnimeFragmentTitle.text = it.title
        })

        viewModel.mldEverydayAnimeList.observe(viewLifecycleOwner, {
            mBinding.srlEverydayAnimeFragment.isRefreshing = false

            if (it != null) {
                viewModel.everydayAnimeList.apply {
                    clear()
                    if (this@EverydayAnimeFragment::adapter.isInitialized)
                        adapter.notifyDataSetChanged()
                    addAll(it)
                    if (this@EverydayAnimeFragment::adapter.isInitialized)
                        adapter.notifyDataSetChanged()
                }
                val selectedTabIndex = this.selectedTabIndex
                activity?.let { it1 ->
                    //先隐藏
                    ObjectAnimator.ofFloat(mBinding.llEverydayAnimeFragment, "alpha", 1f, 0f)
                        .setDuration(270).start()
                    //添加rv
                    if (!this::adapter.isInitialized) {
                        adapter = Vp2Adapter(it1, viewModel.everydayAnimeList)
                        mBinding.vp2EverydayAnimeFragment.setAdapter(adapter)
                    }
                    val tabLayoutMediator = TabLayoutMediator(
                        mBinding.tlEverydayAnimeFragment,
                        mBinding.vp2EverydayAnimeFragment.getViewPager()
                    ) { tab, position ->
                        tab.text = viewModel.tabList[position].title
                    }
                    tabLayoutMediator.attach()

                    val tabCount = adapter.itemCount
                    mBinding.vp2EverydayAnimeFragment.post {
                        if (selectedTabIndex != -1 && selectedTabIndex < tabCount)
                            mBinding.vp2EverydayAnimeFragment.setCurrentItem(
                                selectedTabIndex, false
                            )
                        else if (selectedTabIndex == -1 && viewModel.selectedTabIndex < tabCount
                            && viewModel.selectedTabIndex >= 0
                        ) {
                            mBinding.vp2EverydayAnimeFragment.setCurrentItem(
                                viewModel.selectedTabIndex, false
                            )
                        }
                        //设置完数据后显示，避免闪烁
                        ObjectAnimator.ofFloat(mBinding.llEverydayAnimeFragment, "alpha", 0f, 1f)
                            .setDuration(270).start()
                    }
                }
                hideLoadFailedTip()
            } else {
                viewModel.everydayAnimeList.apply {
                    val count = size
                    clear()
                    if (this@EverydayAnimeFragment::adapter.isInitialized)
                        adapter.notifyItemRangeRemoved(0, count)
                }
                showLoadFailedTip(getString(R.string.load_data_failed_click_to_retry)) {
                    viewModel.getEverydayAnimeData()
                    hideLoadFailedTip()
                }
            }
        })

        mBinding.srlEverydayAnimeFragment.isRefreshing = true
        viewModel.getEverydayAnimeData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onMessageEvent(event: MessageEvent) {
        when (event) {
            is RefreshEvent -> {
                refresh()
            }
        }
    }

    private fun refresh() {
        //避免刷新间隔太短
        if (System.currentTimeMillis() - lastRefreshTime > 500) {
            mBinding.srlEverydayAnimeFragment.isRefreshing = true
            lastRefreshTime = System.currentTimeMillis()
            viewModel.getEverydayAnimeData()
        } else {
            mBinding.srlEverydayAnimeFragment.isRefreshing = false
        }
    }

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutEverydayAnimeFragmentLoadFailed

    class Vp2Adapter(
        private var activity: Activity,
        private var list: List<List<AnimeCoverBean>>,
        private var showRankNumber: BooleanArray = BooleanArray(0)
    ) : SkinRvAdapter() {

        //必须四个参数都不是-1才生效
        var childPadding = Rect(-1, -1, -1, -1)

        override fun getItemViewType(position: Int): Int = ViewHolderTypeInt.GRID_RECYCLER_VIEW_1

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            val viewHolder = super.onCreateViewHolder(parent, viewType)
            //vp2的item必须是MATCH_PARENT的
            val layoutParams = viewHolder.itemView.layoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            viewHolder.itemView.layoutParams = layoutParams
            return viewHolder
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = list[position]
            when (holder) {
                is GridRecyclerView1ViewHolder -> {
                    val rvLayoutParams = holder.rvGridRecyclerView1.layoutParams
                    rvLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    holder.rvGridRecyclerView1.layoutManager = WrapLinearLayoutManager(activity)
                    holder.rvGridRecyclerView1.layoutParams = rvLayoutParams
                    holder.rvGridRecyclerView1.isNestedScrollingEnabled = true
                    val adapter = AnimeShowAdapter.GridRecyclerView1Adapter(activity, item)
                    adapter.padding = childPadding
                    if (showRankNumber.isNotEmpty() && showRankNumber[position])
                        adapter.showRankNumber = true
                    holder.rvGridRecyclerView1.adapter = adapter
                }
            }
        }

        override fun getItemCount(): Int = list.size
    }

    companion object {
        const val TAG = "EverydayAnimeFragment"
    }
}
