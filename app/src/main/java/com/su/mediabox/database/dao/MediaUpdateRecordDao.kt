package com.su.mediabox.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.su.mediabox.config.Const.Database.OfflineDataBase.UPDATE_RECORD_TABLE_NAME
import com.su.mediabox.database.entity.MediaUpdateRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaUpdateRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg record: MediaUpdateRecord?)

    @Query("SELECT * FROM $UPDATE_RECORD_TABLE_NAME ORDER BY time DESC")
    fun getAllUpdateRecordFlow(): Flow<List<MediaUpdateRecord>>

    @Query("UPDATE $UPDATE_RECORD_TABLE_NAME SET confirmed = 1 WHERE time = :time")
    suspend fun confirmed(time: Long)

    @Query("UPDATE $UPDATE_RECORD_TABLE_NAME SET confirmed = 1")
    suspend fun confirmedAll()

    @Query("DELETE FROM $UPDATE_RECORD_TABLE_NAME WHERE time = :time")
    suspend fun delete(time: Long)

    @Query("DELETE FROM $UPDATE_RECORD_TABLE_NAME")
    suspend fun deleteAll()

    @Query("SELECT COUNT(1) FROM $UPDATE_RECORD_TABLE_NAME")
    suspend fun getMediaUpdateRecordCount(): Long

    @Query("SELECT COUNT(1) FROM $UPDATE_RECORD_TABLE_NAME WHERE confirmed = 0")
    fun getUnConfirmedMediaUpdateRecordCountLiveData(): LiveData<Long>

    //TODO 取消收藏番剧时删除相关更新记录
}