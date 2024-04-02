package com.example.sirius.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LikedAnimal")
data class LikedAnimal (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @NonNull
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @NonNull
    @ColumnInfo(name = "animal_id")
    val animalId: Int,
) {
    constructor(userId: Int, animalId: Int)
            : this(0, userId, animalId)
}