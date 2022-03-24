package com.su.mediabox.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.su.mediabox.config.Const
import com.su.mediabox.pluginapi.been.BaseBean
import java.io.Serializable

@Deprecated("不需要继承BaseBeen多那么多冗余字段，也不应该持久化视图类型")
@Entity(tableName = Const.Database.AppDataBase.HISTORY_TABLE_NAME)
open class HistoryBean(      //下面的url都是partUrl
    @ColumnInfo(name = "type")
    override var type: String,
    @ColumnInfo(name = "actionUrl")
    override var actionUrl: String,
    @PrimaryKey
    @ColumnInfo(name = "animeUrl")
    var animeUrl: String,
    @ColumnInfo(name = "animeTitle")
    var animeTitle: String,
    @ColumnInfo(name = "time")
    var time: Long,                 // 观看日期
    @ColumnInfo(name = "cover")
    var cover: String,           // 封面
    @ColumnInfo(name = "lastEpisodeUrl")
    var lastEpisodeUrl: String? = null,        //上次看到哪一集
    @ColumnInfo(name = "lastEpisode")
    var lastEpisode: String? = null
) : BaseBean, Serializable
