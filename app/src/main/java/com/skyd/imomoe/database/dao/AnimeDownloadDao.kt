package com.skyd.imomoe.database.dao

import androidx.room.*
import com.skyd.imomoe.database.entity.AnimeDownloadEntity

@Dao
interface AnimeDownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnimeDownload(animeDownloadEntity: AnimeDownloadEntity)

    @Query(value = "SELECT * FROM animeDownloadList")
    fun getAnimeDownloadList(): List<AnimeDownloadEntity>

    @Query(value = "SELECT * FROM animeDownloadList WHERE md5 = :md5")
    fun getAnimeDownload(md5: String): AnimeDownloadEntity?

    @Query(value = "DELETE FROM animeDownloadList")
    fun deleteAllAnimeDownload()

    @Query(value = "DELETE FROM animeDownloadList WHERE md5 = :md5")
    fun deleteAnimeDownload(md5: String)
}