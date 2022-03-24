package com.su.mediabox.v2.viewmodel

import androidx.lifecycle.*
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.R
import com.su.mediabox.bean.SearchHistoryBean
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.pluginapi.v2.components.IVideoSearchDataComponent
import com.su.mediabox.util.PluginIO
import com.su.mediabox.util.removeAllObserver
import com.su.mediabox.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoSearchViewModel : ViewModel() {

    companion object {
        const val DEFAULT_PAGE = 1
    }

    private val videoSearchViewModel: IVideoSearchDataComponent by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IVideoSearchDataComponent::class.java)
    }

    private var page = DEFAULT_PAGE
    var mKeyWord = ""
    var lastLoadSize = 0
        private set

    private val _showState = MutableLiveData<ShowState>()

    /**
     * 显示状态
     */
    val showState: LiveData<ShowState> = _showState

    //搜索历史
    private val searchHistory = getAppDataBase().searchHistoryDao().getSearchHistoryListLiveData()
        .apply {
            //观察搜索历史自动更新
            observeForever {
                viewModelScope.launch(Dispatchers.Default) {
                    when (showState.value) {
                        null, ShowState.KEYWORD -> {
                            _resultData = it?.let { mutableListOf<Any>().apply { addAll(it) } }
                            _showState.postValue(ShowState.KEYWORD)
                        }
                        else -> {}
                    }
                }
            }
        }

    private var _resultData: MutableList<Any>? = null

    /**
     * 结果数据，可为搜索历史[SearchHistoryBean]或搜索结果[VideoLinearItemData]
     */
    val resultData: List<Any>?
        get() = _resultData

    fun getSearchData(keyWord: String = mKeyWord) {
        if (keyWord.isBlank())
            return
        updateSearchHistory(keyWord)
        viewModelScope.launch(Dispatchers.PluginIO) {
            try {
                val list = mutableListOf<Any>()

                //不一致则表示新的搜索
                if (keyWord != mKeyWord) {
                    page = DEFAULT_PAGE
                    mKeyWord = keyWord
                }
                if (showState.value == ShowState.RESULT)
                    _resultData?.also { list.addAll(it) }

                val result = videoSearchViewModel.getSearchData(keyWord, page++)
                lastLoadSize = result.size
                list.addAll(result)
                _resultData = list
                _showState.postValue(ShowState.RESULT)
                //空列表由RV自行提供空视图，非搜索失败
            } catch (e: Exception) {
                _showState.postValue(ShowState.FAILED)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    fun showSearchHistory() {
        _resultData = searchHistory.value?.let { mutableListOf<Any>().apply { addAll(it) } }
        page = DEFAULT_PAGE
        _showState.postValue(ShowState.KEYWORD)
    }

    private fun updateSearchHistory(keyWord: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getAppDataBase().searchHistoryDao()
                .insertSearchHistory(SearchHistoryBean(keyWord, System.currentTimeMillis()))
        }
    }

    fun deleteSearchHistory(keyWord: String = mKeyWord) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAppDataBase().searchHistoryDao().deleteSearchHistory(keyWord)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        searchHistory.removeAllObserver()
    }

    enum class ShowState {
        FAILED, RESULT, KEYWORD
    }
}