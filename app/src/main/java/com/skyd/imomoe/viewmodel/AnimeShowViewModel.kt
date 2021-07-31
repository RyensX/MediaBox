package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.IAnimeShowBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.AnimeShowModel
import com.skyd.imomoe.model.interfaces.IAnimeShowModel
import com.skyd.imomoe.util.Util.showToastOnThread
import com.skyd.imomoe.view.adapter.SerializableRecycledViewPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*


class AnimeShowViewModel : ViewModel() {
    private val animeShowModel: IAnimeShowModel by lazy {
        DataSourceManager.create(IAnimeShowModel::class.java) ?: AnimeShowModel()
    }
    var childViewPool: SerializableRecycledViewPool? = null
    var viewPool: SerializableRecycledViewPool? = null
    var animeShowList: MutableList<IAnimeShowBean> = ArrayList()
    var mldGetAnimeShowList: MutableLiveData<Int> = MutableLiveData()   // value：-1错误；0重新获取；1刷新
    var pageNumberBean: PageNumberBean? = null
    var newPageIndex: Pair<Int, Int>? = null

    private var isRequesting = false

    //http://www.yhdm.io版本
    fun getAnimeShowData(partUrl: String, isRefresh: Boolean = true) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (isRequesting) return@launch
                isRequesting = true
                pageNumberBean = null
                if (isRefresh) animeShowList.clear()
                val positionStart = animeShowList.size
                animeShowModel.getAnimeShowData(partUrl).apply {
                    animeShowList.addAll(first)
                    pageNumberBean = second
                }
                newPageIndex = Pair(positionStart, animeShowList.size - positionStart)
                mldGetAnimeShowList.postValue(if (isRefresh) 0 else 1)
                isRequesting = false
            } catch (e: Exception) {
                animeShowList.clear()
                mldGetAnimeShowList.postValue(-1)
                isRequesting = false
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    companion object {
        const val TAG = "AnimeShowViewModel"
    }
}