package com.su.mediabox.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.PluginManager
import com.su.mediabox.R
import com.su.mediabox.util.Util.getRealDayOfWeek
import com.su.mediabox.util.showToast
import com.su.mediabox.plugin.interfaces.IEverydayAnimeModel
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.AnimeShowBean
import com.su.mediabox.plugin.standard.been.TabBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class EverydayAnimeViewModel : ViewModel() {
    private val everydayAnimeModel: IEverydayAnimeModel by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IEverydayAnimeModel::class.java)
    }
    var header: AnimeShowBean = AnimeShowBean(
        "", "", "", "",
        "", null, "", null
    )
    var selectedTabIndex = -1
    var mldHeader: MutableLiveData<AnimeShowBean> = MutableLiveData()
    var tabList: MutableList<TabBean> = ArrayList()
    var mldTabList: MutableLiveData<List<TabBean>> = MutableLiveData()
    var everydayAnimeList: MutableList<List<AnimeCoverBean>> = ArrayList()
    var mldEverydayAnimeList: MutableLiveData<MutableList<List<AnimeCoverBean>>?> =
        MutableLiveData()

    fun getEverydayAnimeData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                everydayAnimeModel.getEverydayAnimeData().apply {
                    if (first.size != second.size) throw Exception("tabs count != tabList count")
                    selectedTabIndex = getRealDayOfWeek(
                        Calendar.getInstance(Locale.getDefault())
                            .get(Calendar.DAY_OF_WEEK)
                    ) - 1
                    header = third
                    tabList.clear()
                    tabList.addAll(first)
                    mldTabList.postValue(tabList)
                    mldEverydayAnimeList.postValue(second)
                    mldHeader.postValue(header)
                }
            } catch (e: Exception) {
                selectedTabIndex = -1
                tabList.clear()
                mldEverydayAnimeList.postValue(null)
                e.printStackTrace()
                "${App.context.getString(R.string.get_data_failed)}\n${e.message}"
                    .showToast(Toast.LENGTH_LONG)
            }
        }
    }

    companion object {
        const val TAG = "EverydayAnimeViewModel"
    }
}