package com.skyd.imomoe.database.dao

import androidx.room.*
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.bean.ImageBean

@Dao
interface FavoriteAnimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteAnime(favoriteAnimeBean: FavoriteAnimeBean)

    //按照时间戳顺序，从大到小。最后搜索的元组在最上方（下标0）显示
    @Query(value = "SELECT * FROM favoriteAnimeList ORDER BY time DESC")
    fun getFavoriteAnimeList(): MutableList<FavoriteAnimeBean>

    @Query(value = "SELECT * FROM favoriteAnimeList WHERE animeUrl = :animeUrl")
    fun getFavoriteAnime(animeUrl: String): FavoriteAnimeBean?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFavoriteAnime(favoriteAnimeBean: FavoriteAnimeBean)

    @Query(value = "UPDATE favoriteAnimeList SET cover = :cover WHERE animeUrl = :animeUrl")
    fun updateFavoriteAnimeCover(animeUrl: String, cover: ImageBean)

    @Query(value = "UPDATE favoriteAnimeList SET animeTitle = :animeTitle WHERE animeUrl = :animeUrl")
    fun updateFavoriteAnimeTitle(animeUrl: String, animeTitle: String)

    @Query(value = "DELETE FROM favoriteAnimeList WHERE animeUrl = :animeUrl")
    fun deleteFavoriteAnime(animeUrl: String)
}