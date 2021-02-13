package com.skyd.imomoe.database.converter

import androidx.room.TypeConverter
import com.skyd.imomoe.util.downloadanime.AnimeDownloadStatus

class AnimeDownloadStatusConverter {

    @TypeConverter
    fun intToEnum(status: Int?): AnimeDownloadStatus? = AnimeDownloadStatus.values()[status ?: 0]

    @TypeConverter
    fun enumToInt(animeDownloadStatus: AnimeDownloadStatus?): Int? = animeDownloadStatus?.ordinal

}