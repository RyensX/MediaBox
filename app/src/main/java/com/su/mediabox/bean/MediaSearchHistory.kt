package com.su.mediabox.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.su.mediabox.config.Const

@Entity(tableName = Const.Database.AppDataBase.SEARCH_MEDIA_TABLE_NAME)
class MediaSearchHistory(
    @PrimaryKey
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "timeStamp")
    var timeStamp: Long
)