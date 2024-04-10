package com.example.sirius.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Shelters")
data class Shelter (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @NonNull
    @ColumnInfo(name = "name")
    var name: String,
    @NonNull
    @ColumnInfo(name = "about_us")
    var aboutUs: String,
    @NonNull
    @ColumnInfo(name = "location")
    val location: String,
    @NonNull
    @ColumnInfo(name = "schedule")
    val schedule: String,
    @NonNull
    @ColumnInfo(name = "shelters_data")
    val sheltersData: String,
    @NonNull
    @ColumnInfo(name = "email")
    val email: String,
    @NonNull
    @ColumnInfo(name = "phone")
    val phone: String,

) {
    constructor(name: String, aboutUs: String, location: String, schedule: String, sheltersData: String, email: String, phone: String)
            : this(0, name, aboutUs, location, schedule, sheltersData,email , phone)
}
