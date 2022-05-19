package com.su.mediabox.viewmodel

import com.su.mediabox.util.logD
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.su.mediabox.database.DatabaseOperations.insertHistoryData
import com.su.mediabox.database.DatabaseOperations.updateFavoriteData
import com.su.mediabox.pluginapi.data.VideoPlayMedia
import com.su.mediabox.pluginapi.components.IVideoPlayPageDataComponent
import com.su.mediabox.util.PluginIO
import com.su.mediabox.util.lazyAcquireComponent
import com.su.mediabox.util.toLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class VideoMediaPlayViewModel : ViewModel() {

    private val playComponent by lazyAcquireComponent<IVideoPlayPageDataComponent>()

    lateinit var detailPartUrl: String
    lateinit var coverUrl: String
    lateinit var videoName: String

    var currentPlayEpisodeUrl by Delegates.notNull<String>()
        private set

    private val _currentVideoPlayMedia = MutableLiveData<VideoPlayMedia>()
    private val _currentDanmakuData = MutableLiveData<List<DanmakuItemData>?>()

    val currentVideoPlayMedia = _currentVideoPlayMedia.toLiveData()
    val currentDanmakuData = _currentDanmakuData.toLiveData()

    fun playVideoMedia(episodeUrl: String = currentPlayEpisodeUrl) {
        if (episodeUrl.isNotBlank()) {
            currentPlayEpisodeUrl = episodeUrl
            //开始解析
            viewModelScope.launch(Dispatchers.PluginIO) {
                playComponent.getVideoPlayMedia(episodeUrl).also {
                    logD("视频解析结果", "剧集：${it.title} 链接：$${it.videoPlayUrl}")
                    if (it.videoPlayUrl.isBlank())
                        throw RuntimeException("播放链接解析错误")
                    // VideoPlayMedia("测试","file:///storage/emulated/0/Android/data/com.su.mediabox.debug/files/DownloadAnime/萌萌侵略者/GEfErSXSJIsA.mp4").also {
                    _currentVideoPlayMedia.postValue(it)
                    //记录历史
                    viewModelScope.apply {
                        updateFavoriteData(detailPartUrl, episodeUrl, it.title)
                        insertHistoryData(detailPartUrl, episodeUrl, coverUrl, videoName, it.title)
                    }
                }
            }
        }
    }

    suspend fun putDanmaku(danmaku: String): Boolean = playComponent.putDanmaku(danmaku)

    fun initDanmakuData() {
        _currentVideoPlayMedia.value?.run {
            viewModelScope.launch(Dispatchers.PluginIO) {
                _currentDanmakuData.value?.apply {
                    playComponent.getDanmakuData(videoName, title, currentPlayEpisodeUrl)?.also {
                        _currentDanmakuData.postValue(it)
                    }
                }
            }
        }
    }
}