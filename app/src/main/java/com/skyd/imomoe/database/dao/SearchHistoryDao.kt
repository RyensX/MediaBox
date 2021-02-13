package com.skyd.imomoe.database.dao

import androidx.room.*
import com.skyd.imomoe.bean.SearchHistoryBean

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchHistory(searchHistoryBean: SearchHistoryBean)

    //按照时间戳顺序，从大到小。最后搜索的元组在最上方（下标0）显示
    @Query(value = "SELECT * FROM searchHistoryList ORDER BY id DESC")
    fun getSearchHistoryList(): List<SearchHistoryBean>

    @Update
    fun updateSearchHistory(searchHistoryBean: SearchHistoryBean)

    @Query(value = "DELETE FROM searchHistoryList WHERE title = :title")
    fun deleteSearchHistory(title: String)

    @Query(value = "DELETE FROM searchHistoryList WHERE id = :timeStamp")
    fun deleteSearchHistory(timeStamp: Long)
}