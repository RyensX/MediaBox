package com.su.mediabox.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.su.mediabox.config.Const.Database.OfflineDataBase.UPDATE_RECORD_TABLE_NAME
import com.su.mediabox.database.entity.MediaUpdateRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaUpdateRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg record: MediaUpdateRecord)

    @Query("SELECT * FROM $UPDATE_RECORD_TABLE_NAME")
    fun getAllUpdateRecordFlow(): Flow<List<MediaUpdateRecord>>

    @Query("DELETE FROM $UPDATE_RECORD_TABLE_NAME WHERE time = :time")
    fun delete(time: Long)

    @Query("DELETE FROM $UPDATE_RECORD_TABLE_NAME")
    fun deleteAll()

    // 获取记录条数
    @Query("SELECT COUNT(1) FROM $UPDATE_RECORD_TABLE_NAME")
    fun getMediaUpdateRecordCount(): Long
}