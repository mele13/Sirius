package com.example.sirius.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sirius.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDAO {
    @Insert
    suspend fun insertEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("SELECT * FROM Event ORDER BY date DESC")
    fun getEvent(): Flow<List<Event>>

    @Query("SELECT * FROM Event WHERE userId = :userID ORDER BY date DESC")
    fun getNewsByUserID(userID: Int): Flow<List<Event>>

    @Query("SELECT * FROM Event WHERE type = :typeEvent ORDER BY date DESC")
    fun getNewsById(typeEvent: String): Flow<List<Event>>
/*
    @Query("SELECT * FROM News WHERE good_news = 1 ORDER BY published_date DESC")
    fun getGoodNews(): Flow<List<News>>

    @Query("SELECT * FROM News WHERE good_news = 0 ORDER BY published_date DESC")
    fun getWhatNews(): Flow<List<News>>

    @Query("DELETE FROM Event")
    suspend fun deleteAllEvents()

    @Query("UPDATE News SET title = :title  WHERE id = :newsId")
    suspend fun updateTitleNews(newsId: Int, title : String)

    @Query("UPDATE News SET short_info = :shortInfo  WHERE id = :newsId")
    suspend fun updateShortInfoNews(newsId: Int, shortInfo : String)

    @Query("UPDATE News SET long_info = :longtInfo  WHERE id = :newsId")
    suspend fun updateLongInfoNews(newsId: Int, longtInfo : String)

    @Query("UPDATE News SET photo_news = :photoNews  WHERE id = :newsId")
    suspend fun updatePhotoNews(newsId: Int, photoNews : String)
    */

}
