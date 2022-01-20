package com.skyd.imomoe.util

import com.skyd.imomoe.view.component.player.AnimeVideoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//test
object AnimeVideoPositionMemoryStore : AnimeVideoPlayer.PlayPositionMemoryDataStore {

    private val map = mutableMapOf<String, Long>()

    override suspend fun getPlayPosition(url: String): Long? {
        withContext(Dispatchers.Main) {
            "获取播放进度".showToast()
        }
        return map[url]
    }

    override suspend fun putPlayPosition(url: String, position: Long) {
        withContext(Dispatchers.Main) {
            "记忆播放进度".showToast()
        }
        map[url] = position
    }

    override fun positionFormat(position: Long): String = position.toString()
}