package com.skyd.imomoe.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.FragmentAnimeShowBinding
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.view.adapter.AnimeShowAdapter
import com.skyd.imomoe.view.adapter.SerializableRecycledViewPool
import com.skyd.imomoe.viewmodel.AnimeShowViewModel
import java.lang.Exception


class AnimeShowFragment : BaseFragment<FragmentAnimeShowBinding>() {
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

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAnimeShowBinding = FragmentAnimeShowBinding.inflate(inflater, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val childViewPool = viewModel.childViewPool
        adapter = if (childViewPool == null) {
            AnimeShowAdapter(this, viewModel.animeShowList)
        } else {
            AnimeShowAdapter(this, viewModel.animeShowList, childViewPool)
        }

        mBinding.run {
            rvAnimeShowFragment.layoutManager = LinearLayoutManager(activity)
            rvAnimeShowFragment.setHasFixedSize(true)
            rvAnimeShowFragment.adapter = adapter
            srlAnimeShowFragment.setColorSchemeResources(R.color.main_color)
            srlAnimeShowFragment.setOnRefreshListener(srlOnRefreshListener)
        }

        viewModel.viewPool?.let {
            mBinding.rvAnimeShowFragment.setRecycledViewPool(it)
        }

        viewModel.mldGetAnimeShowList.observe(viewLifecycleOwner, Observer {
            mBinding.srlAnimeShowFragment.isRefreshing = false
            adapter.notifyDataSetChanged()

            if (it) {
                hideLoadFailedTip()
            } else {
                showLoadFailedTip(getString(R.string.load_data_failed_click_to_retry),
                    View.OnClickListener {
                        viewModel.getAnimeShowData(partUrl)
                        hideLoadFailedTip()
                    })
            }
        })

        refresh()
    }

    fun refresh() {
        mBinding.srlAnimeShowFragment.isRefreshing = true
        srlOnRefreshListener.onRefresh()
    }

    override fun getLoadFailedTipView(): ViewStub? = mBinding.layoutAnimeShowFragmentLoadFailed

    companion object {
        const val TAG = "AnimeShowFragment"
    }
}