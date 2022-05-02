package com.su.mediabox.bean

import androidx.room.Entity
import com.su.mediabox.config.Const

@Entity(tableName = Const.Database.AppDataBase.FAVORITE_MEDIA_TABLE_NAME)
class MediaFavorite(
    mediaUrl: String,
    mediaTitle: String,
    lastViewTime: Long,
    cover: String,
    lastEpisodeUrl: String? = null,
    lastEpisodeTitle: String? = null
) : MediaHistory(mediaUrl, mediaTitle, lastViewTime, cover, lastEpisodeUrl, lastEpisodeTitle)