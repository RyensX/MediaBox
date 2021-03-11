package com.skyd.imomoe.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skyd.imomoe.App
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.database.converter.AnimeDownloadStatusConverter
import com.skyd.imomoe.database.converter.ImageBeanConverter
import com.skyd.imomoe.database.dao.AnimeDownloadDao
import com.skyd.imomoe.database.dao.FavoriteAnimeDao
import com.skyd.imomoe.database.dao.SearchHistoryDao
import com.skyd.imomoe.database.entity.AnimeDownloadEntity
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.bean.HistoryBean
import com.skyd.imomoe.database.dao.HistoryDao

@Database(
    entities = [SearchHistoryBean::class,
        AnimeDownloadEntity::class,
        FavoriteAnimeBean::class,
        HistoryBean::class], version = 3
)
@TypeConverters(
    value = [AnimeDownloadStatusConverter::class,
        ImageBeanConverter::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun animeDownloadDao(): AnimeDownloadDao
    abstract fun favoriteAnimeDao(): FavoriteAnimeDao
    abstract fun historyDao(): HistoryDao

    companion object {
        private var instance: AppDatabase? = null

        private val migration1To2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE animeDownloadList ADD fileName TEXT")
            }
        }

        private val migration2To3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE favoriteAnimeList(type TEXT NOT NULL, actionUrl TEXT NOT NULL, animeUrl TEXT PRIMARY KEY NOT NULL, animeTitle TEXT NOT NULL, time INTEGER NOT NULL, cover TEXT NOT NULL, lastEpisodeUrl TEXT, lastEpisode TEXT)")
                database.execSQL("CREATE TABLE historyList(type TEXT NOT NULL, actionUrl TEXT NOT NULL, animeUrl TEXT PRIMARY KEY NOT NULL, animeTitle TEXT NOT NULL, time INTEGER NOT NULL, cover TEXT NOT NULL, lastEpisodeUrl TEXT, lastEpisode TEXT)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                if (instance != null) return instance as AppDatabase
                return synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app.db"
                    )
                        .addMigrations(migration1To2, migration2To3)
                        .build()
                }
            } else {
                return instance as AppDatabase
            }

        }
    }

    interface DBCallback<T> {
        fun success(result: T)
        fun fail(throwable: Throwable)
    }
}

fun getAppDataBase() = AppDatabase.getInstance(App.context)