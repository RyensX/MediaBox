package com.su.mediabox.bean

import androidx.room.Entity
import com.su.mediabox.config.Const

/**
 * @param updateTag 媒体更新标记信息 //TODO 待实现媒体检查更新组件
 */
@Entity(tableName = Const.Database.AppDataBase.FAVORITE_MEDIA_TABLE_NAME)
class MediaFavorite(
    mediaUrl: String,
    mediaTitle: String,
    lastViewTime: Long,
    cover: String,
    lastEpisodeUrl: String? = null,
    lastEpisodeTitle: String? = null,
    var updateTag: String? = null
) : MediaHistory(mediaUrl, mediaTitle, lastViewTime, cover, lastEpisodeUrl, lastEpisodeTitle)