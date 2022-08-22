package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.su.mediabox.database.DatabaseOperations.insertHistoryData
import com.su.mediabox.database.DatabaseOperations.updateFavoriteData
import com.su.mediabox.pluginapi.data.VideoPlayMedia
import com.su.mediabox.pluginapi.components.IVideoPlayPageDataComponent
import com.su.mediabox.util.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class VideoMediaPlayViewModel : ViewModel() {

    private val playComponent by lazyAcquireComponent<IVideoPlayPageDataComponent>()

    lateinit var detailPartUrl: String
    lateinit var coverUrl: String
    lateinit var videoName: String

    var currentPlayEpisodeUrl = ""
        private set

    private val _currentVideoPlayMedia = MutableLiveData<DataState<VideoPlayMedia>>()
    private val _currentDanmakuData = MutableLiveData<List<DanmakuItemData>?>()

    val currentVideoPlayMedia = _currentVideoPlayMedia.toLiveData()
    val currentDanmakuData = _currentDanmakuData.toLiveData()

    private val videoPlayMediaDispatcher =
        Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            _currentVideoPlayMedia.postValue(DataState.Failed(throwable))
        }

    fun playVideoMedia(episodeUrl: String = currentPlayEpisodeUrl) {
        if (episodeUrl.isNotBlank()) {
            _currentVideoPlayMedia.postValue(DataState.Loading)
            currentPlayEpisodeUrl = episodeUrl
            //开始解析
            viewModelScope.launch(videoPlayMediaDispatcher) {
                //一次动作在未完成工作下重复解析两次，降低因为网络问题解析失败概率
                let {
                    var result by Delegates.notNull<VideoPlayMedia>()
                    for (i in 0 until 2) {
                        result = playComponent.getVideoPlayMedia(episodeUrl)
                        if (result.videoPlayUrl.isNotBlank())
                            break
                    }
                    result
                }.also {
                    logD("视频解析结果", "剧集：${it.title} 链接：$${it.videoPlayUrl}")
                    if (it.videoPlayUrl.isBlank())
                        throw RuntimeException("无法解析出有效播放链接")
                    // VideoPlayMedia("测试","file:///storage/emulated/0/Android/data/com.su.mediabox.debug/files/DownloadAnime/萌萌侵略者/GEfErSXSJIsA.mp4").also {
                    _currentVideoPlayMedia.postValue(DataState.Success(it))
                    //记录历史
                    viewModelScope.apply {
                        updateFavoriteData(detailPartUrl, episodeUrl, it.title)
                        insertHistoryData(detailPartUrl, episodeUrl, coverUrl, videoName, it.title)
                    }
                }
            }
        }
    }

    suspend fun putDanmaku(danmaku: String, time: Long, color: Int, type: Int): Boolean {
        logD("发送弹幕", danmaku)
        return when (val dataState = currentVideoPlayMedia.value) {
            is DataState.Success -> {
                playComponent.putDanmaku(
                    videoName, dataState.data!!.title, currentPlayEpisodeUrl,
                    danmaku, time, color, type
                )
                true
            }
            else -> false
        }
    }

    fun initDanmakuData() {
        when (val dataState = currentVideoPlayMedia.value) {
            is DataState.Success -> {
                dataState.data?.apply {
                    logD("加载弹幕", "媒体:$videoName 集数:$title")
                    viewModelScope.launch(Dispatchers.PluginIO) {
                        playComponent.getDanmakuData(videoName, title, currentPlayEpisodeUrl)
                            ?.also {
                                logD("加载弹幕成功", "媒体:$videoName 集数:$title 数量:${it.size}")
                                _currentDanmakuData.postValue(it)
                            }
                    }
                }
            }
            else -> logD("加载弹幕失败", "当前无播放媒体")
        }
    }
}