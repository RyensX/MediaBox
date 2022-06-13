package com.su.mediabox.view.fragment.page

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
import com.su.mediabox.plugin.PluginManager.launchPlugin
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.data.SimpleTextData
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.*
import com.su.mediabox.view.activity.MediaFavoriteActivity
import com.su.mediabox.view.adapter.*
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.dialog.PluginManageBottomSheetDialogFragment
import com.su.mediabox.view.fragment.BaseFragment
import com.su.mediabox.view.viewcomponents.inner.ItemPluginViewHolder
import com.su.mediabox.view.viewcomponents.SimpleTextViewHolder
import com.su.mediabox.view.viewcomponents.inner.MediaMoreViewHolder
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
                        .registerDataViewMap<MediaMoreViewHolder.DataStub, MediaMoreViewHolder>()
                        //TODO 暂时不能直接启动对于插件打开详情页
                        .registerDataViewMap<MediaFavorite, MediaFavoriteActivity.FavoriteViewHolder>(),
                    PluginManageDiff
                ) { rv ->
                    (rv.layoutManager as GridLayoutManager).spanSizeLookup =
                        ExploreSpanLookup(this::getItem)

                    rv.addItemDecoration(DynamicGridItemDecoration(8.dp))

                    vHCreateDSL<ItemPluginManageViewHolder> {
                        //切换分组状态
                        setOnClickListener(binding.pluginManageMedia) { pos ->
                            viewModel.switchGroupState(pos)
                        }
                        //启动插件
                        setOnClickListener(itemView) { pos ->
                            bindingContext.launchPlugin(getData<PluginManageModel>(pos)?.pluginInfo)
                        }
                        //管理插件
                        setOnLongClickListener(itemView) { pos ->
                            bindingTypeAdapter.getData<PluginManageModel>(pos)?.let { pm ->
                                PluginManageBottomSheetDialogFragment.create(pm.pluginInfo.packageName)
                                    .show(requireActivity())
                            }
                            true
                        }
                    }

                    vHCreateDSL<MediaFavoriteActivity.FavoriteViewHolder> {
                        setOnClickListener(itemView) { pos ->
                            //向上查找所属插件的信息
                            bindingTypeAdapter.findTypeData<PluginManageModel>(pos, -1)?.also {
                                //提取目标媒体信息
                                bindingTypeAdapter.getData<MediaFavorite>(pos)?.run {
                                    //静默启动插件
                                    bindingContext.launchPlugin(it.pluginInfo, false)
                                    //路由到目标页面
                                    DetailAction.obtain(mediaUrl).go(bindingContext)
                                }
                            }
                        }
                    }

                    vHCreateDSL<MediaMoreViewHolder> {
                        setOnClickListener(itemView) { pos ->
                            //向上查找所属插件的信息
                            bindingTypeAdapter.findTypeData<PluginManageModel>(pos, -1)?.also {
                                bindingContext.launchPlugin(it.pluginInfo, false)
                                requireActivity().goActivity<MediaFavoriteActivity>()
                            }
                        }
                    }
                }
        }

        lifecycleCollect(viewModel.exploreData) { dataState ->
            when (dataState) {
                DataState.Init -> {
                    logD("插件管理数据", "初始化")
                }
                is DataState.Success -> {
                    logD("插件管理数据", "数据数量=${dataState.data?.data?.size} 引用=${dataState.data?.data?.hashCode()}")
                    mBinding.pluginList.submitList(dataState.data?.data.let { if (it.isNullOrEmpty()) emptyView else it })
                }
                DataState.Loading -> {
                    logD("插件管理数据", "加载中")
                }
                is DataState.Failed -> {
                    dataState.throwable?.message?.showToast()
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
                pluginManageMediaInfo.text = bindingContext.getString(
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