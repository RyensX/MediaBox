package com.su.mediabox.v2.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.su.mediabox.R
import com.su.mediabox.util.showToast
import com.su.mediabox.v2.viewmodel.PageLoadViewModel
import com.su.mediabox.view.activity.BasePluginActivity
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.linear
import com.su.mediabox.view.adapter.type.typeAdapter

abstract class PageLoadActivity<VB : ViewBinding> : BasePluginActivity<VB>(),
    PageLoadViewModel.LoadData {

    protected val pageLoadViewModel by viewModels<PageLoadViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataListView.linear().initTypeList { }

        pageLoadViewModel.loadDataFun = this

        pageLoadViewModel.loadState.observe(this) {
            when (it) {
                is PageLoadViewModel.LoadState.FAILED -> loadFailed(it.throwable)
                is PageLoadViewModel.LoadState.SUCCESS -> loadSuccess(it)
                is PageLoadViewModel.LoadState.LOADING -> loading()
            }
        }

        refreshLayout.apply {
            //刷新
            setOnRefreshListener {
                pageLoadViewModel.reLoadData()
            }
            //载入更多
            setOnLoadMoreListener {
                pageLoadViewModel.loadData()
            }
        }

        pageLoadViewModel.reLoadData()
    }

    @CallSuper
    open fun loadSuccess(loadState: PageLoadViewModel.LoadState.SUCCESS) {
        dataListView.apply {
            typeAdapter()
                .submitList(loadState.data) {
                    if (loadState.isLoadEmptyData) {
                        getString(R.string.no_more_info).showToast()
                    }
                    postDelayed({
                        refreshLayout.closeHeaderOrFooter()
                    }, 100)
                }
        }
    }

    open fun loadFailed(throwable: Throwable?) {
        refreshLayout.closeHeaderOrFooter()
        throwable?.apply {
            printStackTrace()
            message?.showToast(Toast.LENGTH_LONG)
        } ?: "请求错误".showToast()
    }

    open fun loading() {
        refreshLayout.autoRefresh()
    }

    abstract val refreshLayout: SmartRefreshLayout
    abstract val dataListView: RecyclerView
}