package com.su.mediabox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.config.Const.ViewComponent.DEFAULT_PAGE
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.plugin.PluginManager.acquireComponent
import com.su.mediabox.pluginapi.components.IMediaSearchPageDataComponent
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.util.*
import com.su.mediabox.util.DataState.Success.Companion.destroySuccessIns
import com.su.mediabox.util.DataState.Success.Companion.successIns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MediaCombineSearchViewModel : ViewModel() {

    //惰性自动响应的插件媒体搜索组件流
    private val pluginSearchComponentsFlow =
        PluginManager.pluginFlow.map { pluginList ->
            logI("搜索插件信息", "数量:${pluginList.size}")
            mutableListOf<Pair<PluginInfo, IMediaSearchPageDataComponent>>().apply {
                pluginList.forEach { plugin ->
                    logI("添加搜索插件", plugin.id)
                    Util.withoutExceptionGet(showErrMsg = false) {
                        plugin.acquireComponent(
                            IMediaSearchPageDataComponent::class.java
                        )
                    }
                        ?.let { add(Pair(plugin, it)) }
                }
            }.apply {
                logI("测试", "包含搜索插件数:${size}")
            }
        }
            .flowOn(Dispatchers.IO)
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _keywordFlow = MutableStateFlow("")
    val keywordFlow: StateFlow<String> = _keywordFlow

    var page = DEFAULT_PAGE
        private set

    //搜索数据
    private val _searchDataList =
        MutableStateFlow<DataState<MutableDynamicReferenceListData<BaseData>>>(DataState.Init)
    val searchDataList: StateFlow<DataState<DynamicReferenceListData<BaseData>>> =
        _searchDataList

    @OptIn(FlowPreview::class)
    fun combineSearch(keyword: String = keywordFlow.value, isReLoad: Boolean = false) {
        if (keyword.isBlank())
            return
        viewModelScope.launch(Dispatchers.IO) {

            val isSameKeyword = _keywordFlow.value == keyword

            if (!isSameKeyword || isReLoad) {
                page = DEFAULT_PAGE
            }

            _keywordFlow.value = keyword

            logD(
                "开始聚合搜索",
                "关键词:$keyword 页:$page 插件数:${pluginSearchComponentsFlow.value?.size} 是否相同关键词:$isSameKeyword"
            )

            //并行/并发搜索
            pluginSearchComponentsFlow.value?.apply {
                _searchDataList.value = DataState.Loading
            }?.asFlow()?.flatMapMerge { info ->
                flow {
                    runCatching {
                        emit(info.second.getSearchData(keyword, page).map {
                            it.apply {
                                //填入所属插件信息
                                action?.extraData = info.first
                            }
                        })
                    }
                }
            }?.toList()?.let { combineResult ->
                //平行合并数据
                //每个插件对等Index结果交织在一起
                val combine = mutableListOf<BaseData>()
                val pluginIterator = mutableListOf<Iterator<BaseData>>()
                var maxCountOfIterations = 0
                combineResult.forEach {
                    if (it.size > maxCountOfIterations)
                        maxCountOfIterations = it.size
                    pluginIterator.add(it.iterator())
                }
                repeat(maxCountOfIterations) {
                    pluginIterator.forEach {
                        if (it.hasNext())
                            combine.add(it.next())
                    }
                }
                //准备更新
                logD("聚合搜索结果", "共${combineResult.size}个数据源 ${combineResult.size}个结果")
                _searchDataList.value =
                    this@MediaCombineSearchViewModel.successIns<MutableDynamicReferenceListData<BaseData>>()
                        .apply {
                            if (!isSameKeyword) {
                                logD("填充聚合数据", "size=${combine.size}")
                                data().putData(combine)
                            } else {
                                logD("插入聚合数据", "size=${combine.size}")
                                data().appendData(combine)
                            }
                        }
                if (combine.isNotEmpty())
                    page++
            }

        }
    }

    override fun onCleared() {
        destroySuccessIns()
        super.onCleared()
    }

}