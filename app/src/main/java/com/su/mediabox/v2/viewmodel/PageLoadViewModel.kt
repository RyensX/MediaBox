package com.su.mediabox.v2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.config.Const
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.util.toLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PageLoadViewModel : ViewModel() {

    private var page = Const.ViewComponent.DEFAULT_PAGE

    private val _loadState = MutableLiveData<LoadState>()
    val loadState = _loadState.toLiveData()

    var loadDataFun: LoadData? = null

    private val jobContext =
        Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            _loadState.postValue(LoadState.FAILED(throwable))
        }

    fun loadData() {
        _loadState.value = LoadState.LOADING
        viewModelScope.launch(jobContext) {
            try {
                _loadState.postValue(
                    LoadState.SUCCESS.getIns(this@PageLoadViewModel)
                        .appendData(loadDataFun?.load(page).apply {
                            //只有无错误且有数据才递增
                            if (!isNullOrEmpty())
                                page++
                        })
                )
            } catch (e: Exception) {
                _loadState.postValue(LoadState.FAILED(e))
            }
        }
    }

    fun reLoadData() {
        page = Const.ViewComponent.DEFAULT_PAGE
        LoadState.SUCCESS.getIns(this).data = null
        loadData()
    }

    override fun onCleared() {
        LoadState.SUCCESS.destroyIns(this)
        super.onCleared()
    }

    sealed class LoadState {
        object LOADING : LoadState()
        class FAILED(val throwable: Throwable?) : LoadState()
        class SUCCESS private constructor() : LoadState() {

            companion object {

                private val insMap = mutableMapOf<Any, SUCCESS>()

                fun destroyIns(obj: Any) {
                    insMap.remove(obj)
                }

                fun getIns(obj: Any): SUCCESS {
                    return insMap[obj] ?: SUCCESS().also { insMap[obj] = it }
                }
            }

            var data: List<Any>? = null

            var isLoadEmptyData = false
            fun appendData(appendData: List<Any>?): SUCCESS {
                isLoadEmptyData = appendData.isNullOrEmpty()
                if (appendData.isNullOrEmpty())
                    return this
                val list = mutableListOf<Any>()
                data?.also { list.addAll(it) }
                list.addAll(appendData)
                data = list
                return this
            }

        }
    }

    interface LoadData {
        suspend fun load(page: Int): List<Any>?
    }
}