package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.ResponseDataType
import com.skyd.imomoe.bean.IAnimeShowBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.AnimeShowModel
import com.skyd.imomoe.model.interfaces.IAnimeShowModel
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.adapter.SerializableRecycledViewPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AnimeShowViewModel : ViewModel() {
    private val animeShowModel: IAnimeShowModel by lazy {
        DataSourceManager.create(IAnimeShowModel::class.java) ?: AnimeShowModel()
    }
    var childViewPool: SerializableRecycledViewPool? = null
    var viewPool: SerializableRecycledViewPool? = null
    var animeShowList: MutableList<IAnimeShowBean> = ArrayList()
    var mldGetAnimeShowList: MutableLiveData<Pair<ResponseDataType, MutableList<IAnimeShowBean>>> =
        MutableLiveData()   // value：-1错误；0重新获取；1刷新
    var pageNumberBean: PageNumberBean? = null

    private var isRequesting = false

    //http://www.yhdm.io版本
    fun getAnimeShowData(partUrl: String, isRefresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isRequesting) return@launch
                isRequesting = true
                pageNumberBean = null
                animeShowModel.getAnimeShowData(partUrl).apply {
                    pageNumberBean = second
                    mldGetAnimeShowList.postValue(
                        Pair(
                            if (isRefresh) ResponseDataType.REFRESH else ResponseDataType.LOAD_MORE, first
                        )
                    )
                    isRequesting = false
                }
            } catch (e: Exception) {
                mldGetAnimeShowList.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
                isRequesting = false
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    companion object {
        const val TAG = "AnimeShowViewModel"
    }
}