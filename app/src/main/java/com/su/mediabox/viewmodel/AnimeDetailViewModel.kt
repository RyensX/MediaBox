package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.R
import com.su.mediabox.bean.*
import com.su.mediabox.pluginapi.been.IAnimeDetailBean
import com.su.mediabox.pluginapi.components.IAnimeDetailComponent
import com.su.mediabox.util.lazyAcquireComponent
import com.su.mediabox.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Deprecated("在v2 API完善后移除")
class AnimeDetailViewModel : ViewModel() {

    private val animeDetailModel by lazyAcquireComponent<IAnimeDetailComponent>()

    var partUrl: String = ""
    var cover = ""
    var title: String = ""
    var animeDetailList: MutableList<IAnimeDetailBean> = ArrayList()
    var mldAnimeDetailList: MutableLiveData<Pair<ResponseDataType, List<IAnimeDetailBean>>> =
        MutableLiveData()

    fun getAnimeDetailData(partUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                animeDetailModel.getAnimeDetailData(partUrl).apply {
                    cover = first
                    title = second
                    mldAnimeDetailList.postValue(Pair(ResponseDataType.REFRESH, third))
                }
            } catch (e: Exception) {
                mldAnimeDetailList.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    companion object {
        const val TAG = "AnimeDetailViewModel"
    }
}