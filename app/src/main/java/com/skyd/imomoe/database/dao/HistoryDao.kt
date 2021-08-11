package com.skyd.imomoe.database.dao

import androidx.room.*
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.bean.HistoryBean

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(historyBean: HistoryBean)

    //按照时间戳顺序，从大到小。最后搜索的元组在最上方（下标0）显示
    @Query(value = "SELECT * FROM historyList ORDER BY time DESC")
    fun getHistoryList(): MutableList<HistoryBean>

    @Query(value = "SELECT * FROM historyList WHERE animeUrl = :animeUrl")
    fun getHistory(animeUrl: String): FavoriteAnimeBean?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateHistory(favoriteAnimeBean: FavoriteAnimeBean)

    @Query(value = "UPDATE historyList SET animeTitle = :animeTitle WHERE animeUrl = :animeUrl")
    fun updateHistoryTitle(animeUrl: String, animeTitle: String)

    @Query(value = "DELETE FROM historyList WHERE animeUrl = :animeUrl")
    fun deleteHistory(animeUrl: String)

    @Query(value = "DELETE FROM historyList")
    fun deleteAllHistory()
}