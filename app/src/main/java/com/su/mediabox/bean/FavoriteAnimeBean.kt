package com.su.mediabox.bean

import androidx.room.Entity
import com.su.mediabox.config.Const

@Entity(tableName = Const.Database.AppDataBase.FAVORITE_ANIME_TABLE_NAME)
class FavoriteAnimeBean(      //下面的url都是partUrl
    override var type: String,
    override var actionUrl: String,
    animeUrl: String,
    animeTitle: String,
    time: Long,                 //收藏日期
    cover: String,           //封面
    lastEpisodeUrl: String? = null,        //上次看到哪一集
    lastEpisode: String? = null
) : HistoryBean(type, actionUrl, animeUrl, animeTitle, time, cover, lastEpisodeUrl, lastEpisode)
