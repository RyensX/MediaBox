package com.su.mediabox.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.su.mediabox.config.Const

@Entity(tableName = Const.Database.OfflineDataBase.SKIP_POS_RECORD_TABLE_NAME)
data class SkipPosEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    //所属作品
    @ColumnInfo(name = "video")
    var video: String,
    // 播放进度，单位ms
    @ColumnInfo(name = "position")
    var position: Long,
    @ColumnInfo(name = "duration")
    var duration: Long,
    @ColumnInfo(name = "desc")
    var desc: String,
    @ColumnInfo(name = "enable")
    var enable: Boolean
) {
    constructor(video: String, position: Long, duration: Long, desc: String, enable: Boolean) :
            this(-1, video, position, duration, desc, enable)
}