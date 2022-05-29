package com.su.mediabox.view.fragment.page

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.su.mediabox.R
import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.databinding.ItemPluginManageBinding
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.databinding.PageExploreBinding
import com.su.mediabox.lifecycleCollect
import com.su.mediabox.model.PluginManageModel
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.plugin.PluginManager.launchPlugin
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.data.SimpleTextData
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.*
import com.su.mediabox.view.activity.MediaFavoriteActivity
import com.su.mediabox.view.adapter.*
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.fragment.BaseFragment
import com.su.mediabox.view.viewcomponents.ItemPluginViewHolder
import com.su.mediabox.view.viewcomponents.SimpleTextViewHolder
import com.su.mediabox.viewmodel.ExploreViewModel

//TODO 要重新设计为插件管理合并数据显示
class ExplorePageFragment : BaseFragment<PageExploreBinding>() {

    private val viewModel by activityViewModels<ExploreViewModel>()

    private val emptyView by unsafeLazy {
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
                .grid(ExploreSpanLookup.SPAN_COUNT)
                .initTypeList(
                    DataViewMapList()
                        .registerDataViewMap<PluginInfo, ItemPluginViewHolder>()
                        .registerDataViewMap<SimpleTextData, SimpleTextViewHolder>()
                        .registerDataViewMap<PluginManageModel, ItemPluginManageViewHolder>()
                        //TODO 暂时不能直接启动对于插件打开详情页
                        .registerDataViewMap<MediaFavorite, MediaFavoriteActivity.FavoriteViewHolder>(),
                    PluginManageDiff
                ) {
                    (it.layoutManager as GridLayoutManager).spanSizeLookup =
                        ExploreSpanLookup(this::getItem)

                    it.addItemDecoration(DynamicGridItemDecoration(8.dp))

                    vHCreateDSL<ItemPluginManageViewHolder> {
                        //切换分组状态
                        setOnClickListener(binding.pluginManageMedia) { pos ->
                            viewModel.switchGroupState(pos)
                        }
                        //启动插件
                        setOnClickListener(itemView) { pos ->
                            bindingContext.launchPlugin(getData<PluginManageModel>(pos)?.pluginInfo)
                        }
                    }
                }
        }

        lifecycleCollect(viewModel.exploreData) {
            when (it) {
                DataState.Init -> {
                    logD("插件管理数据", "初始化")
                }
                is DataState.Success -> {
                    logD("插件管理数据", "数据数量=${it.data?.data?.size} 引用=${it.data?.data?.hashCode()}")
                    mBinding.pluginList.submitList(it.data?.data ?: emptyView)
                }
                DataState.Loading -> {
                    logD("插件管理数据", "加载中")
                }
                is DataState.Failed -> {
                    it.throwable?.message?.showToast()
                }
            }
        }
    }

    class ItemPluginManageViewHolder private constructor(val binding: ItemPluginManageBinding) :
        TypeViewHolder<PluginManageModel>(binding.root) {

        constructor(parent: ViewGroup) : this(
            ItemPluginManageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) {
            binding.pluginManageMedia.setOnClickListener {
            }
        }

        override fun onBind(data: PluginManageModel) {
            binding.apply {
                pluginManageIcon.setImageDrawable(data.pluginInfo.icon)
                pluginManageName.text = data.pluginInfo.name
                pluginManageMediaCount.text = bindingContext.getString(
                    R.string.plugin_manage_media_count_format,
                    data.childData?.size ?: 0
                )
                pluginManageMedia.apply {
                    val r = if (!data.isExpand) 90F else 0F
                    rotation = r
                    animate().rotationBy(if (data.isExpand) 90F else -90F)
                }
            }
        }
    }

}