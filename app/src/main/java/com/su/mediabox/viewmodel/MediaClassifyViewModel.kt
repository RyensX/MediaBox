package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.pluginapi.action.ClassifyAction
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.data.ClassifyItemData
import com.su.mediabox.pluginapi.components.IMediaClassifyPageDataComponent
import com.su.mediabox.util.PluginIO
import com.su.mediabox.util.toLiveData
import com.su.mediabox.view.fragment.MediaClassifyBottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.su.mediabox.config.Const.ViewComponent.DEFAULT_PAGE
import com.su.mediabox.util.DataState
import com.su.mediabox.util.lazyAcquireComponent
import kotlinx.coroutines.CoroutineExceptionHandler

class MediaClassifyViewModel : ViewModel() {

    private val component by lazyAcquireComponent<IMediaClassifyPageDataComponent>()

    private var page = DEFAULT_PAGE

    private var _currentClassify = MutableLiveData<ClassifyAction>()
    val currentClassify = _currentClassify.toLiveData()

    private val _classifyItemDataList = MutableLiveData<DataState>(DataState.INIT)
    val classifyItemDataList = _classifyItemDataList.toLiveData()

    private val _classifyDataList = MutableLiveData<DataState>()
    val classifyDataList = _classifyDataList.toLiveData()

    private val itemDataDispatcher =
        Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            _classifyItemDataList.postValue(DataState.FAILED(throwable))
        }

    private val dataDispatcher =
        Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            _classifyDataList.postValue(DataState.FAILED(throwable))
        }

    fun getClassifyItemData() {
        _classifyItemDataList.postValue(DataState.LOADING)
        viewModelScope.launch(itemDataDispatcher) {
            //获取原始分类项数据
            val rawClassify =
                //testData()
                component.getClassifyItemData()
            if (rawClassify.isEmpty()) {
                _classifyItemDataList.postValue(DataState.FAILED(RuntimeException("分类项为空")))
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
            _classifyItemDataList.postValue(
                DataState.SUCCESS.getIns<BaseData>(itemDataDispatcher).putData(classify)
            )
        }
    }

    fun getClassifyData(
        classifyAction: ClassifyAction = currentClassify.value ?: ClassifyAction.obtain()
    ) {
        _classifyDataList.postValue(DataState.LOADING)
        viewModelScope.launch(dataDispatcher) {
            _classifyDataList.postValue(
                if (classifyAction != currentClassify.value) {
                    page = DEFAULT_PAGE
                    DataState.SUCCESS.getIns<BaseData>(dataDispatcher)
                        .putData(component.getClassifyData(classifyAction, page))
                } else {
                    DataState.SUCCESS.getIns<BaseData>(dataDispatcher)
                        .appendData(component.getClassifyData(classifyAction, page))
                }
            )
            page++
            _currentClassify.postValue(classifyAction)
        }
    }
}