package com.su.mediabox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.model.PluginManageModel
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.util.*
import com.su.mediabox.util.DataState.Success.Companion.destroySuccessIns
import com.su.mediabox.util.DataState.Success.Companion.successIns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {

    private var currentPluginManageJob: Job? = null

    /**
     * 数据项可为[PluginManageModel]和[MediaFavorite]（展开了分组）
     */
    private val _exploreData: MutableStateFlow<DataState<MutableDynamicReferenceListData<Any>>> =
        MutableStateFlow(DataState.Init)

    val exploreData: StateFlow<DataState<DynamicReferenceListData<Any>>> =
        _exploreData

    init {
        //观测插件信息并绑定插件收藏数据库
        viewModelScope.launch {
            PluginManager.pluginFlow.collect { plugins ->
                _exploreData.value = DataState.Loading
                plugins.map { plugin ->
                    logD("插件管理数据", "生成数据:${plugin.id}")
                    //绑定信息
                    plugin.getAppDataBase().favoriteDao().getFavoriteListFlow().map {
                        PluginManageModel(plugin, it)
                    }
                }.also {
                    collectFlowManageData(it)
                }
            }
        }
    }

    //观测各插件对应的收藏数据库
    private fun collectFlowManageData(flowManageData: List<Flow<PluginManageModel>>) {
        currentPluginManageJob?.cancel()
        currentPluginManageJob = viewModelScope.launch(Dispatchers.Default) {
            //合并每个插件对应数据库Flow
            combine(*flowManageData.toTypedArray()) { arrayOfPluginManageModels ->
                arrayOfPluginManageModels
                    //    .map { pluginManageModel ->
                    //    pluginManageModel.apply {
                    //        //记忆折叠状态
                    //        val dataState = _exploreData.value
                    //        if (dataState is DataState.Success)
                    //            dataState.data().data.find {
                    //                (it as PluginManageModel).pluginInfo.id == pluginInfo.id
                    //            }?.also {
                    //                isExpand = (it as PluginManageModel).isExpand
                    //            }
                    //    }
                    //}
                    .toList()
            }
                .map { models ->
                    //按照最后查看日期倒序
                    models.sortedByDescending { it.childData?.firstOrNull()?.lastViewTime ?: -1 }
                }
                .catch {
                    _exploreData.value = DataState.Failed(it)
                }
                .flowOn(Dispatchers.Default)
                .collect {
                    //TODO 根据折叠状态进行增删子数据
                    //TODO 根据最新查看日期排序
                    _exploreData.value =
                        successIns<MutableDynamicReferenceListData<Any>>().apply {
                            data().putData(it)
                        }
                }
        }
    }

    /**
     * 切换分组折叠状态
     */
    //TODO 对分组进行折叠展开可能还是需要抽出为通用工具比较好，可能需要重写。但这里为了兼顾异步更新先这样实现
    fun switchGroupState(pos: Int) {
        val dataState = _exploreData.value
        //StateFlow有distinctUntilChanged机制
        _exploreData.value = DataState.Loading
        viewModelScope.launch(Dispatchers.Default) {
            when (dataState) {
                is DataState.Success -> {
                    dataState.data?.data?.getOrNull(pos)?.also {
                        if ((it as PluginManageModel).isExpand) {
                            dataState.data?.removeData(pos + 1, it.childData?.size ?: 0)
                        } else {
                            it.childData?.apply {
                                dataState.data?.appendData(this, pos + 1)
                            }
                        }
                        //因为是同一引用所以必须替换一个新值保证视图更新
                        dataState.data?.replaceData(pos,
                            PluginManageModel(it.pluginInfo, it.childData).apply {
                                isExpand = !it.isExpand
                            })
                    }
                }
                else -> Unit
            }
            _exploreData.value = dataState
        }
    }

    override fun onCleared() {
        destroySuccessIns()
        super.onCleared()
    }
}