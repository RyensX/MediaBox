package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.R
import com.su.mediabox.bean.ResponseDataType
import com.su.mediabox.pluginapi.been.IAnimeShowBean
import com.su.mediabox.pluginapi.been.PageNumberBean
import com.su.mediabox.pluginapi.components.IAnimeShowComponent
import com.su.mediabox.util.PluginIO
import com.su.mediabox.util.showToast
import com.su.mediabox.view.adapter.SerializableRecycledViewPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Deprecated("更新2.0后删除")
class AnimeShowViewModel : ViewModel() {
    private val animeShowModel: IAnimeShowComponent by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent()
    }
    var childViewPool: SerializableRecycledViewPool? = null
    var viewPool: SerializableRecycledViewPool? = null
    var animeShowList: MutableList<IAnimeShowBean> = ArrayList()
    var mldGetAnimeShowList: MutableLiveData<Pair<ResponseDataType, List<IAnimeShowBean>>> =
        MutableLiveData()   // value：-1错误；0重新获取；1刷新
    var pageNumberBean: PageNumberBean? = null

    private var isRequesting = false

    //http://www.yhdm.io版本
    fun getAnimeShowData(partUrl: String, isRefresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.PluginIO) {
            try {
                if (isRequesting) return@launch
                isRequesting = true
                pageNumberBean = null
                animeShowModel.getAnimeShowData(partUrl).apply {
                    pageNumberBean = second
                    mldGetAnimeShowList.postValue(
                        Pair(
                            if (isRefresh) ResponseDataType.REFRESH else ResponseDataType.LOAD_MORE,
                            first
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