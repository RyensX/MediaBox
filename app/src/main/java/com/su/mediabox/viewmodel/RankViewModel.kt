package com.su.mediabox.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.been.TabBean
import com.su.mediabox.pluginapi.components.IRankComponent
import com.su.mediabox.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class RankViewModel : ViewModel() {
    private val rankModel: IRankComponent by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IRankComponent::class.java)
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