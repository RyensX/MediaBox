package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.ResponseDataType
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.RankListModel
import com.skyd.imomoe.model.interfaces.IRankListModel
import com.skyd.imomoe.util.Util.showToastOnIOThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class RankListViewModel : ViewModel() {
    private val rankModel: IRankListModel by lazy {
        DataSourceManager.create(IRankListModel::class.java) ?: RankListModel()
    }
    var isRequesting = false
    var rankList: MutableList<AnimeCoverBean> = Collections.synchronizedList(ArrayList())
    var pageNumberBean: PageNumberBean? = null
    var mldRankData: MutableLiveData<Pair<ResponseDataType, MutableList<AnimeCoverBean>>> =
        MutableLiveData()

    fun getRankListData(partUrl: String, isRefresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isRequesting) return@launch
                isRequesting = true

                rankModel.getRankListData(partUrl).apply {
                    pageNumberBean = second
                    mldRankData.postValue(
                        Pair(
                            if (isRefresh) ResponseDataType.REFRESH else ResponseDataType.LOAD_MORE,
                            first.toMutableList()
                        )
                    )
                    isRequesting = false
                }
            } catch (e: Exception) {
                mldRankData.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
                isRequesting = false
                e.printStackTrace()
                e.message?.showToastOnIOThread(Toast.LENGTH_LONG)
            }
        }
    }

    companion object {
        const val TAG = "RankViewModel"
    }
}