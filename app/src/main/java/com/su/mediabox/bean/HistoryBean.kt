package com.su.mediabox.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.su.mediabox.config.Const
import com.su.mediabox.pluginapi.been.BaseBean
import java.io.Serializable

@Entity(tableName = Const.Database.AppDataBase.HISTORY_TABLE_NAME)
class HistoryBean(      //下面的url都是partUrl
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
    //FIX_TODO 2022/2/18 1:35 0 从历史记录打开播放无法得到封面信息
    //FIX_TODO 2022/2/18 1:36 0 历史记录无法路由到详情页
    var cover: String,           // 封面
    @ColumnInfo(name = "lastEpisodeUrl")
    var lastEpisodeUrl: String? = null,        //上次看到哪一集
    @ColumnInfo(name = "lastEpisode")
    var lastEpisode: String? = null
) : BaseBean, Serializable
