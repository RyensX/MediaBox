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
import com.su.mediabox.bean.PluginInfo
import com.su.mediabox.config.Const
import com.su.mediabox.config.Const.Database.AppDataBase.APP_DATA_BASE_FILE_NAME
import com.su.mediabox.database.dao.*
import com.su.mediabox.plugin.PluginManager

@Database(
    entities = [
        SearchHistoryBean::class,
        AnimeDownloadEntity::class,
        FavoriteAnimeBean::class,
        HistoryBean::class],
    version = 4
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
                    .addMigrations(version3to4)
                    .build()
            }.also { instances[name] = it }
        }
    }

}

fun getAppDataBase() = PluginManager.currentLaunchPlugin.value?.run { getAppDataBase() }
    ?: throw RuntimeException("获取当前插件信息错误！")

fun PluginInfo.getAppDataBase() = AppDatabase.getInstance(App.context, packageName, signature)

private val version3to4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val table = Const.Database.AppDataBase.SEARCH_HISTORY_TABLE_NAME
        //删除旧的搜索历史表
        database.execSQL("DROP TABLE $table")
        //建立新表
        database.execSQL("CREATE TABLE IF NOT EXISTS $table (`title` TEXT NOT NULL, `timeStamp` INTEGER NOT NULL, PRIMARY KEY(`title`))")
    }

}