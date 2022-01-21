package com.skyd.imomoe.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skyd.imomoe.config.Const

@Entity(tableName = Const.Database.OfflineDataBase.PLAY_RECORD_TABLE_NAME)
data class PlayRecordEntity(
    @PrimaryKey
    @ColumnInfo(name = "url")
    var url: String,
    // 播放进度，单位ms
    @ColumnInfo(name = "position")
    var position: Long
)