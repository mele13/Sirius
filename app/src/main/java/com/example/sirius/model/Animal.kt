package com.example.sirius.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Animal")
data class Animal (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @NonNull
    @ColumnInfo(name = "name")
    var nameAnimal: String,
    @NonNull
    @ColumnInfo(name = "birth_date")
    val birthDate: String,
    @NonNull
    @ColumnInfo(name = "sex")
    val sexAnimal: String,
    @NonNull
    @ColumnInfo(name = "waiting_adoption")
    val waitingAdoption: Int, // 0 -> preAdoption | 1 -> adoption
    @NonNull
    @ColumnInfo(name = "foster_care")
    val fosterCare: Int, // 0 -> no foster care | 1 -> in foster care
    @NonNull
    @ColumnInfo(name = "short_info")
    var shortInfoAnimal: String,
    @NonNull
    @ColumnInfo(name = "long_info")
    var longInfoAnimal: String,
    @NonNull
    @ColumnInfo(name = "breed")
    val breedAnimal: String,
    @NonNull
    @ColumnInfo(name = "type_animal")
    // Enumerado
    val typeAnimal: TypeAnimal,
    @NonNull
    @ColumnInfo(name = "entry_date")
    val entryDate: String,
    @NonNull
    @ColumnInfo(name = "photo_animal")
    var photoAnimal: String,
    @NonNull
    @ColumnInfo(name = "in_shelter")
    var inShelter: Int,
    @NonNull
    @ColumnInfo(name = "lost")
    var lost: Int,
)