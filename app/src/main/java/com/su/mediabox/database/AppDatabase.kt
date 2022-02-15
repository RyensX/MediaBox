package com.su.mediabox.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.su.mediabox.App
import com.su.mediabox.bean.SearchHistoryBean
import com.su.mediabox.database.converter.AnimeDownloadStatusConverter
import com.su.mediabox.database.entity.AnimeDownloadEntity
import com.su.mediabox.bean.FavoriteAnimeBean
import com.su.mediabox.bean.HistoryBean
import com.su.mediabox.config.Const.Database.AppDataBase.ANIME_DOWNLOAD_TABLE_NAME
import com.su.mediabox.config.Const.Database.AppDataBase.APP_DATA_BASE_FILE_NAME
import com.su.mediabox.config.Const.Database.AppDataBase.FAVORITE_ANIME_TABLE_NAME
import com.su.mediabox.config.Const.Database.AppDataBase.HISTORY_TABLE_NAME
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
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                if (instance != null) return instance as AppDatabase
                return synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        APP_DATA_BASE_FILE_NAME
                    )
                        //.addMigrations()
                        .build()
                }
            } else {
                return instance as AppDatabase
            }

        }
    }

}

fun getAppDataBase() = AppDatabase.getInstance(App.context)