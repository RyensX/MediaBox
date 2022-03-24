package com.su.mediabox.view.component.player

import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.database.entity.PlayRecordEntity
import com.su.mediabox.database.getOfflineDatabase

object VideoPositionMemoryDbStore : AnimeVideoPlayer.PlayPositionMemoryDataStore {

    private val dao = getOfflineDatabase().playRecordDao()

    override suspend fun getPlayPosition(url: String): Long? = dao.query(url)?.position

    override suspend fun putPlayPosition(url: String, position: Long) {
        dao.insert(PlayRecordEntity(url, position))
    }

    override suspend fun deletePlayPosition(url: String) = dao.delete(url)

    override fun positionFormat(position: Long): String =
        if (position < 0L) App.context.getString(R.string.episode_play_completed)
        else CommonUtil.stringForTime(position.toInt())
}