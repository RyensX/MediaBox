package com.su.mediabox.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.su.mediabox.config.Const

@Entity(tableName = Const.Database.OfflineDataBase.UPDATE_RECORD_TABLE_NAME)
data class MediaUpdateRecord(
    @PrimaryKey
    val time: Long,
    val targetMedia: String,
    val targetMediaLabel: String,
    val oldTag: String?,
    val newTag: String,
    var confirmed: Boolean = false
)