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
import com.skyd.imomoe.database.dao.AnimeDownloadDao
import com.skyd.imomoe.database.dao.SearchHistoryDao
import com.skyd.imomoe.database.entity.AnimeDownloadEntity

@Database(
    entities = [SearchHistoryBean::class,
        AnimeDownloadEntity::class], version = 2
)
@TypeConverters(AnimeDownloadStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun animeDownloadDao(): AnimeDownloadDao

    companion object {
        private var instance: AppDatabase? = null

        private val migration1To2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE animeDownloadList add fileName TEXT")
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
                        .addMigrations(migration1To2)
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