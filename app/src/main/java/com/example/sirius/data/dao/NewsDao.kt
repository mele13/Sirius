package com.example.sirius.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
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

    @Query("SELECT * FROM News WHERE good_news = 1 ORDER BY published_date DESC")
    fun getGoodNews(): Flow<List<News>>

    @Query("SELECT * FROM News WHERE good_news = 0 ORDER BY published_date DESC")
    fun getWhatNews(): Flow<List<News>>

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

    @Query("UPDATE News SET title = :title  WHERE id = :newsId")
    suspend fun updateTitleNews(newsId: Int, title : String)

    @Query("UPDATE News SET short_info = :shortInfo  WHERE id = :newsId")
    suspend fun updateShortInfoNews(newsId: Int, shortInfo : String)

    @Query("UPDATE News SET long_info = :longtInfo  WHERE id = :newsId")
    suspend fun updateLongInfoNews(newsId: Int, longtInfo : String)

    @Query("UPDATE News SET photo_news = :photoNews  WHERE id = :newsId")
    suspend fun updatePhotoNews(newsId: Int, photoNews : String)
}
