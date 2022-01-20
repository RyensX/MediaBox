package com.skyd.imomoe.util

import com.skyd.imomoe.database.entity.PlayRecordEntity
import com.skyd.imomoe.database.getOfflineDatabase
import com.skyd.imomoe.view.component.player.AnimeVideoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AnimeVideoPositionMemoryStore : AnimeVideoPlayer.PlayPositionMemoryDataStore {

    private val dao = getOfflineDatabase().playPlayRecordDao()

    override suspend fun getPlayPosition(url: String): Long? = dao.query(url)?.position

    override suspend fun putPlayPosition(url: String, position: Long) {
        dao.insert(PlayRecordEntity(url, position))
    }

    override fun positionFormat(position: Long): String = position.toString()
}