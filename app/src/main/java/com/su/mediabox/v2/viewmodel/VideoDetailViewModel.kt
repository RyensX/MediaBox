package com.su.mediabox.v2.viewmodel

import android.view.Gravity
import androidx.lifecycle.*
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.R
import com.su.mediabox.bean.FavoriteAnimeBean
import com.su.mediabox.bean.ResponseDataType
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.v2.been.*
import com.su.mediabox.pluginapi.v2.components.IVideoDetailDataComponent
import com.su.mediabox.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoDetailViewModel : ViewModel() {

    private val videoDetailModel: IVideoDetailDataComponent by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IVideoDetailDataComponent::class.java)
    }
    var partUrl: String = ""
    var cover = ""
    var title: String = ""

    private val _videoData: MutableLiveData<Pair<ResponseDataType, List<BaseData>>> =
        MutableLiveData()
    val videoData: LiveData<Pair<ResponseDataType, List<BaseData>>> = _videoData

    private var rawFavData: LiveData<FavoriteAnimeBean?>? = null
    private val _isFavVideo = MediatorLiveData<Boolean>()
    val isFavVideo: LiveData<Boolean> = _isFavVideo

    //切换收藏状态
    fun switchFavState() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_isFavVideo.value == true) {
                getAppDataBase().favoriteAnimeDao().deleteFavoriteAnime(partUrl)
            } else {
                //如果按追番前已经看过则同步进度
                val history = getAppDataBase().historyDao().getHistory(partUrl)
                getAppDataBase().favoriteAnimeDao().insertFavoriteAnime(
                    FavoriteAnimeBean(
                        //UP_TODO 2022/2/28 22:16 0 使用新的多类型系统
                        Constant.ViewHolderTypeString.ANIME_COVER_8,
                        "",
                        partUrl,
                        title,
                        System.currentTimeMillis(),
                        cover,
                        lastEpisode = history?.lastEpisode,
                        lastEpisodeUrl = history?.lastEpisodeUrl
                    )
                )
            }
        }
    }

    //更新监听的目标视频作品
    private fun updateFavTarget() {
        viewModelScope.launch(Dispatchers.IO) {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnimeLiveData(partUrl).also { liveData ->
                withContext(Dispatchers.Main) {
                    rawFavData?.also {
                        _isFavVideo.removeSource(it)
                    }
                    _isFavVideo.addSource(liveData) {
                        _isFavVideo.postValue(it != null)
                    }
                    rawFavData = liveData
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