package com.skyd.imomoe.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.skyd.imomoe.App
import com.skyd.imomoe.config.Constant
import com.skyd.imomoe.database.dao.PlayRecordDao
import com.skyd.imomoe.database.entity.PlayRecordEntity

@Database(entities = [PlayRecordEntity::class], version = 1)
abstract class OfflineDatabase : RoomDatabase() {

    abstract fun playPlayRecordDao(): PlayRecordDao

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
                Constant.Database.OfflineData.DB_FILE_NAME
            ).build()
    }
}

fun getOfflineDatabase() = OfflineDatabase.getInstance(App.context)