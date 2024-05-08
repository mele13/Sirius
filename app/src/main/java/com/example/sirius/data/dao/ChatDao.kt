package com.example.sirius.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sirius.model.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM Chat WHERE chat_id = :chatId ORDER BY sent_on")
    fun loadMessages(chatId: String): Flow<List<Chat>>

    @Insert
    suspend fun insertMessage(message: Chat)

    @Query("SELECT message FROM Chat WHERE chat_id = :chatId ORDER BY sent_on DESC LIMIT 1")
    suspend fun getLastMessage(chatId: String) : String

    @Query("SELECT sent_by FROM Chat WHERE seen = 0")
    fun getUnseenMessages(): Flow<List<Int>>

    @Query("SELECT chat_id FROM Chat WHERE seen = 0")
    fun getChatId(): Flow<List<String>>

    @Query("UPDATE Chat SET seen = 1 WHERE chat_id = :chatId")
    fun markMessagesAsSeen(chatId: String)
}