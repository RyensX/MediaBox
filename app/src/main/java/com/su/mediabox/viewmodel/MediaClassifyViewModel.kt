package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.pluginapi.action.ClassifyAction
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.data.ClassifyItemData
import com.su.mediabox.pluginapi.components.IMediaClassifyPageDataComponent
import com.su.mediabox.view.fragment.MediaClassifyBottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.su.mediabox.config.Const.ViewComponent.DEFAULT_PAGE
import com.su.mediabox.util.*
import com.su.mediabox.util.DataState.Success.Companion.destroySuccessIns
import com.su.mediabox.util.DataState.Success.Companion.successIns
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MediaClassifyViewModel : ViewModel() {

    private val component by lazyAcquireComponent<IMediaClassifyPageDataComponent>()

    private var page = DEFAULT_PAGE

    private var _currentClassify = MutableLiveData<ClassifyAction>()
    val currentClassify = _currentClassify.toLiveData()


    private val _classifyItemDataList =
        MutableStateFlow<DataState<MutableDynamicReferenceListData<BaseData>>>(DataState.Init)
    val classifyItemDataList: StateFlow<DataState<DynamicReferenceListData<BaseData>>> =
        _classifyItemDataList

    private val _classifyDataList =
        MutableStateFlow<DataState<MutableDynamicReferenceListData<BaseData>>>(DataState.Init)
    val classifyDataList: StateFlow<DataState<DynamicReferenceListData<BaseData>>> =
        _classifyDataList

    private val itemDataDispatcher =
        Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            _classifyItemDataList.value = DataState.Failed(throwable)
        }

    private val dataDispatcher =
        Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            _classifyDataList.value = DataState.Failed(throwable)
        }

    fun getClassifyItemData() {
        _classifyItemDataList.value = DataState.Loading
        viewModelScope.launch(itemDataDispatcher) {
            //获取原始分类项数据
            val rawClassify =
                //testData()
                component.getClassifyItemData()
            if (rawClassify.isEmpty()) {
                _classifyItemDataList.value = DataState.Failed(RuntimeException("分类项为空"))
                return@launch
            }
            //自动分类
            val classify = mutableListOf<BaseData>()
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
            _classifyItemDataList.value =
                itemDataDispatcher.successIns<MutableDynamicReferenceListData<BaseData>>().apply {
                    data().putData(classify)
                }
        }
    }

    fun getClassifyData(
        classifyAction: ClassifyAction = currentClassify.value ?: ClassifyAction.obtain()
    ) {
        _classifyDataList.value = DataState.Loading
        viewModelScope.launch(dataDispatcher) {
            _classifyDataList.value =
                dataDispatcher.successIns<MutableDynamicReferenceListData<BaseData>>().apply {
                    if (classifyAction != currentClassify.value) {
                        page = DEFAULT_PAGE
                        data().putData(component.getClassifyData(classifyAction, page))
                        logD("测试加载", "获取数据1")
                    } else {
                        data().appendData(component.getClassifyData(classifyAction, page))
                        logD("测试加载", "获取数据2")
                    }
                }
            logD("测试加载", "更新数据3")
            page++
            _currentClassify.postValue(classifyAction)
        }
    }

    override fun onCleared() {
        itemDataDispatcher.destroySuccessIns()
        dataDispatcher.destroySuccessIns()
        super.onCleared()
    }
}