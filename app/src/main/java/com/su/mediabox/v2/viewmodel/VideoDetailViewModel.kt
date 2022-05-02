package com.su.mediabox.v2.viewmodel

import androidx.lifecycle.*
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.R
import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.bean.ResponseDataType
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.v2.been.*
import com.su.mediabox.pluginapi.v2.components.IVideoDetailDataComponent
import com.su.mediabox.util.lazyAcquireComponent
import com.su.mediabox.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoDetailViewModel : ViewModel() {

    private val videoDetailModel by lazyAcquireComponent<IVideoDetailDataComponent>()

    var partUrl: String = ""
    var cover = ""
    var title: String = ""

    private val _videoData: MutableLiveData<Pair<ResponseDataType, List<BaseData>>> =
        MutableLiveData()
    val videoData: LiveData<Pair<ResponseDataType, List<BaseData>>> = _videoData

    private var rawFavData: LiveData<MediaFavorite?>? = null
    private val _isFavVideo = MediatorLiveData<Boolean>()
    val isFavVideo: LiveData<Boolean> = _isFavVideo

    //切换收藏状态
    fun switchFavState() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_isFavVideo.value == true) {
                getAppDataBase().favoriteDao().deleteFavorite(partUrl)
            } else {
                //如果按追番前已经看过则同步进度
                val history = getAppDataBase().historyDao().getHistory(partUrl)
                getAppDataBase().favoriteDao().insertFavorite(
                    MediaFavorite(
                        partUrl, title, System.currentTimeMillis(), cover,
                        lastEpisodeTitle = history?.lastEpisodeTitle,
                        lastEpisodeUrl = history?.lastEpisodeUrl
                    )
                )
            }
        }
    }

    //更新监听的目标视频作品
    private fun updateFavTarget() {
        viewModelScope.launch(Dispatchers.IO) {
            getAppDataBase().favoriteDao().getFavoriteLiveData(partUrl).also { liveData ->
                //重新绑定
                withContext(Dispatchers.Main) {
                    rawFavData?.also {
                        _isFavVideo.removeSource(it)
                    }
                    _isFavVideo.addSource(liveData) {
                        _isFavVideo.postValue(it != null)
                    }
                    rawFavData = liveData
                }
                //智能更新封面
                liveData.value?.also {
                    if (it.cover != cover) {
                        getAppDataBase().favoriteDao().updateFavorite(it.apply {
                            cover = this@VideoDetailViewModel.cover
                        })
                    }
                }
            }
        }
    }

    fun getAnimeDetailData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                videoDetailModel.getAnimeDetailData(partUrl).apply {
                    cover = first
                    title = second
                    _videoData.postValue(Pair(ResponseDataType.REFRESH, third))
                }
                updateFavTarget()
            } catch (e: Exception) {
                _videoData.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }
}