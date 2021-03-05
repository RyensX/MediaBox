package com.skyd.imomoe.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.view.activity.*
import com.skyd.imomoe.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: VpAdapter
    private var offscreenPageLimit = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        adapter = VpAdapter(this)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vp2_home_fragment.setOffscreenPageLimit(offscreenPageLimit)
        vp2_home_fragment.setAdapter(adapter)
        val tabLayoutMediator = TabLayoutMediator(
            tl_home_fragment, vp2_home_fragment.getViewPager()
        ) { tab, position ->
            if (position < viewModel.allTabList.size)
                tab.text = viewModel.allTabList[position].title
        }
        tabLayoutMediator.attach()

        iv_home_fragment_rank.setOnClickListener {
            activity?.let {
                it.startActivity(Intent(it, RankActivity::class.java))
                it.overridePendingTransition(
                    R.anim.anl_push_left_in,
                    R.anim.anl_stay
                )
            }
        }

        iv_home_fragment_classify.setOnClickListener {
            startActivity(Intent(activity, ClassifyActivity::class.java))
        }

        tv_home_fragment_header_search.setOnClickListener {
            activity?.let {
                it.startActivity(Intent(it, SearchActivity::class.java))
                it.overridePendingTransition(
                    R.anim.anl_push_top_in,
                    R.anim.anl_stay
                )
            }
        }

        iv_home_fragment_anime_download.setOnClickListener {
            XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE).request(
                object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        startActivity(Intent(activity, AnimeDownloadActivity::class.java))
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        super.onDenied(permissions, never)
                        "无存储权限，无法播放本地缓存视频".showToast(Toast.LENGTH_LONG)
                    }
                }
            )
        }

        iv_home_fragment_setting.setOnClickListener {
            startActivity(Intent(activity, SettingActivity::class.java))
        }

        tl_home_fragment.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                //当选项卡变成未选中状态时调用
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                adapter.refresh(tab.position)
            }
        })

        viewModel.mldGetAllTabList.observe(viewLifecycleOwner, Observer {
            if (viewModel.allTabList.size == 0) getString(R.string.get_home_tab_data_failed).showToast(
                Toast.LENGTH_LONG
            )
            adapter.clearAllFragment()
            for (i in it.indices) {
                val fragment = AnimeShowFragment()
                val bundle = Bundle()
                bundle.putString("partUrl", it[i].actionUrl)
                bundle.putSerializable("viewPool", viewModel.viewPool)
                bundle.putSerializable("childViewPool", viewModel.childViewPool)
                fragment.arguments = bundle
                adapter.addFragment(fragment)
            }
            adapter.notifyDataSetChanged()
        })

        viewModel.getAllTabData()
    }

    inner class VpAdapter :
        FragmentStateAdapter {

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