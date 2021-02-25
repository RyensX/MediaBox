package com.skyd.imomoe.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.view.adapter.AnimeShowAdapter
import com.skyd.imomoe.view.adapter.SerializableRecycledViewPool
import com.skyd.imomoe.viewmodel.AnimeShowViewModel
import kotlinx.android.synthetic.main.fragment_anime_show.*
import java.lang.Exception


class AnimeShowFragment : BaseFragment() {
    private var partUrl: String = ""
    private lateinit var viewModel: AnimeShowViewModel
    private lateinit var adapter: AnimeShowAdapter
    private val srlOnRefreshListener =
        SwipeRefreshLayout.OnRefreshListener { viewModel.getAnimeShowData(partUrl) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(AnimeShowViewModel::class.java)
        val arguments = arguments

        try {
            partUrl = arguments?.getString("partUrl") ?: ""
            viewModel.viewPool =
                arguments?.getSerializable("viewPool") as SerializableRecycledViewPool
            viewModel.childViewPool =
                arguments.getSerializable("childViewPool") as SerializableRecycledViewPool
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.showToast(Toast.LENGTH_LONG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_anime_show, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val childViewPool = viewModel.childViewPool
        adapter = if (childViewPool == null) {
            AnimeShowAdapter(this, viewModel.animeShowList)
        } else {
            AnimeShowAdapter(this, viewModel.animeShowList, childViewPool)
        }

        val layoutManager = LinearLayoutManager(activity)
        rv_anime_show_fragment.layoutManager = layoutManager
        rv_anime_show_fragment.setHasFixedSize(true)
        rv_anime_show_fragment.adapter = adapter
        viewModel.viewPool?.let {
            rv_anime_show_fragment.setRecycledViewPool(it)
        }

        srl_anime_show_fragment.setColorSchemeResources(R.color.main_color)
        srl_anime_show_fragment.setOnRefreshListener(srlOnRefreshListener)

        viewModel.mldGetAnimeShowList.observe(viewLifecycleOwner, Observer {
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