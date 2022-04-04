package com.su.mediabox.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.su.mediabox.plugin.PluginManager.process
import com.su.mediabox.R
import com.su.mediabox.databinding.FragmentHomeBinding
import com.su.mediabox.util.showToast
import com.su.mediabox.util.eventbus.EventBusSubscriber
import com.su.mediabox.util.eventbus.MessageEvent
import com.su.mediabox.util.eventbus.RefreshEvent
import com.su.mediabox.util.eventbus.SelectHomeTabEvent
import com.su.mediabox.util.requestManageExternalStorage
import com.su.mediabox.view.activity.*
import com.su.mediabox.viewmodel.HomeViewModel
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.util.clickScale
import com.su.mediabox.util.setViewsOnClickListener
import com.su.mediabox.v2.view.activity.MediaClassifyActivity
import com.su.mediabox.v2.view.activity.VideoFavoriteActivity
import com.su.mediabox.view.listener.dsl.addOnTabSelectedListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeFragment : BaseFragment<FragmentHomeBinding>(), View.OnClickListener, EventBusSubscriber {
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: VpAdapter
    private var currentTab = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        adapter = VpAdapter(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 清除缓存，以免换肤后颜色错误
        viewModel.childViewPool.clear()
        viewModel.viewPool.clear()

        mBinding.run {
            vp2HomeFragment.setAdapter(adapter)
            val tabLayoutMediator = TabLayoutMediator(
                tlHomeFragment, vp2HomeFragment.getViewPager()
            ) { tab, position ->
                if (position < viewModel.allTabList.size)
                    tab.text = viewModel.allTabList[position].title
            }
            tabLayoutMediator.attach()

            //菜单
            setViewsOnClickListener(
                ivHomeFragmentRank,
                ivHomeFragmentClassify,
                tvHomeFragmentHeaderSearch,
                ivHomeFragmentAnimeDownload,
                ivHomeFragmentFavorite
            )

            tlHomeFragment.addOnTabSelectedListener {
                onTabSelected { currentTab = it!!.position }
                onTabReselected { adapter.refresh(currentTab) }
            }
        }

        viewModel.mldGetAllTabList.observe(viewLifecycleOwner) {
            adapter.clearAllFragment()
            if (!it) {
                showLoadFailedTip(getString(R.string.load_data_failed_click_to_retry)) {
                    viewModel.getAllTabData()
                    hideLoadFailedTip()
                }
                getString(R.string.get_home_tab_data_failed).showToast(Toast.LENGTH_LONG)
            } else {
                hideLoadFailedTip()
                viewModel.allTabList.size.let { size ->
                    if (size > 0) mBinding.vp2HomeFragment.setOffscreenPageLimit(size)
                }
                for (i in viewModel.allTabList.indices) {
                    val fragment = AnimeShowFragment()
                    val bundle = Bundle()
                    bundle.putString("partUrl", viewModel.allTabList[i].actionUrl)
                    bundle.putSerializable("viewPool", viewModel.viewPool)
                    bundle.putSerializable("childViewPool", viewModel.childViewPool)
                    fragment.arguments = bundle
                    adapter.addFragment(fragment)
                }
            }
            adapter.notifyDataSetChanged()
        }

        viewModel.getAllTabData()
    }

    // priority = 1比MainActivity的高，以便在找不到相应子页面时拦截SelectHomeTabEvent
    // 使得不会切换到MainActivity页面
    @SuppressLint("StringFormatInvalid")
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    override fun onMessageEvent(event: MessageEvent) {
        when (event) {
            is RefreshEvent -> {
                // 如果获取首页信息成功了，则刷新每个tab内容，否则重新获取主页信息
                if (viewModel.mldGetAllTabList.value == true)
                    adapter.refresh(currentTab)
                else viewModel.getAllTabData()
            }
            is SelectHomeTabEvent -> {
                var tabPosition = -1
                viewModel.allTabList.forEachIndexed { index, tabBean ->
                    if (tabBean.actionUrl == event.actionUrl) {
                        tabPosition = index
                        return@forEachIndexed
                    }
                }
                if (tabPosition >= 0 && tabPosition < mBinding.tlHomeFragment.tabCount)
                    mBinding.vp2HomeFragment.getViewPager()
                        .apply { post { currentItem = tabPosition } }
                else {
                    EventBus.getDefault().cancelEventDelivery(event)
                    getString(R.string.unknown_route, event.actionUrl).showToast()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            when (v) {
                ivHomeFragmentRank -> activity?.let { it1 ->
                    it1.startActivity(Intent(it1, RankActivity::class.java))
                    it1.overridePendingTransition(R.anim.anl_push_left_in, R.anim.anl_stay)
                }
                ivHomeFragmentClassify -> startActivity(
                    Intent(activity, MediaClassifyActivity::class.java)
                )
                tvHomeFragmentHeaderSearch -> activity?.let {
                    process(Constant.ActionUrl.ANIME_SEARCH)
                    it.overridePendingTransition(R.anim.anl_push_top_in, R.anim.anl_stay)
                    return
                }
                ivHomeFragmentAnimeDownload -> requestManageExternalStorage {
                    onGranted {
                        startActivity(Intent(activity, AnimeDownloadActivity::class.java))
                    }
                    onDenied { "无存储权限，无法播放本地缓存视频".showToast(Toast.LENGTH_LONG) }
                }
                ivHomeFragmentFavorite -> startActivity(
                    Intent(
                        activity,
                        VideoFavoriteActivity::class.java
                    )
                )
            }
            v?.clickScale(0.8f, 70)
        }
    }

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutHomeFragmentLoadFailed

    class VpAdapter : FragmentStateAdapter {

        constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity)

        constructor(fragment: Fragment) : super(fragment)

        private val fragments = mutableListOf<AnimeShowFragment>()

        fun clearAllFragment() {
            fragments.clear()
        }

        fun addFragment(fragment: AnimeShowFragment) {
            fragments.add(fragment)
        }

        fun refresh(position: Int) {
            fragments[position].refresh()
        }

        override fun getItemCount() = fragments.size

        override fun createFragment(position: Int) = fragments[position]
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}
