package com.su.mediabox.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.su.mediabox.App
import com.su.mediabox.bean.*
import com.su.mediabox.config.Const
import com.su.mediabox.database.dao.*
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.util.getOrInit

@Database(
    version = 2,
    entities = [
        MediaSearchHistory::class,
        MediaFavorite::class,
        MediaHistory::class
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchDao(): MediaSearchHistoryDao
    abstract fun favoriteDao(): MediaFavoriteDao
    abstract fun historyDao(): MediaHistoryDao

    companion object {

        private val instances by lazy(LazyThreadSafetyMode.NONE) { mutableMapOf<String, AppDatabase>() }

        fun getInstance(context: Context, dbFile: String): AppDatabase {
            return instances.getOrInit(dbFile) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    dbFile
                ).build()
            }
        }

        fun destroyInstance(dbFile: String) {
            instances.remove(dbFile)
        }
    }

}

fun PluginInfo.getAppDataBaseFileName() =
    String.format(Const.Database.AppDataBase.MEDIA_DB_FILE_NAME_TEMPLATE, id)

fun getAppDataBase() = PluginManager.currentLaunchPlugin.value?.run { getAppDataBase() }
    ?: throw RuntimeException("获取当前插件信息错误！")

fun PluginInfo.getAppDataBase() = AppDatabase.getInstance(App.context, getAppDataBaseFileName())

fun PluginInfo.destroyInstance() = AppDatabase.destroyInstance(getAppDataBaseFileName())