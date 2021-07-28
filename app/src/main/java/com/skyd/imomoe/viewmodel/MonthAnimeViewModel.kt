package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.model.impls.MonthAnimeModel
import com.skyd.imomoe.model.interfaces.IMonthAnimeModel
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*


class MonthAnimeViewModel : ViewModel() {
    val monthAnimeModel: IMonthAnimeModel = MonthAnimeModel()
    var monthAnimeList: MutableList<AnimeCoverBean> = ArrayList()
    var mldMonthAnimeList: MutableLiveData<Boolean> = MutableLiveData()

    fun getMonthAnimeData(partUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                monthAnimeModel.getMonthAnimeData(partUrl).apply {
                    monthAnimeList.clear()
                    monthAnimeList.addAll(this)
                }
                mldMonthAnimeList.postValue(true)
            } catch (e: Exception) {
                monthAnimeList.clear()
                mldMonthAnimeList.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    companion object {
        const val TAG = "MonthAnimeViewModel"
    }
}