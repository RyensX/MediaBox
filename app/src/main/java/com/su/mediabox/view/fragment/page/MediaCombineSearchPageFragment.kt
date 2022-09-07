package com.su.mediabox.view.fragment.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.su.mediabox.R
import com.su.mediabox.databinding.PageDownloadBinding
import com.su.mediabox.databinding.PageSearchBinding
import com.su.mediabox.lifecycleCollect
import com.su.mediabox.util.DataState
import com.su.mediabox.util.logD
import com.su.mediabox.util.showToast
import com.su.mediabox.view.adapter.type.dynamicGrid
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.submitList
import com.su.mediabox.view.fragment.BaseViewBindingFragment
import com.su.mediabox.viewmodel.MediaCombineSearchViewModel

class MediaCombineSearchPageFragment : BaseViewBindingFragment<PageSearchBinding>() {

    private val vm by viewModels<MediaCombineSearchViewModel>()

    override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        PageSearchBinding.inflate(inflater)

    override fun pagerInit() {

        mBinding.searchList.dynamicGrid().initTypeList { }

        mBinding.searchView.apply {
            setEnterListener {
                vm.combineSearch(it.text.toString())
            }
        }

        mBinding.searchRefresh.apply {
            setOnRefreshListener {
                vm.combineSearch(isReLoad = true)
                it.finishRefresh()
            }
            setOnLoadMoreListener {
                vm.combineSearch()
                it.finishLoadMore()
            }
        }

        lifecycleCollect(vm.keywordFlow) {
            mBinding.searchView.apply {
                if (text.toString() != it)
                    text = it
            }
        }

        lifecycleCollect(vm.searchDataList) { ds ->
            mBinding.searchView.isEdit = true
            mBinding.searchRefresh.apply {
                val e = ds is DataState.Success
                setEnableLoadMore(e)
                setEnableRefresh(e)
            }

            when (ds) {
                is DataState.Failed -> {

                }
                DataState.Init -> {

                }
                DataState.Loading -> {
                    mBinding.searchView.isEdit = false
                }
                is DataState.Success -> {
                    //TODO 还需要排查为什么DS会重复两次分发
                    if (ds.data?.data?.isEmpty() == true)
                        getString(R.string.no_result_info).showToast()
                    else if (ds.data?.lastLoad == 0)
                        getString(R.string.no_more_info).showToast()
                    ds.data?.data?.let {
                        logD("聚合搜索", "展示数据size=${it.size} 上次加载:${ds.data?.lastLoad}")
                        mBinding.searchList.submitList(it)
                    }
                }
            }
        }
    }

}