package com.example.sirius.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "News")
data class News (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Int,
        @NonNull
        @ColumnInfo(name = "title")
        val titleNews: String,
        @NonNull
        @ColumnInfo(name = "short_info")
        val shortInfoNews: String,
        @NonNull
        @ColumnInfo(name = "long_info")
        val longInfoNews: String,
        @NonNull
        @ColumnInfo(name = "published_date")
        val publishedDate: String,
        @NonNull
        @ColumnInfo(name = "created_at")
        val createdAt: String,
        @ColumnInfo(name = "until_date")
        val untilDate: String?,
        @ColumnInfo(name = "photo_news")
        val photoNews: String,
        @NonNull
        @ColumnInfo(name = "good_news")
        val goodNews: Int, // 0 -> not good news | 1 -> it is good news
)
