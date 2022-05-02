package com.su.mediabox.database

import android.util.Log
import com.su.mediabox.bean.MediaHistory
import com.su.mediabox.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseOperations {

    // 更新媒体剧集数据
    fun CoroutineScope.updateFavoriteData(
        detailPartUrl: String,
        lastEpisodeUrl: String,
        lastEpisodeTitle: String,
        lastViewTime: Long = System.currentTimeMillis()
    ) {
        launch(Dispatchers.IO) {
            try {
                val favoriteDao = getAppDataBase().favoriteDao()
                val favorite = favoriteDao.getFavorite(detailPartUrl)
                if (favorite != null) {
                    favorite.lastEpisodeUrl = lastEpisodeUrl
                    favorite.lastEpisodeTitle = lastEpisodeTitle
                    favorite.lastViewTime = lastViewTime
                    favoriteDao.updateFavorite(favorite)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //更新媒体播放记录
    fun CoroutineScope.insertHistoryData(
        detailPartUrl: String,
        lastEpisodeUrl: String,
        coverUrl: String,
        lastEpisodeTitle: String,
        episodeName: String
    ) {
        launch(Dispatchers.IO) {
            Log.d("更新播放历史", detailPartUrl)
            try {
                if (coverUrl.isBlank()) {
                    "封面为空，无法记录播放历史".showToast()
                } else {
                    getAppDataBase().historyDao().insertHistory(
                        MediaHistory(
                            detailPartUrl, lastEpisodeTitle,
                            System.currentTimeMillis(),
                            coverUrl, lastEpisodeUrl, episodeName
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}