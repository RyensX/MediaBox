package com.su.mediabox.view.fragment.page

import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.su.mediabox.Pref
import com.su.mediabox.R
import com.su.mediabox.databinding.PageDownloadBinding
import com.su.mediabox.databinding.PageSearchBinding
import com.su.mediabox.lifecycleCollect
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.util.DataState
import com.su.mediabox.util.logD
import com.su.mediabox.util.logI
import com.su.mediabox.util.showToast
import com.su.mediabox.view.adapter.type.dynamicGrid
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.submitList
import com.su.mediabox.view.fragment.BaseViewBindingFragment
import com.su.mediabox.viewmodel.MediaCombineSearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MediaCombineSearchPageFragment : BaseViewBindingFragment<PageSearchBinding>() {

    private val vm by viewModels<MediaCombineSearchViewModel>()

    override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        PageSearchBinding.inflate(inflater)

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

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

        mBinding.searchView.closeBtnClickListener = View.OnClickListener {
            vm.combineSearch("")
        }

        lifecycleCollect(vm.keywordFlow) {
            mBinding.searchView.apply {
                val key = text.toString()
                if (key != it)
                    text = it
                mBinding.searchHint.isVisible = key.isBlank()
                mBinding.searchList.isVisible = key.isNotBlank()
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

    private var searchMenuJob: Job? = null
    private fun pluginBind2Menu(menu: Menu) {
        searchMenuJob?.cancel()
        menu.clear()
        menu.addSubMenu(getString(R.string.combine_search_ignores)).apply {
            val ignores = Pref.combineSearchIgnorePlugins.value
            logI("聚合搜索忽略", ignores)
            searchMenuJob = lifecycleScope.launch(Dispatchers.Main) {
                vm.pluginSearchComponentsFlow.collect { plugins ->
                    plugins?.forEach {
                        add(it.first.name).apply {
                            titleCondensed = it.first.packageName
                            isCheckable = true
                            isChecked = !ignores.contains("${it.first.packageName}!")
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        pluginBind2Menu(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.subMenu == null) {
            item.isChecked = !item.isChecked
            vm.setPluginEnable(item.titleCondensed.toString(), item.isChecked)
        }
        return super.onOptionsItemSelected(item)
    }

}