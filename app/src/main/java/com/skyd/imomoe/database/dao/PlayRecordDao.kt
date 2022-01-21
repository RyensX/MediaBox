package com.skyd.imomoe.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skyd.imomoe.config.Const.Database.OfflineDataBase.PLAY_RECORD_TABLE_NAME
import com.skyd.imomoe.database.entity.PlayRecordEntity

@Dao
interface PlayRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg record: PlayRecordEntity)

    @Query("SELECT * FROM $PLAY_RECORD_TABLE_NAME WHERE url=:url")
    suspend fun query(url: String): PlayRecordEntity?

    @Query("DELETE FROM $PLAY_RECORD_TABLE_NAME WHERE url = :url")
    fun delete(url: String)
}