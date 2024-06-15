package com.su.mediabox.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.su.mediabox.App
import com.su.mediabox.config.Const
import com.su.mediabox.database.dao.MediaUpdateRecordDao
import com.su.mediabox.database.dao.PlayRecordDao
import com.su.mediabox.database.dao.SkipPosRecordDao
import com.su.mediabox.database.entity.MediaUpdateRecord
import com.su.mediabox.database.entity.PlayRecordEntity
import com.su.mediabox.database.entity.SkipPosEntity
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.util.getOrInit
import com.su.mediabox.util.logD

// 本地数据库，不参与WebDAV备份
@Database(
    entities = [PlayRecordEntity::class, MediaUpdateRecord::class, SkipPosEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class OfflineDatabase : RoomDatabase() {

    abstract fun playRecordDao(): PlayRecordDao

    abstract fun mediaUpdateDao(): MediaUpdateRecordDao

    abstract fun skipPosRecordDao(): SkipPosRecordDao

    companion object {

        private val instances by lazy(LazyThreadSafetyMode.NONE) { mutableMapOf<String, OfflineDatabase>() }

        @Synchronized
        fun getInstance(context: Context, dbFile: String): OfflineDatabase {
            return instances.getOrInit(dbFile) {
                Room.databaseBuilder(
                    context.applicationContext,
                    OfflineDatabase::class.java,
                    dbFile
                )
                    .build()
            }
        }

        @Synchronized
        fun destroyInstance(dbFile: String) {
            instances.remove(dbFile)
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