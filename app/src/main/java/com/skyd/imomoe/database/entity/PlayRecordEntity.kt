package com.skyd.imomoe.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skyd.imomoe.config.Constant

@Entity(tableName = Constant.Database.OfflineData.PLAY_RECORD_TABLE_NAME)
data class PlayRecordEntity(
    @PrimaryKey
    var url: String,
    //播放进度，单位ms
    var position: Long
)