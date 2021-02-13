package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const.ActionUrl.Companion.ANIME_SEARCH
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.util.ParseHtmlUtil.parseLpic
import com.skyd.imomoe.util.Util.showToastOnThread
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import kotlin.collections.ArrayList


class SearchViewModel : ViewModel() {
    var searchResultList: MutableList<AnimeCoverBean> = ArrayList()
    var mldSearchResultList: MutableLiveData<String> = MutableLiveData()
    var mldFailed: MutableLiveData<Boolean> = MutableLiveData()
    var searchHistoryList: MutableList<SearchHistoryBean> = ArrayList()
    var mldSearchHistoryList: MutableLiveData<Boolean> = MutableLiveData()
    var mldInsertCompleted: MutableLiveData<Boolean> = MutableLiveData()
    var mldUpdateCompleted: MutableLiveData<Int> = MutableLiveData()
    var mldDeleteCompleted: MutableLiveData<Int> = MutableLiveData()

    fun getSearchData(keyWord: String) {
        Thread {
            try {
                val document = Jsoup.connect(Api.MAIN_URL + ANIME_SEARCH + keyWord).get()
                val lpic: Elements = document.getElementsByClass("area")
                    .select("[class=fire l]").select("[class=lpic]")
                searchResultList.clear()
                searchResultList.addAll(parseLpic(lpic[0]))
                mldSearchResultList.postValue(keyWord)
            } catch (e: Exception) {
                mldFailed.postValue(true)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }.start()
    }

    fun getSearchHistoryData() {
        Thread {
            try {
                searchHistoryList.clear()
                searchHistoryList.addAll(getAppDataBase().searchHistoryDao().getSearchHistoryList())
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            } finally {
                mldSearchHistoryList.postValue(true)
            }
        }.start()
    }

    fun insertSearchHistory(searchHistoryBean: SearchHistoryBean) {
        Thread {
            try {
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
        }.start()
    }

    fun updateSearchHistory(searchHistoryBean: SearchHistoryBean, itemPosition: Int) {
        Thread {
            try {
                searchHistoryList[itemPosition] = searchHistoryBean
                getAppDataBase().searchHistoryDao().updateSearchHistory(searchHistoryBean)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mldUpdateCompleted.postValue(itemPosition)
            }
        }.start()
    }

    fun deleteSearchHistory(itemPosition: Int) {
        Thread {
            try {
                val searchHistoryBean = searchHistoryList.removeAt(itemPosition)
                getAppDataBase().searchHistoryDao().deleteSearchHistory(searchHistoryBean.timeStamp)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mldDeleteCompleted.postValue(itemPosition)
            }
        }.start()
    }

    companion object {
        const val TAG = "SearchViewModel"
    }
}