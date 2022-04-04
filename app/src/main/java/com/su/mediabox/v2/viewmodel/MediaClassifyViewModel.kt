package com.su.mediabox.v2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.v2.action.ClassifyAction
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.pluginapi.v2.been.ClassifyItemData
import com.su.mediabox.pluginapi.v2.been.GridItemData
import com.su.mediabox.pluginapi.v2.been.VideoInfoItemData
import com.su.mediabox.pluginapi.v2.components.IMediaClassifyDataComponent
import com.su.mediabox.util.PluginIO
import com.su.mediabox.util.toLiveData
import com.su.mediabox.view.fragment.MediaClassifyBottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.su.mediabox.config.Const.ViewComponent.DEFAULT_PAGE
import kotlinx.coroutines.delay

class MediaClassifyViewModel : ViewModel() {

    private val component by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(
            IMediaClassifyDataComponent::class.java
        )
    }

    private var page = DEFAULT_PAGE

    private var _currentClassify = MutableLiveData<ClassifyAction>()
    val currentClassify = _currentClassify.toLiveData()

    private val _classifyItemDataList = MutableLiveData<List<GridItemData>>()
    val classifyItemDataList = _classifyItemDataList.toLiveData()

    private val _classifyDataList = MutableLiveData<List<BaseData>>()
    val classifyDataList = _classifyDataList.toLiveData()

    fun getClassifyItemData() {
        viewModelScope.launch(Dispatchers.PluginIO) {
            //获取原始分类项数据
            val rawClassify =
                //testData()
            component.getClassifyItemData()
            if (rawClassify.isEmpty()) {
                _classifyItemDataList.postValue(rawClassify)
                return@launch
            }
            //自动分类
            val classify = mutableListOf<GridItemData>()
            val classifyMap = mutableMapOf<String, MutableList<ClassifyItemData>>()
            rawClassify.forEach { itemData ->
                itemData.spanSize = 1
                //没有ClassifyAction或者不完整的不显示
                val action = itemData.action
                if (action is ClassifyAction && action.classifyCategory != null && action.classify != null && action.url != null) {
                    val list = classifyMap[action.classifyCategory!!]
                        ?: mutableListOf<ClassifyItemData>().also {
                            classifyMap[action.classifyCategory!!] = it
                        }
                    list.add(itemData)
                }
            }
            //重新排列
            classifyMap.forEach { entry ->
                classify.add(MediaClassifyBottomSheetDialogFragment.ClassifyCategoryData(entry.key))
                entry.value.forEach { classify.add(it) }
            }
            //更新
            _classifyItemDataList.postValue(classify)
        }
    }

    fun getClassifyData(
        classifyAction: ClassifyAction = currentClassify.value ?: ClassifyAction.obtain()
    ) {
        viewModelScope.launch(Dispatchers.PluginIO) {
            val data = mutableListOf<BaseData>()
            if (classifyAction != currentClassify.value)
                page = DEFAULT_PAGE
            else
                classifyDataList.value?.also { data.addAll(it) }
            data.addAll(component.getClassifyData(classifyAction, page++))
            _classifyDataList.postValue(data)
            _currentClassify.postValue(classifyAction)
        }
    }
}