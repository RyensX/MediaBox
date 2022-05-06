package com.su.mediabox.view.activity

import android.os.Bundle
import com.su.mediabox.R
import com.su.mediabox.bean.PreviewPluginInfo
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ActivityCustomDataBinding
import com.su.mediabox.net.RetrofitManager
import com.su.mediabox.net.service.PluginService
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.v2.been.SimpleTextData
import com.su.mediabox.util.Util
import com.su.mediabox.viewmodel.PageLoadViewModel
import com.su.mediabox.view.adapter.type.DataViewMapList
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.linear
import com.su.mediabox.view.adapter.type.registerDataViewMap
import com.su.mediabox.view.viewcomponents.PreviewPluginInfoViewHolder
import com.su.mediabox.view.viewcomponents.SimpleTextViewHolder

class PluginRepositoryActivity : PageLoadActivity<ActivityCustomDataBinding>() {

    override val refreshLayout get() = mBinding.customDataSwipe
    override val dataListView get() = mBinding.customDataList

    private val api = RetrofitManager.get().create(PluginService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(mBinding.customDataBar)
        setTitle(R.string.plugin_repo_title)

        PluginManager.pluginLiveData.observe(this) {
            //TODO 这里简单实现为直接再次拉取信息并生成，应该做合并liveData
            if (pageLoadViewModel.loadState.value !is PageLoadViewModel.LoadState.LOADING)
                pageLoadViewModel.reLoadData()
        }
    }

    override fun initList() {
        mBinding.apply {
            customDataList.linear()
                .initTypeList(
                    DataViewMapList()
                        .registerDataViewMap<PreviewPluginInfo, PreviewPluginInfoViewHolder>()
                        .registerDataViewMap<SimpleTextData, SimpleTextViewHolder>()
                ) { }
        }
    }

    override fun getBinding() = ActivityCustomDataBinding.inflate(layoutInflater)

    //TODO 待重新实现进而分离出Activity
    //混合本地插件信息分类为 已安装/可更新/可下载
    override suspend fun load(page: Int): List<Any>? {
        //因类别分析需要，所以不再分页加载插件信息
        if (page > 1)
            return null
        val repoData =
            Util.withoutExceptionGet { api.fetchRepositoryPluginPreviewInfo() } ?: return null
        val localPluginData = PluginManager.pluginLiveData.value ?: return null
        val combineData = mutableListOf<Any>()

        val installed = mutableListOf<Any>()
        val updatable = mutableListOf<Any>()
        val downloadable = mutableListOf<Any>()

        for (pp in repoData) {
            localPluginData.find { it.packageName == pp.packageName }?.also {
                //已安装
                if (//debug的无视版本
                    it.isExternalPlugin
                    || (pp.version == it.version && pp.apiVersion == it.apiVersion)
                ) {
                    pp.state = Const.Plugin.PLUGIN_STATE_OPEN
                    pp.mergeLocalData(it)
                    installed.add(pp)
                }
                //可更新
                else {
                    pp.state = Const.Plugin.PLUGIN_STATE_UPDATABLE
                    updatable.add(pp)
                }
            } ?: run {
                //可下载
                pp.state = Const.Plugin.PLUGIN_STATE_DOWNLOADABLE
                downloadable.add(pp)
            }
        }

        combineData.apply {
            if (installed.isNotEmpty()) {
                add(
                    getPluginCategoryText(
                        getString(
                            R.string.plugin_repo_title_template_installed, installed.size
                        )
                    )
                )
                addAll(installed)
            }
            if (updatable.isNotEmpty()) {
                add(
                    getPluginCategoryText(
                        getString(
                            R.string.plugin_repo_title_template_updatable, updatable.size
                        )
                    )
                )
                addAll(updatable)
            }
            if (downloadable.isNotEmpty()) {
                add(
                    getPluginCategoryText(
                        getString(
                            R.string.plugin_repo_title_template_downloadable, downloadable.size
                        )
                    )
                )
                addAll(downloadable)
            }
        }

        return combineData
    }

    private fun getPluginCategoryText(text: String) = SimpleTextData(text).apply {
        paddingLeft = 14.dp
        paddingTop = 16.dp
    }

}