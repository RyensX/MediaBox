package com.su.mediabox.view.activity

import android.app.ActivityManager
import android.app.ActivityOptions
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.su.mediabox.R
import com.su.mediabox.databinding.ActivityHomeBinding
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.components.IHomePageDataComponent
import com.su.mediabox.util.*
import com.su.mediabox.viewmodel.PageLoadViewModel
import com.su.mediabox.view.adapter.type.typeAdapter

class HomeActivity : PageLoadActivity(), View.OnClickListener {

    private val mBinding by viewBind(ActivityHomeBinding::inflate)

    private val dataComponent by lazyAcquireComponent<IHomePageDataComponent>()

    override val refreshLayout: SmartRefreshLayout
        get() = mBinding.homeDataSwipe
    override val dataListView: RecyclerView
        get() = mBinding.homeDataList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.apply {
            setViewsOnClickListener(
                homeHeaderSearch,
                homeHeaderClassify,
                homeHeaderData
            )
        }
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            when (v) {
                homeHeaderSearch -> goActivity<MediaSearchActivity>(
                    options = ActivityOptions.makeSceneTransitionAnimation(
                        this@HomeActivity,
                        mBinding.homeHeaderSearch,
                        getString(R.string.search_transition_name)
                    ).toBundle()
                )
                homeHeaderClassify -> {
                    v.clickScale(0.8f, 70)
                    goActivity<MediaClassifyActivity>()
                }
                homeHeaderData -> {
                    v.clickScale(0.8f, 70)
                    goActivity<MediaDataActivity>()
                }
            }
        }
    }

    override fun loadSuccess(loadState: PageLoadViewModel.LoadState.SUCCESS) {
        super.loadSuccess(loadState)
        hideError()
    }

    override fun loadFailed(throwable: Throwable?) {
        super.loadFailed(throwable)
        if (mBinding.homeDataList.typeAdapter().currentList.isNullOrEmpty())
            showError()
    }

    private fun showError() {
        mBinding.homeLoadFailedLayout.apply {
            smartInflate()
            visibility = View.VISIBLE
        }
    }

    private fun hideError() {
        mBinding.homeLoadFailedLayout.visibility = View.GONE
    }

    override suspend fun load(page: Int): List<BaseData>? = dataComponent.getData(page)

}