package com.skyd.imomoe.view.component.player

import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.skyd.imomoe.database.entity.PlayRecordEntity
import com.skyd.imomoe.database.getAppDataBase

object AnimeVideoPositionMemoryStore : AnimeVideoPlayer.PlayPositionMemoryDataStore {

    private val dao = getAppDataBase().playRecordDao()

    override suspend fun getPlayPosition(url: String): Long? = dao.query(url)?.position

    override suspend fun putPlayPosition(url: String, position: Long) {
        dao.insert(PlayRecordEntity(url, position))
    }

    override fun positionFormat(position: Long): String = CommonUtil.stringForTime(position.toInt())
}