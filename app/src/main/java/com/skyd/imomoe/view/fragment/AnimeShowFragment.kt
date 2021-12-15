package com.skyd.imomoe.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.ResponseDataType
import com.skyd.imomoe.databinding.FragmentAnimeShowBinding
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.smartNotifyDataSetChanged
import com.skyd.imomoe.view.adapter.AnimeShowAdapter
import com.skyd.imomoe.view.adapter.SerializableRecycledViewPool
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize
import com.skyd.imomoe.viewmodel.AnimeShowViewModel


class AnimeShowFragment : BaseFragment<FragmentAnimeShowBinding>() {
    private var partUrl: String = ""
    private lateinit var viewModel: AnimeShowViewModel
    private lateinit var adapter: AnimeShowAdapter

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

    override fun onResume() {
        super.onResume()
        if (isFirstLoadData) {
            initData()
            isFirstLoadData = false
        }
    }

    private fun initData() {
        val childViewPool = viewModel.childViewPool
        adapter = if (childViewPool == null) {
            AnimeShowAdapter(this, viewModel.animeShowList)
        } else {
            AnimeShowAdapter(this, viewModel.animeShowList, childViewPool)
        }

        mBinding.run {
            rvAnimeShowFragment.layoutManager = GridLayoutManager(activity, 4)
                .apply {
                    spanSizeLookup = AnimeShowSpanSize(adapter)
                }
            rvAnimeShowFragment.addItemDecoration(AnimeShowItemDecoration())
            rvAnimeShowFragment.setHasFixedSize(true)
            rvAnimeShowFragment.adapter = adapter
            srlAnimeShowFragment.setOnRefreshListener {
                viewModel.getAnimeShowData(partUrl)
            }
            srlAnimeShowFragment.setOnLoadMoreListener {
                viewModel.pageNumberBean?.let {
                    viewModel.getAnimeShowData(it.actionUrl, isRefresh = false)
                    return@setOnLoadMoreListener
                }
                mBinding.srlAnimeShowFragment.finishLoadMore()
                getString(R.string.no_more_info).showToast()
            }
        }


        viewModel.viewPool?.let {
            mBinding.rvAnimeShowFragment.setRecycledViewPool(it)
        }

        viewModel.mldGetAnimeShowList.observe(viewLifecycleOwner, {
            mBinding.srlAnimeShowFragment.closeHeaderOrFooter()
            adapter.smartNotifyDataSetChanged(it.first, it.second, viewModel.animeShowList)
            when (it.first) {
                ResponseDataType.REFRESH, ResponseDataType.LOAD_MORE -> hideLoadFailedTip()
                ResponseDataType.FAILED -> {
                    showLoadFailedTip(getString(R.string.load_data_failed_click_to_retry)) {
                        viewModel.getAnimeShowData(partUrl)
                        hideLoadFailedTip()
                    }
                }
            }
        })
        refresh()
    }

    fun refresh(): Boolean {
        return mBinding.srlAnimeShowFragment.autoRefresh()
    }

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutAnimeShowFragmentLoadFailed

    companion object {
        const val TAG = "AnimeShowFragment"
    }
}