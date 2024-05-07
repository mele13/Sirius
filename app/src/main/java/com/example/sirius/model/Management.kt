package com.example.sirius.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Management")
data class Management (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @NonNull
    @ColumnInfo(name = "shelter_id")
    var shelterId: Int,
    @NonNull
    @ColumnInfo(name = "value")
    var value: String,
    @NonNull
    @ColumnInfo(name = "description")
    val description: String,
    @NonNull
    @ColumnInfo(name = "date")
    val date: String,


    ) {
    constructor(shelterId: Int, value: String, description: String, date:String)
            : this(0, shelterId, value, description, date)
}