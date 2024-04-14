package com.su.mediabox.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.su.mediabox.config.Const.Database.OfflineDataBase.PLAY_RECORD_TABLE_NAME
import com.su.mediabox.config.Const.Database.OfflineDataBase.SKIP_POS_RECORD_TABLE_NAME
import com.su.mediabox.database.entity.PlayRecordEntity
import com.su.mediabox.database.entity.SkipPosEntity

@Dao
interface SkipPosRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg record: SkipPosEntity)

    @Query("INSERT INTO $SKIP_POS_RECORD_TABLE_NAME(video,position,duration,`desc`,enable) VALUES(:video,:position,:duration,:desc,:enable)")
    fun insertData(video: String, position: Long, duration: Long, desc: String, enable: Boolean)

    @Query("SELECT * FROM $SKIP_POS_RECORD_TABLE_NAME WHERE video = :video")
    fun queryList(video: String): LiveData<List<SkipPosEntity>?>

    @Query("DELETE FROM $SKIP_POS_RECORD_TABLE_NAME WHERE id = :id")
    fun delete(id: Int)

    @Query("UPDATE $SKIP_POS_RECORD_TABLE_NAME SET enable = :enable WHERE id = :id")
    fun enable(id: Int, enable: Boolean)

    @Query(value = "DELETE FROM $SKIP_POS_RECORD_TABLE_NAME")
    fun deleteAll()
}