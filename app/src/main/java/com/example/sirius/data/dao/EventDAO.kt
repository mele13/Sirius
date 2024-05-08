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
}
