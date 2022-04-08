package com.su.mediabox.v2.view.activity

import android.app.ActivityManager
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.su.mediabox.databinding.ActivityHomeBinding
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.plugin.PluginManager.getPluginInfo
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.pluginapi.v2.components.IHomeDataComponent
import com.su.mediabox.util.clickScale
import com.su.mediabox.util.goActivity
import com.su.mediabox.util.setViewsOnClickListener
import com.su.mediabox.util.smartInflate
import com.su.mediabox.v2.viewmodel.PageLoadViewModel
import com.su.mediabox.view.activity.AnimeDownloadActivity
import com.su.mediabox.view.adapter.type.typeAdapter

class HomeActivity : PageLoadActivity<ActivityHomeBinding>(), View.OnClickListener {

    private val dataComponent by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(
            IHomeDataComponent::class.java
        )
    }

    override val refreshLayout: SmartRefreshLayout
        get() = mBinding.homeDataSwipe
    override val dataListView: RecyclerView
        get() = mBinding.homeDataList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getPluginInfo().also {
            val description = ActivityManager.TaskDescription(it.name, it.icon.toBitmap())
            setTaskDescription(description)
        }

        mBinding.apply {
            setViewsOnClickListener(
                homeHeaderSearch,
                homeHeaderClassify,
                homeHeaderDownload,
                homeHeaderFavorite
            )
        }
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            when (v) {
                homeHeaderSearch -> goActivity<VideoSearchActivity>()
                homeHeaderClassify -> {
                    v.clickScale(0.8f, 70)
                    goActivity<MediaClassifyActivity>()
                }
                homeHeaderDownload -> {
                    v.clickScale(0.8f, 70)
                    goActivity<AnimeDownloadActivity>()
                }
                homeHeaderFavorite -> {
                    v.clickScale(0.8f, 70)
                    goActivity<VideoFavoriteActivity>()
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

    override fun getBinding() = ActivityHomeBinding.inflate(layoutInflater)

}