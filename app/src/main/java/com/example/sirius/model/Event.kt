package com.example.sirius.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "Event")
data class Event (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @NonNull
    @ColumnInfo(name = "title")
    var titleEvent: String,
    @NonNull
    @ColumnInfo(name = "description")
    var descriptionEvent: String,
    @NonNull
    @ColumnInfo(name = "date")
    var dateEvent: String,

    @NonNull
    @ColumnInfo(name = "type")
    var eventType: TypeEvent,
    @NonNull
    @ColumnInfo(name = "requestingUser")
    var requestingUser: Int,

    @ColumnInfo(name = "userId")
    val userId: Int? = null,
) {
    constructor( titleEvent: String, descriptionEvent: String, dateEvent: String, eventType: TypeEvent, requestingUser: Int)
            : this(0,  titleEvent, descriptionEvent, dateEvent, eventType, requestingUser)

    constructor( titleEvent: String, descriptionEvent: String, dateEvent: String, eventType: TypeEvent, userId: Int, requestingUser: Int)
            : this(0,  titleEvent, descriptionEvent, dateEvent, eventType, requestingUser,userId )
}
