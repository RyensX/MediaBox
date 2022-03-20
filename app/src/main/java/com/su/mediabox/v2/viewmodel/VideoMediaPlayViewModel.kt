package com.su.mediabox.v2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.database.DatabaseOperations.insertHistoryData
import com.su.mediabox.database.DatabaseOperations.updateFavoriteData
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.v2.been.VideoPlayMedia
import com.su.mediabox.pluginapi.v2.components.IVideoPlayComponent
import com.su.mediabox.util.PluginIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class VideoMediaPlayViewModel : ViewModel() {

    private val playComponent by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IVideoPlayComponent::class.java)
    }

    lateinit var detailPartUrl: String
    lateinit var coverUrl: String
    lateinit var videoName: String

    var currentPlayEpisodeUrl by Delegates.notNull<String>()
        private set

    val currentVideoPlayMedia = MutableLiveData<VideoPlayMedia>()
    val currentDanmakuData = MutableLiveData<Pair<String, Map<String, String>?>>()

    fun playVideoMedia(episodeUrl: String = currentPlayEpisodeUrl) {
        if (episodeUrl.isNotBlank()) {
            //开始解析
            viewModelScope.launch(Dispatchers.PluginIO) {
                playComponent.getVideoPlayMedia(episodeUrl).also {
                    currentVideoPlayMedia.postValue(it)
                    //记录历史
                    viewModelScope.apply {
                        updateFavoriteData(detailPartUrl, episodeUrl, it.title)
                        insertHistoryData(detailPartUrl, episodeUrl, coverUrl, videoName, it.title)
                    }
                }
            }
        }
    }

    fun initDanmakuData() {
        currentVideoPlayMedia.value?.run {
            viewModelScope.launch(Dispatchers.PluginIO) {
                playComponent.getDanmakuData(currentPlayEpisodeUrl)?.also {
                    currentDanmakuData.postValue(it)
                }
            }
        }
    }
}