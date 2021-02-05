package com.skyd.imomoe.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.skyd.imomoe.R
import com.skyd.imomoe.view.activity.SearchActivity
import com.skyd.imomoe.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: VpAdapter
    private var offscreenPageLimit = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vp2_home_fragment.offscreenPageLimit = offscreenPageLimit

        tv_home_fragment_header_search.setOnClickListener {
            activity?.let {
                it.startActivity(Intent(it, SearchActivity::class.java))
                it.overridePendingTransition(
                    R.anim.anl_push_top_in,
                    R.anim.anl_stay
                )
            }
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

        viewModel.mldGetLTabList.observe(viewLifecycleOwner, {
            adapter = VpAdapter(activity!!)
            vp2_home_fragment.adapter = adapter

            val tabLayoutMediator = TabLayoutMediator(
                tl_home_fragment, vp2_home_fragment
            ) { tab, position ->

            }
            tabLayoutMediator.attach()
            for (i in it.indices) {
                tl_home_fragment.addTab(tl_home_fragment.newTab().setText(it[i].title))

                adapter.addFragment(AnimeShowFragment(it[i].actionUrl))
            }
            viewModel.getRTabData()
        })

        viewModel.mldGetRTabList.observe(viewLifecycleOwner, {
            for (i in it.indices) {
                tl_home_fragment.addTab(tl_home_fragment.newTab().setText(it[i].title))

                adapter.addFragment(AnimeShowFragment(it[i].actionUrl))
            }
        })

        viewModel.getLTabData()
    }

    inner class VpAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        private val fragments = mutableListOf<AnimeShowFragment>()

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