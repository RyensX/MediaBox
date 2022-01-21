package com.skyd.imomoe.database.dao

import androidx.room.*
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.config.Const.Database.AppDataBase.SEARCH_HISTORY_TABLE_NAME

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchHistory(searchHistoryBean: SearchHistoryBean)

    //按照时间戳顺序，从大到小。最后搜索的元组在最上方（下标0）显示
    @Query(value = "SELECT * FROM $SEARCH_HISTORY_TABLE_NAME ORDER BY id DESC")
    fun getSearchHistoryList(): List<SearchHistoryBean>

    @Update
    fun updateSearchHistory(searchHistoryBean: SearchHistoryBean)

    @Query(value = "DELETE FROM $SEARCH_HISTORY_TABLE_NAME WHERE title = :title")
    fun deleteSearchHistory(title: String)

    @Query(value = "DELETE FROM $SEARCH_HISTORY_TABLE_NAME WHERE id = :timeStamp")
    fun deleteSearchHistory(timeStamp: Long)

    @Query(value = "DELETE FROM $SEARCH_HISTORY_TABLE_NAME")
    fun deleteAllSearchHistory()
}