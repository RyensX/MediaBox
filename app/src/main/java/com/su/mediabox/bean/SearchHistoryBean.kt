package com.su.mediabox.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.su.mediabox.config.Const
import com.su.mediabox.pluginapi.been.BaseBean

@Entity(tableName = Const.Database.AppDataBase.SEARCH_HISTORY_TABLE_NAME)
class SearchHistoryBean(
    @ColumnInfo(name = "type")
    override var type: String,
    @ColumnInfo(name = "actionUrl")
    override var actionUrl: String,
    @PrimaryKey
    @ColumnInfo(name = "id")
    var timeStamp: Long,        //时间戳作为主键
    @ColumnInfo(name = "title")
    var title: String
) : BaseBean {
    override fun equals(other: Any?): Boolean {
        if (other is SearchHistoryBean) {
            return other.title == title
        }
        return false
    }
}
