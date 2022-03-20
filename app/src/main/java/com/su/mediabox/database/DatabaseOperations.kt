package com.su.mediabox.database

import android.util.Log
import com.su.mediabox.bean.HistoryBean
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseOperations {

    // 更新追番集数数据
    fun CoroutineScope.updateFavoriteData(
        detailPartUrl: String,
        lastEpisodeUrl: String,
        lastEpisode: String,
        time: Long = System.currentTimeMillis()
    ) {
        launch(Dispatchers.IO) {
            try {
                val favoriteAnimeDao = getAppDataBase().favoriteAnimeDao()
                val favoriteAnimeBean = favoriteAnimeDao.getFavoriteAnime(detailPartUrl)
                if (favoriteAnimeBean != null) {
                    favoriteAnimeBean.lastEpisode = lastEpisode
                    favoriteAnimeBean.lastEpisodeUrl = lastEpisodeUrl
                    favoriteAnimeBean.time = time
                    favoriteAnimeDao.updateFavoriteAnime(favoriteAnimeBean)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 插入观看历史记录
    fun CoroutineScope.insertHistoryData(
        detailPartUrl: String,
        episodeUrl: String,
        coverUrl: String,
        videoName: String,
        episodeName: String
    ) {
        launch(Dispatchers.IO) {
            Log.d("更新播放历史", detailPartUrl)
            try {
                if (coverUrl.isBlank()) {
                    "封面为空，无法记录播放历史".showToast()
                } else {
                    getAppDataBase().historyDao().insertHistory(
                        HistoryBean(
                            Constant.ViewHolderTypeString.ANIME_COVER_9, "", detailPartUrl,
                            videoName,
                            System.currentTimeMillis(),
                            coverUrl,
                            episodeUrl,
                            episodeName
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}