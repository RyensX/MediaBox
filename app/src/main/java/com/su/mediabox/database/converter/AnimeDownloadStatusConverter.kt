package com.su.mediabox.database.converter

import androidx.room.TypeConverter
import com.su.mediabox.util.downloadanime.AnimeDownloadStatus

class AnimeDownloadStatusConverter {

    @TypeConverter
    fun intToEnum(status: Int?): AnimeDownloadStatus? = AnimeDownloadStatus.values()[status ?: 0]

    @TypeConverter
    fun enumToInt(animeDownloadStatus: AnimeDownloadStatus?): Int? = animeDownloadStatus?.ordinal

}