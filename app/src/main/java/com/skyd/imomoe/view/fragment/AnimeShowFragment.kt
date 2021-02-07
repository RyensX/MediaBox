package com.skyd.imomoe.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.skyd.imomoe.R
import com.skyd.imomoe.view.adapter.AnimeShowAdapter
import com.skyd.imomoe.viewmodel.AnimeShowViewModel
import kotlinx.android.synthetic.main.fragment_anime_show.*


class AnimeShowFragment : BaseFragment {
    private var partUrl: String = ""
    private lateinit var viewModel: AnimeShowViewModel
    private lateinit var adapter: AnimeShowAdapter
    private val srlOnRefreshListener =
        SwipeRefreshLayout.OnRefreshListener { viewModel.getAnimeShowData(partUrl) }

    constructor(partUrl: String) {
        this.partUrl = partUrl
    }

    constructor() : super()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_anime_show, container, false)
        viewModel = ViewModelProvider(this).get(AnimeShowViewModel::class.java)
        adapter = AnimeShowAdapter(this, viewModel.animeShowList)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = LinearLayoutManager(activity)
        rv_anime_show_fragment.layoutManager = layoutManager
        rv_anime_show_fragment.setHasFixedSize(true)
        rv_anime_show_fragment.adapter = adapter

        srl_anime_show_fragment.setColorSchemeResources(R.color.main_color)
        srl_anime_show_fragment.setOnRefreshListener(srlOnRefreshListener)

        viewModel.mldGetAnimeShowList.observe(viewLifecycleOwner, {
            srl_anime_show_fragment.isRefreshing = false
            if (it) {
                adapter.notifyDataSetChanged()
            }
        })

        refresh()
    }

    fun refresh() {
        srl_anime_show_fragment.isRefreshing = true
        srlOnRefreshListener.onRefresh()
    }

    companion object {
        const val TAG = "AnimeShowFragment"
    }
}