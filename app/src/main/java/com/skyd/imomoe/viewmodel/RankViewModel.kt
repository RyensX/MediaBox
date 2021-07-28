package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.model.impls.RankModel
import com.skyd.imomoe.model.interfaces.IRankModel
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class RankViewModel : ViewModel() {
    private val rankModel: IRankModel = RankModel()
    var isRequesting = false
    var tabList: MutableList<TabBean> = Collections.synchronizedList(ArrayList())
    var rankList: MutableList<List<AnimeCoverBean>> = Collections.synchronizedList(ArrayList())
    var mldRankData: MutableLiveData<Boolean> = MutableLiveData()

    fun getRankData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (isRequesting) return@launch
                isRequesting = true

                rankModel.rankData.apply {
                    tabList.clear()
                    rankList.clear()
                    tabList.addAll(first)
                    rankList.addAll(second)
                }
                mldRankData.postValue(true)
                isRequesting = false
            } catch (e: Exception) {
                mldRankData.postValue(false)
                tabList.clear()
                rankList.clear()
                isRequesting = false
                e.printStackTrace()
                e.message?.showToastOnThread(Toast.LENGTH_LONG)
            }
        }
    }

    companion object {
        const val TAG = "RankViewModel"
    }
}