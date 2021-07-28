package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.AnimeShowBean
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.model.impls.EverydayAnimeModel
import com.skyd.imomoe.model.interfaces.IEverydayAnimeModel
import com.skyd.imomoe.util.Util.getRealDayOfWeek
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*


class EverydayAnimeViewModel : ViewModel() {
    private val everydayAnimeModel: IEverydayAnimeModel = EverydayAnimeModel()
    var header: AnimeShowBean = AnimeShowBean(
        "", "", "", "",
        "", null, "", null
    )
    var selectedTabIndex = -1
    var mldHeader: MutableLiveData<AnimeShowBean> = MutableLiveData()
    var tabList: MutableList<TabBean> = ArrayList()
    var mldTabList: MutableLiveData<List<TabBean>> = MutableLiveData()
    var everydayAnimeList: MutableList<List<AnimeCoverBean>> = ArrayList()
    var mldEverydayAnimeList: MutableLiveData<Boolean> = MutableLiveData()

    fun getEverydayAnimeData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                everydayAnimeModel.everydayAnimeData.apply {
                    selectedTabIndex = getRealDayOfWeek(
                        Calendar.getInstance(Locale.getDefault())
                            .get(Calendar.DAY_OF_WEEK)
                    ) - 1
                    header = third
                    tabList.clear()
                    tabList.addAll(first)
                    mldTabList.postValue(tabList)
                    everydayAnimeList.clear()
                    everydayAnimeList.addAll(second)
                    mldEverydayAnimeList.postValue(true)
                    mldHeader.postValue(header)
                }
            } catch (e: Exception) {
                selectedTabIndex = -1
                tabList.clear()
                everydayAnimeList.clear()
                mldEverydayAnimeList.postValue(false)
                e.printStackTrace()
                "${App.context.getString(R.string.get_data_failed)}\n${e.message}"
                    .showToastOnThread(Toast.LENGTH_LONG)
            }
        }
    }

    companion object {
        const val TAG = "EverydayAnimeViewModel"
    }
}