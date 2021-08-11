package com.skyd.imomoe.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searchHistoryList")
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
