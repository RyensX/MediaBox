package com.su.mediabox.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.su.mediabox.App
import com.su.mediabox.config.Const
import com.su.mediabox.database.dao.MediaUpdateRecordDao
import com.su.mediabox.database.dao.PlayRecordDao
import com.su.mediabox.database.entity.MediaUpdateRecord
import com.su.mediabox.database.entity.PlayRecordEntity
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.plugin.PluginManager

// 本地数据库，不参与WebDAV备份
@Database(entities = [PlayRecordEntity::class, MediaUpdateRecord::class], version = 1)
abstract class OfflineDatabase : RoomDatabase() {

    abstract fun playRecordDao(): PlayRecordDao

    abstract fun mediaUpdateDao(): MediaUpdateRecordDao

    companion object {
        @Volatile
        private var INSTANCE: OfflineDatabase? = null

        fun getInstance(
            context: Context,
            dbName: String
        ): OfflineDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context,
                    OfflineDatabase::class.java,
                    dbName
                )
                    .build().also { INSTANCE = it }
            }
    }
}

fun PluginInfo.getOfflineDatabaseFileName() =
    String.format(Const.Database.AppDataBase.MEDIA_OFFLINE_DB_FILE_NAME_TEMPLATE, id)

fun getOfflineDatabase() =
    PluginManager.currentLaunchPlugin.value?.run { getOfflineDatabase() }
        ?: throw RuntimeException("获取当前插件信息错误！")

fun PluginInfo.getOfflineDatabase() =
    OfflineDatabase.getInstance(App.context, getOfflineDatabaseFileName())