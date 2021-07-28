package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.model.impls.AnimeDetailModel
import com.skyd.imomoe.model.interfaces.IAnimeDetailModel
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.collections.ArrayList


class AnimeDetailViewModel : ViewModel() {
    private val animeDetailModel: IAnimeDetailModel = AnimeDetailModel()
    var cover: ImageBean = ImageBean("", "", "", "")
    var title: String = ""
    var animeDetailList: MutableList<IAnimeDetailBean> = ArrayList()
    var mldAnimeDetailList: MutableLiveData<Boolean> = MutableLiveData()

    //www.yhdm.io
    fun getAnimeDetailData(partUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                animeDetailModel.getAnimeDetailData(partUrl).apply {
                    cover = first
                    title = second
                    animeDetailList.clear()
                    animeDetailList.addAll(third)
                }
                mldAnimeDetailList.postValue(true)
            } catch (e: Exception) {
                animeDetailList.clear()
                mldAnimeDetailList.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    companion object {
        const val TAG = "AnimeDetailViewModel"
    }
}