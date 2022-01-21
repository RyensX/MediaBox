package com.skyd.imomoe.database.dao

import androidx.room.*
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.bean.ImageBean
import com.skyd.imomoe.config.Const.Database.AppDataBase.FAVORITE_ANIME_TABLE_NAME

@Dao
interface FavoriteAnimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteAnime(favoriteAnimeBean: FavoriteAnimeBean)

    //按照时间戳顺序，从大到小。最后搜索的元组在最上方（下标0）显示
    @Query(value = "SELECT * FROM $FAVORITE_ANIME_TABLE_NAME ORDER BY time DESC")
    fun getFavoriteAnimeList(): MutableList<FavoriteAnimeBean>

    @Query(value = "SELECT * FROM $FAVORITE_ANIME_TABLE_NAME WHERE animeUrl = :animeUrl")
    fun getFavoriteAnime(animeUrl: String): FavoriteAnimeBean?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFavoriteAnime(favoriteAnimeBean: FavoriteAnimeBean)

    @Query(value = "UPDATE $FAVORITE_ANIME_TABLE_NAME SET cover = :cover WHERE animeUrl = :animeUrl")
    fun updateFavoriteAnimeCover(animeUrl: String, cover: ImageBean)

    @Query(value = "UPDATE $FAVORITE_ANIME_TABLE_NAME SET animeTitle = :animeTitle WHERE animeUrl = :animeUrl")
    fun updateFavoriteAnimeTitle(animeUrl: String, animeTitle: String)

    @Query(value = "DELETE FROM $FAVORITE_ANIME_TABLE_NAME WHERE animeUrl = :animeUrl")
    fun deleteFavoriteAnime(animeUrl: String)
}