package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.PluginManager
import com.su.mediabox.R
import com.su.mediabox.util.showToast
import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.pluginapi.been.PageNumberBean
import com.su.mediabox.pluginapi.components.IMonthAnimeComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MonthAnimeViewModel : ViewModel() {
    private val monthAnimeModel: IMonthAnimeComponent by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IMonthAnimeComponent::class.java)
    }
    var monthAnimeList: MutableList<AnimeCoverBean> = ArrayList()
    var mldMonthAnimeList: MutableLiveData<Boolean> = MutableLiveData()
    var pageNumberBean: PageNumberBean? = null
    var newPageIndex: Pair<Int, Int>? = null

    fun getMonthAnimeData(partUrl: String, isRefresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                monthAnimeModel.getMonthAnimeData(partUrl).apply {
                    if (isRefresh) monthAnimeList.clear()
                    val positionStart = monthAnimeList.size
                    monthAnimeList.addAll(first)
                    pageNumberBean = second
                    newPageIndex = Pair(positionStart, monthAnimeList.size - positionStart)
                    mldMonthAnimeList.postValue(true)
                }
            } catch (e: Exception) {
                monthAnimeList.clear()
                mldMonthAnimeList.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    companion object {
        const val TAG = "MonthAnimeViewModel"
    }
}