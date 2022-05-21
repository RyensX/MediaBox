package com.su.mediabox.view.fragment.page

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.R
import com.su.mediabox.bean.PluginInfo
import com.su.mediabox.databinding.PageExploreBinding
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.data.SimpleTextData
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.getFirstItemDecorationBy
import com.su.mediabox.util.update.AppUpdateHelper
import com.su.mediabox.util.update.AppUpdateStatus
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.fragment.BaseFragment
import com.su.mediabox.view.viewcomponents.ItemPluginViewHolder
import com.su.mediabox.view.viewcomponents.SimpleTextViewHolder

//TODO 要重新设计为插件管理合并数据显示
class ExplorePageFragment : BaseFragment<PageExploreBinding>() {

    private val emptyView by lazy(LazyThreadSafetyMode.NONE) {
        listOf(SimpleTextData(requireContext().getString(R.string.plugin_list_empty)).apply {
            val padding = 8.dp
            paddingLeft = padding
            paddingTop = padding
            paddingRight = padding
            paddingBottom = padding
            spanSize = Constant.DEFAULT_SPAN_COUNT
        })
    }

    override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        PageExploreBinding.inflate(inflater)

    override fun pagerInit() {
        mBinding.apply {
            pluginList
                .initTypeList(
                    DataViewMapList()
                        .registerDataViewMap<PluginInfo, ItemPluginViewHolder>()
                        .registerDataViewMap<SimpleTextData, SimpleTextViewHolder>()
                ) {}
        }

        PluginManager.pluginLiveData.observe(this) {
            mBinding.pluginList.apply {
                val dy = getFirstItemDecorationBy<DynamicGridItemDecoration>()
                if (it.isEmpty()) {
                    if (dy == null || layoutManager == null)
                        dynamicGrid()
                    submitList(emptyView)
                } else {
                    if (dy != null)
                        removeItemDecoration(dy)
                    if (layoutManager == null)
                        grid(4)
                    submitList(it)
                }
            }
        }

        //自动刷新
        listenInstallBroadcasts()
    }

    private fun listenInstallBroadcasts() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        context?.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                PluginManager.scanPlugin()
            }
        }, intentFilter)
    }

}