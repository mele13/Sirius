package com.example.sirius.data.dao

import android.view.textclassifier.ConversationActions.Message
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sirius.model.Animal
import com.example.sirius.model.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM Chat WHERE chat_id = :chatId ORDER BY sent_on")
    fun loadMessages(chatId: String): Flow<List<Chat>>

    @Insert
    suspend fun insertMessage(message: Chat)
}