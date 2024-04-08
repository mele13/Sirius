package com.example.sirius.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Chat")
data class Chat (
    @NonNull
    @ColumnInfo(name = "chat_id")
    val chatId: String,
    @NonNull
    @ColumnInfo(name = "message")
    val message: String,
    @NonNull
    @ColumnInfo(name = "seen")
    val seen: Int,
    @NonNull
    @ColumnInfo(name = "sent_by")
    val sentBy: Int,
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sent_on")
    val sentOn: String // Cambiar el tipo a Long
)
