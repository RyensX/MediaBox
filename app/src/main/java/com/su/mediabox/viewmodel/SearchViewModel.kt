package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.R
import com.su.mediabox.bean.ResponseDataType
import com.su.mediabox.bean.SearchHistoryBean
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.pluginapi.been.PageNumberBean
import com.su.mediabox.pluginapi.components.ISearchComponent
import com.su.mediabox.util.PluginIO
import com.su.mediabox.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Deprecated("更新2.0后删除")
class SearchViewModel : ViewModel() {
    private val searchModel: ISearchComponent by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent()
    }

    var searchResultList: MutableList<AnimeCoverBean> = ArrayList()
    var mldSearchResultList: MutableLiveData<Pair<ResponseDataType, List<AnimeCoverBean>>> =
        MutableLiveData()
    var keyWord = ""
    var searchHistoryList: MutableList<SearchHistoryBean> = ArrayList()
    var mldSearchHistoryList: MutableLiveData<Boolean> = MutableLiveData()
    var mldInsertCompleted: MutableLiveData<Boolean> = MutableLiveData()
    var mldUpdateCompleted: MutableLiveData<Int> = MutableLiveData()
    var mldDeleteCompleted: MutableLiveData<Int> = MutableLiveData()
    var pageNumberBean: PageNumberBean? = null

    fun getSearchData(keyWord: String, isRefresh: Boolean = true, partUrl: String = "") {
        viewModelScope.launch(Dispatchers.PluginIO) {
            try {
                searchModel.getSearchData(keyWord, partUrl).apply {
                    pageNumberBean = second
                    this@SearchViewModel.keyWord = keyWord
                    mldSearchResultList.postValue(
                        Pair(
                            if (isRefresh) ResponseDataType.REFRESH else ResponseDataType.LOAD_MORE,
                            first
                        )
                    )
                }
            } catch (e: Exception) {
                mldSearchResultList.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    fun getSearchHistoryData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                searchHistoryList.clear()
                searchHistoryList.addAll(getAppDataBase().searchHistoryDao().getSearchHistoryList())
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            } finally {
                mldSearchHistoryList.postValue(true)
            }
        }
    }

    fun insertSearchHistory(searchHistoryBean: SearchHistoryBean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (searchHistoryList.isEmpty()) searchHistoryList.addAll(
                    getAppDataBase().searchHistoryDao().getSearchHistoryList()
                )
                val index = searchHistoryList.indexOf(searchHistoryBean)
                if (index != -1) {
                    searchHistoryList.removeAt(index)
                    searchHistoryList.add(0, searchHistoryBean)
                    getAppDataBase().searchHistoryDao().deleteSearchHistory(searchHistoryBean.title)
                    getAppDataBase().searchHistoryDao().insertSearchHistory(searchHistoryBean)
                } else {
                    searchHistoryList.add(0, searchHistoryBean)
                    getAppDataBase().searchHistoryDao().insertSearchHistory(searchHistoryBean)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mldInsertCompleted.postValue(true)
            }
        }
    }

    fun updateSearchHistory(searchHistoryBean: SearchHistoryBean, itemPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                searchHistoryList[itemPosition] = searchHistoryBean
                getAppDataBase().searchHistoryDao().updateSearchHistory(searchHistoryBean)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mldUpdateCompleted.postValue(itemPosition)
            }
        }
    }

    fun deleteSearchHistory(itemPosition: Int) {

    }

    companion object {
        const val TAG = "SearchViewModel"
    }
}