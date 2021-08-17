package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.SearchModel
import com.skyd.imomoe.model.interfaces.ISearchModel
import com.skyd.imomoe.util.Util.showToastOnIOThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchViewModel : ViewModel() {
    private val searchModel: ISearchModel by lazy {
        DataSourceManager.create(ISearchModel::class.java) ?: SearchModel()
    }

    var searchResultList: MutableList<AnimeCoverBean> = ArrayList()
    var mldSearchResultList: MutableLiveData<Int> = MutableLiveData()   // value：-1错误；0重新获取；1刷新
    var keyWord = ""
    var mldFailed: MutableLiveData<Boolean> = MutableLiveData()
    var searchHistoryList: MutableList<SearchHistoryBean> = ArrayList()
    var mldSearchHistoryList: MutableLiveData<Boolean> = MutableLiveData()
    var mldInsertCompleted: MutableLiveData<Boolean> = MutableLiveData()
    var mldUpdateCompleted: MutableLiveData<Int> = MutableLiveData()
    var mldDeleteCompleted: MutableLiveData<Int> = MutableLiveData()
    var pageNumberBean: PageNumberBean? = null

    fun getSearchData(keyWord: String, isRefresh: Boolean = true, partUrl: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isRefresh) searchResultList.clear()
                searchModel.getSearchData(keyWord, partUrl).apply {
                    searchResultList.addAll(first)
                    pageNumberBean = second
                    this@SearchViewModel.keyWord = keyWord
                    mldSearchResultList.postValue(if (isRefresh) 0 else 1)
                }
            } catch (e: Exception) {
                mldFailed.postValue(true)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnIOThread()
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
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnIOThread()
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val searchHistoryBean = searchHistoryList.removeAt(itemPosition)
                getAppDataBase().searchHistoryDao().deleteSearchHistory(searchHistoryBean.timeStamp)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mldDeleteCompleted.postValue(itemPosition)
            }
        }
    }

    companion object {
        const val TAG = "SearchViewModel"
    }
}