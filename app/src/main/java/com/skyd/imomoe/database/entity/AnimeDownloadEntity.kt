package com.skyd.imomoe.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skyd.imomoe.config.Const
import java.io.Serializable

@Entity(tableName = Const.Database.AppDataBase.ANIME_DOWNLOAD_TABLE_NAME)
class AnimeDownloadEntity(
    @PrimaryKey
    @ColumnInfo(name = "md5")
    var md5: String,        //md5
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "fileName")
    var fileName: String?
) : Serializable {
    override fun equals(other: Any?): Boolean {
        return this.md5 == (other as AnimeDownloadEntity).md5
    }
}
