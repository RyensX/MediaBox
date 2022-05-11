package com.su.mediabox.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.database.DatabaseOperations.insertHistoryData
import com.su.mediabox.database.DatabaseOperations.updateFavoriteData
import com.su.mediabox.pluginapi.data.VideoPlayMedia
import com.su.mediabox.pluginapi.components.IVideoPlayPageDataComponent
import com.su.mediabox.util.PluginIO
import com.su.mediabox.util.lazyAcquireComponent
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

    val currentVideoPlayMedia = MutableLiveData<VideoPlayMedia>()
    val currentDanmakuData = MutableLiveData<Pair<String, Map<String, String>?>>()

    fun playVideoMedia(episodeUrl: String = currentPlayEpisodeUrl) {
        if (episodeUrl.isNotBlank()) {
            currentPlayEpisodeUrl = episodeUrl
            //开始解析
            viewModelScope.launch(Dispatchers.PluginIO) {
                playComponent.getVideoPlayMedia(episodeUrl).also {
                    Log.d("视频解析结果", "剧集：${it.title} 链接：$${it.videoPlayUrl}")
                    if (it.videoPlayUrl.isBlank())
                        throw RuntimeException("播放链接解析错误")
                    // VideoPlayMedia("测试","file:///storage/emulated/0/Android/data/com.su.mediabox.debug/files/DownloadAnime/萌萌侵略者/GEfErSXSJIsA.mp4").also {
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