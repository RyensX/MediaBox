package com.skyd.imomoe.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searchHistoryList")
class SearchHistoryBean(
    override var type: String,
    override var actionUrl: String,
    @PrimaryKey
    @ColumnInfo(name = "id")
    var timeStamp: Long,        //时间戳作为主键
    var title: String
) : BaseBean {
    override fun equals(other: Any?): Boolean {
        if (other is SearchHistoryBean) {
            return other.title == title
        }
        return false
    }
}
