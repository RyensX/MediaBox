package com.su.mediabox.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.config.Const.Database.AppDataBase.FAVORITE_MEDIA_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaFavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favorite: MediaFavorite)

    //按照时间戳顺序，从大到小。最后搜索的元组在最上方（下标0）显示
    @Query(value = "SELECT * FROM $FAVORITE_MEDIA_TABLE_NAME ORDER BY lastViewTime DESC")
    suspend fun getFavoriteList(): List<MediaFavorite>

    //按照时间戳顺序，从大到小。最后搜索的元组在最上方（下标0）显示
    @Query(value = "SELECT * FROM $FAVORITE_MEDIA_TABLE_NAME ORDER BY lastViewTime DESC")
    fun getFavoriteListLiveData(): LiveData<List<MediaFavorite>>

    //按照时间戳顺序，从大到小。最后搜索的元组在最上方（下标0）显示
    @Query(value = "SELECT * FROM $FAVORITE_MEDIA_TABLE_NAME ORDER BY lastViewTime DESC")
    fun getFavoriteListFlow(): Flow<List<MediaFavorite>>

    @Query(value = "SELECT * FROM $FAVORITE_MEDIA_TABLE_NAME WHERE mediaUrl = :mediaUrl")
    suspend fun getFavorite(mediaUrl: String): MediaFavorite?

    @Query(value = "SELECT * FROM $FAVORITE_MEDIA_TABLE_NAME WHERE mediaUrl = :mediaUrl")
    fun getFavoriteLiveData(mediaUrl: String): LiveData<MediaFavorite?>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFavorite(favorite: MediaFavorite)

    @Query(value = "DELETE FROM $FAVORITE_MEDIA_TABLE_NAME WHERE mediaUrl = :mediaUrl")
    fun deleteFavorite(mediaUrl: String)
}