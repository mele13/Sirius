package com.example.sirius.data.dao

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.example.sirius.model.News
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Insert
    suspend fun insertNews(news: News)

    @Update
    suspend fun updateNews(news: News)

    @Delete
    suspend fun deleteNews(news: News)

    @Query("SELECT * FROM News ORDER BY published_date DESC")
    fun getNews(): Flow<List<News>>

    @Query("SELECT * FROM News WHERE title = :title ORDER BY published_date DESC")
    fun getNewsByTitle(title: String): Flow<List<News>>

    @Query("SELECT * FROM News WHERE id = :newsId")
    suspend fun getNewsById(newsId: Int): News?

//    @Query("SELECT * FROM News WHERE published_date > :startDateMillis")
//    suspend fun getNewsPublishedAfter(startDateMillis: Long): List<News>
//
//    @Query("SELECT * FROM News WHERE published_date <= :endDateMillis ORDER BY published_date DESC")
//    suspend fun getNewsPublishedBefore(endDateMillis: Long): List<News>
//
//    @Query("SELECT * FROM News WHERE published_date BETWEEN :startDateMillis AND :endDateMillis ORDER BY published_date DESC")
//    fun getNewsBetweenDates(startDateMillis: Long, endDateMillis: Long): Flow<List<News>>

    @Query("DELETE FROM News")
    suspend fun deleteAllNews()
}
