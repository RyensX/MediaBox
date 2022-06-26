package com.su.mediabox.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.bean.MediaHistory
import com.su.mediabox.config.Const
import com.su.mediabox.config.Const.Database.AppDataBase.HISTORY_MEDIA_TABLE_NAME

@Dao
interface MediaHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(historyBean: MediaHistory)

    //按照时间戳顺序，从大到小。最后搜索的元组在最上方（下标0）显示
    @Query(value = "SELECT * FROM $HISTORY_MEDIA_TABLE_NAME ORDER BY lastViewTime DESC")
    suspend fun getHistoryList(): List<MediaHistory>

    @Query(value = "SELECT * FROM $HISTORY_MEDIA_TABLE_NAME WHERE mediaUrl = :mediaUrl")
    suspend fun getHistory(mediaUrl: String): MediaHistory?

    //按照时间戳顺序，从大到小。最后搜索的元组在最上方（下标0）显示
    @Query(value = "SELECT * FROM $HISTORY_MEDIA_TABLE_NAME ORDER BY lastViewTime DESC")
    fun getHistoryListLiveData(): LiveData<List<MediaHistory>>

    @Query("SELECT * FROM $HISTORY_MEDIA_TABLE_NAME WHERE mediaUrl = :mediaUrl")
    fun getHistoryLiveData(mediaUrl: String): LiveData<MediaHistory?>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateHistory(historyBean: MediaHistory)

    @Query(value = "DELETE FROM $HISTORY_MEDIA_TABLE_NAME WHERE mediaUrl = :mediaUrl")
    fun deleteHistory(mediaUrl: String)

    @Query(value = "DELETE FROM $HISTORY_MEDIA_TABLE_NAME")
    fun deleteAllHistory()

    // 获取记录条数
    @Query(value = "SELECT COUNT(1) FROM $HISTORY_MEDIA_TABLE_NAME")
    suspend fun getHistoryCount(): Long
}