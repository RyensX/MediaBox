package com.su.mediabox.viewmodel

import androidx.lifecycle.*
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.bean.ResponseDataType
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.components.IMediaDetailPageDataComponent
import com.su.mediabox.util.lazyAcquireComponent
import com.su.mediabox.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaDetailViewModel : ViewModel() {

    private val videoDetailModel by lazyAcquireComponent<IMediaDetailPageDataComponent>()

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
                //更新信息
                liveData.value?.also {
                    getAppDataBase().favoriteDao().updateFavorite(it.apply {
                        mediaTitle = title
                        cover = this@MediaDetailViewModel.cover
                    })
                }
            }
        }
    }

    fun getMediaDetailData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                videoDetailModel.getMediaDetailData(partUrl).apply {
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