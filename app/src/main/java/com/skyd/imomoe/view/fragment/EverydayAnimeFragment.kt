package com.skyd.imomoe.view.fragment

import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.skyd.imomoe.util.ViewHolderUtil.Companion.GRID_RECYCLER_VIEW_1
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getViewHolder
import com.skyd.imomoe.view.adapter.AnimeShowAdapter
import com.skyd.imomoe.viewmodel.EverydayAnimeViewModel
import kotlinx.android.synthetic.main.fragment_everyday_anime.*


class EverydayAnimeFragment : BaseFragment() {
    private lateinit var viewModel: EverydayAnimeViewModel
    private lateinit var adapter: Vp2Adapter
    private var offscreenPageLimit = 1
    private var selectedTabIndex = -1
    private var lastRefreshTime: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_everyday_anime, container, false)
        viewModel = ViewModelProvider(this).get(EverydayAnimeViewModel::class.java)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vp2_everyday_anime_fragment.setOffscreenPageLimit(offscreenPageLimit)
        srl_everyday_anime_fragment.setColorSchemeResources(R.color.main_color)
        srl_everyday_anime_fragment.setOnRefreshListener {
            //避免刷新间隔太短
            if (System.currentTimeMillis() - lastRefreshTime > 500) {
                lastRefreshTime = System.currentTimeMillis()
                viewModel.getEverydayAnimeData()
            } else {
                srl_everyday_anime_fragment.isRefreshing = false
            }
        }

        tl_everyday_anime_fragment.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedTabIndex = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }

        })

        viewModel.mldHeader.observe(viewLifecycleOwner, Observer {
            tv_everyday_anime_fragment_title.text = it.title
        })

        viewModel.mldEverydayAnimeList.observe(viewLifecycleOwner, Observer {
            val selectedTabIndex = this.selectedTabIndex
            srl_everyday_anime_fragment.isRefreshing = false

            activity?.let { it1 ->
                //先隐藏
                ObjectAnimator.ofFloat(ll_everyday_anime_fragment, "alpha", 1f, 0f)
                    .setDuration(270).start()
                //添加rv
                adapter = Vp2Adapter(it1, it)
                vp2_everyday_anime_fragment.setAdapter(adapter)

                val tabLayoutMediator = TabLayoutMediator(
                    tl_everyday_anime_fragment, vp2_everyday_anime_fragment.getViewPager()
                ) { tab, position ->
                    tab.text = viewModel.tabList[position].title
                }
                tabLayoutMediator.attach()

                if (selectedTabIndex < tl_everyday_anime_fragment.tabCount)
                    vp2_everyday_anime_fragment.setCurrentItem(selectedTabIndex, false)

                //设置完数据后显示，避免闪烁
                ObjectAnimator.ofFloat(ll_everyday_anime_fragment, "alpha", 0f, 1f)
                    .setDuration(270).start()
            }
        })

        srl_everyday_anime_fragment.isRefreshing = true
        viewModel.getEverydayAnimeData()
    }

    class Vp2Adapter//默认初始化全为false
        (
        private var activity: Activity,
        private var list: List<List<AnimeCoverBean>>,
        private var showRankNumber: BooleanArray = BooleanArray(0)
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        //必须四个参数都不是-1才生效
        var childPadding = Rect(-1, -1, -1, -1)

        override fun getItemViewType(position: Int): Int = GRID_RECYCLER_VIEW_1

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            val viewHolder = getViewHolder(parent, viewType)
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
                    val layoutManager = LinearLayoutManager(activity)
                    val rvLayoutParams = holder.rvGridRecyclerView1.layoutParams
                    rvLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    holder.rvGridRecyclerView1.layoutParams = rvLayoutParams
                    holder.rvGridRecyclerView1.setHasFixedSize(true)
                    holder.rvGridRecyclerView1.layoutManager = layoutManager
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