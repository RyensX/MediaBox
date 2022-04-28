package com.su.mediabox.v2.view.activity

import android.os.Bundle
import com.su.mediabox.R
import com.su.mediabox.bean.PreviewPluginInfo
import com.su.mediabox.databinding.ActivityCustomDataBinding
import com.su.mediabox.net.RetrofitManager
import com.su.mediabox.net.service.PluginService
import com.su.mediabox.pluginapi.v2.been.SimpleTextData
import com.su.mediabox.util.Util
import com.su.mediabox.view.adapter.type.DataViewMapList
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.linear
import com.su.mediabox.view.adapter.type.registerDataViewMap
import com.su.mediabox.view.viewcomponents.PreviewPluginInfoViewHolder
import com.su.mediabox.view.viewcomponents.SimpleTextViewHolder

//TODO 混合本地插件信息分类为 已安装/可更新/可下载
class PluginRepositoryActivity : PageLoadActivity<ActivityCustomDataBinding>() {

    override val refreshLayout get() = mBinding.customDataSwipe
    override val dataListView get() = mBinding.customDataList

    private val api = RetrofitManager.get().create(PluginService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(mBinding.customDataBar)
        setTitle(R.string.plugin_manager_title)
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

    override suspend fun load(page: Int) =
        Util.withoutExceptionGet { api.fetchRepositoryPluginPreviewInfo(page) }

}