package com.example.sirius.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.lang.reflect.Constructor

@Entity(tableName = "user", indices = [Index(value = ["username"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @NonNull
    @ColumnInfo(name = "username")
    var username: String,
    @NonNull
    @ColumnInfo(name = "email")
    var email: String,
    @NonNull
    @ColumnInfo(name = "password")
    var password: String,
    @NonNull
    @ColumnInfo(name = "role")
    //Enumerado
    val role: String,
    @NonNull
    @ColumnInfo(name = "photo_user")
    var photoUser: String,
    // Favourites
) {
    constructor(username: String, email: String, password: String, role: String, photoUser: String)
            : this(0, username, email, password, role, photoUser)
}
