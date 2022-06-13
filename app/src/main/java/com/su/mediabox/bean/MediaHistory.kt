package com.su.mediabox.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.su.mediabox.config.Const
import java.io.Serializable

@Entity(tableName = Const.Database.AppDataBase.HISTORY_MEDIA_TABLE_NAME)
open class MediaHistory(
    @PrimaryKey
    @ColumnInfo(name = "mediaUrl")
    var mediaUrl: String,
    @ColumnInfo(name = "mediaTitle")
    var mediaTitle: String,
    @ColumnInfo(name = "lastViewTime")
    var lastViewTime: Long,
    @ColumnInfo(name = "cover")
    var cover: String,
    @ColumnInfo(name = "lastEpisodeUrl")
    var lastEpisodeUrl: String? = null,
    @ColumnInfo(name = "lastEpisode")
    var lastEpisodeTitle: String? = null
) : Serializable