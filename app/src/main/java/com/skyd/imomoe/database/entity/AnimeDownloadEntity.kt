package com.skyd.imomoe.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "animeDownloadList")
class AnimeDownloadEntity(
    @PrimaryKey
    @ColumnInfo(name = "md5")
    var md5: String,        //md5
    @ColumnInfo(name = "title")
    var title: String
//    var status: AnimeDownloadStatus
) : Serializable {
    override fun equals(other: Any?): Boolean {
        return this.md5 == (other as AnimeDownloadEntity).md5
    }
}
