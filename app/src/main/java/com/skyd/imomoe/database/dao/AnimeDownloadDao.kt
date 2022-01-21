package com.skyd.imomoe.database.dao

import androidx.room.*
import com.skyd.imomoe.config.Const.Database.AppDataBase.ANIME_DOWNLOAD_TABLE_NAME
import com.skyd.imomoe.database.entity.AnimeDownloadEntity

@Dao
interface AnimeDownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnimeDownload(animeDownloadEntity: AnimeDownloadEntity)

    @Query(value = "SELECT * FROM $ANIME_DOWNLOAD_TABLE_NAME")
    fun getAnimeDownloadList(): List<AnimeDownloadEntity>

    @Query(value = "SELECT * FROM $ANIME_DOWNLOAD_TABLE_NAME WHERE md5 = :md5")
    fun getAnimeDownload(md5: String): AnimeDownloadEntity?

    // 获取md5列所有内容
    @Query(value = "SELECT md5 FROM $ANIME_DOWNLOAD_TABLE_NAME")
    fun getAnimeDownloadMd5List(): MutableList<String>

    // 通过md5获得title
    @Query(value = "SELECT title FROM $ANIME_DOWNLOAD_TABLE_NAME WHERE md5 = :md5")
    fun getAnimeDownloadTitleByMd5(md5: String): String?

    @Query(value = "DELETE FROM $ANIME_DOWNLOAD_TABLE_NAME")
    fun deleteAllAnimeDownload()

    @Query(value = "DELETE FROM $ANIME_DOWNLOAD_TABLE_NAME WHERE md5 = :md5")
    fun deleteAnimeDownload(md5: String)

    @Query(value = "UPDATE $ANIME_DOWNLOAD_TABLE_NAME SET fileName = :fileName WHERE md5 = :md5")
    fun updateFileNameByMd5(md5: String, fileName: String)
}