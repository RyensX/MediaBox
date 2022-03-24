package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.bean.HistoryBean
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.util.PluginIO
import com.su.mediabox.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HistoryViewModel : ViewModel() {
    var historyList: MutableList<HistoryBean> = ArrayList()
    var mldHistoryList: MutableLiveData<Boolean> = MutableLiveData()
    var mldDeleteHistory: MutableLiveData<Int> = MutableLiveData()
    var mldDeleteAllHistory: MutableLiveData<Int> = MutableLiveData()

    fun getHistoryList() {
        viewModelScope.launch(Dispatchers.PluginIO) {
            try {
                historyList.clear()
                historyList.addAll(getAppDataBase().historyDao().getHistoryList())
                historyList.sortWith(Comparator { o1, o2 ->
                    // 负数表示按时间戳从大到小排列
                    -o1.time.compareTo(o2.time)
                })
                mldHistoryList.postValue(true)
            } catch (e: Exception) {
                historyList.clear()
                mldHistoryList.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    fun deleteHistory(historyBean: HistoryBean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAppDataBase().historyDao().deleteHistory(historyBean.animeUrl)
                val index = historyList.indexOf(historyBean)
                historyList.removeAt(index)
                mldDeleteHistory.postValue(index)
            } catch (e: Exception) {
                mldDeleteHistory.postValue(-1)
                e.printStackTrace()
                (App.context.getString(R.string.delete_failed) + "\n" + e.message).showToast()
            }
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAppDataBase().historyDao().deleteAllHistory()
                val itemCount: Int = historyList.size
                historyList.clear()
                mldDeleteAllHistory.postValue(itemCount)
            } catch (e: Exception) {
                mldDeleteAllHistory.postValue(0)
                e.printStackTrace()
                (App.context.getString(R.string.delete_failed) + "\n" + e.message).showToast()
            }
        }
    }

    companion object {
        const val TAG = "HistoryViewModel"
    }
}