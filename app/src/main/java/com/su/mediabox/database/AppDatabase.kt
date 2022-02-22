package com.su.mediabox.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.su.mediabox.App
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.plugin.PluginManager.getPluginInfo
import com.su.mediabox.bean.SearchHistoryBean
import com.su.mediabox.database.converter.AnimeDownloadStatusConverter
import com.su.mediabox.database.entity.AnimeDownloadEntity
import com.su.mediabox.bean.FavoriteAnimeBean
import com.su.mediabox.bean.HistoryBean
import com.su.mediabox.config.Const.Database.AppDataBase.APP_DATA_BASE_FILE_NAME
import com.su.mediabox.database.dao.*

@Database(
    entities = [SearchHistoryBean::class,
        AnimeDownloadEntity::class,
        FavoriteAnimeBean::class,
        HistoryBean::class], version = 3
)
@TypeConverters(
    value = [AnimeDownloadStatusConverter::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun animeDownloadDao(): AnimeDownloadDao
    abstract fun favoriteAnimeDao(): FavoriteAnimeDao
    abstract fun historyDao(): HistoryDao

    companion object {

        private val instances by lazy(LazyThreadSafetyMode.NONE) { mutableMapOf<String, AppDatabase>() }

        fun getInstance(context: Context, packageName: String, signature: String): AppDatabase {
            val name = String.format(APP_DATA_BASE_FILE_NAME, packageName, signature)
            return instances[name] ?: synchronized(this) {
                instances[name] ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    name
                )
                    //.addMigrations()
                    .build()
            }.also { instances[name] = it }
        }
    }

}

fun getAppDataBase() = AppRouteProcessor.currentActivity?.get()?.getPluginInfo()
    ?.let { AppDatabase.getInstance(App.context, it.packageName, it.signature) }
    ?: throw RuntimeException("获取当前插件信息错误！")