package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavoriteViewModel : ViewModel() {
    var favoriteList: MutableList<FavoriteAnimeBean> = ArrayList()
    var mldFavoriteList: MutableLiveData<Boolean> = MutableLiveData()

    fun getFavoriteData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                favoriteList.clear()
                favoriteList.addAll(getAppDataBase().favoriteAnimeDao().getFavoriteAnimeList())
                favoriteList.sortWith(Comparator { o1, o2 ->
                    // 负数表示按时间戳从大到小排列
                    -o1.time.compareTo(o2.time)
                })
                mldFavoriteList.postValue(true)
            } catch (e: Exception) {
                favoriteList.clear()
                mldFavoriteList.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    companion object {
        const val TAG = "FavoriteViewModel"
    }
}