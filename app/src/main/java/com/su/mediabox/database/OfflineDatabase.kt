package com.su.mediabox.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.su.mediabox.App
import com.su.mediabox.config.Const.Database.OfflineDataBase.OFFLINE_DATA_BASE_FILE_NAME
import com.su.mediabox.database.dao.PlayRecordDao
import com.su.mediabox.database.entity.PlayRecordEntity

// 本地数据库，不参与WebDAV备份
@Database(entities = [PlayRecordEntity::class], version = 1)
abstract class OfflineDatabase : RoomDatabase() {

    abstract fun playRecordDao(): PlayRecordDao

    companion object {
        @Volatile
        private var INSTANCE: OfflineDatabase? = null

        fun getInstance(context: Context): OfflineDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                OfflineDatabase::class.java,
                OFFLINE_DATA_BASE_FILE_NAME
            ).build()
    }
}

fun getOfflineDatabase() = OfflineDatabase.getInstance(App.context)