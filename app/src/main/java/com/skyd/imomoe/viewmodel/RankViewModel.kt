package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyd.imomoe.PluginManager
import com.skyd.imomoe.util.showToast
import com.su.mediabox.plugin.interfaces.IRankModel
import com.su.mediabox.plugin.standard.been.TabBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class RankViewModel : ViewModel() {
    private val rankModel: IRankModel by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IRankModel::class.java)
    }
    var isRequesting = false
    var tabList: MutableList<TabBean> = Collections.synchronizedList(ArrayList())
    var mldRankData: MutableLiveData<Boolean> = MutableLiveData()

    fun getRankTabData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isRequesting) return@launch
                isRequesting = true

                rankModel.getRankTabData().apply {
                    tabList.clear()
                    tabList.addAll(this)
                    mldRankData.postValue(true)
                    isRequesting = false
                }
            } catch (e: Exception) {
                mldRankData.postValue(false)
                tabList.clear()
                isRequesting = false
                e.printStackTrace()
                e.message?.showToast(Toast.LENGTH_LONG)
            }
        }
    }

    companion object {
        const val TAG = "RankViewModel"
    }
}