package com.example.sirius.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ClinicalRecord",
    foreignKeys = [
        ForeignKey(
            entity = Animal::class,
            parentColumns = ["id"],
            childColumns = ["animal_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ClinicalRecord(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @NonNull
    @ColumnInfo(name = "animal_id")
    var animalId: Int,
    @NonNull
    @ColumnInfo(name = "appointment_date")
    var appointmentDate: String,
    @NonNull
    @ColumnInfo(name = "illnesses")
    var illnesses: String,
    @NonNull
    @ColumnInfo(name = "prescriptions")
    var prescriptions: String,
    @NonNull
    @ColumnInfo(name = "prescription_end_date")
    var prescriptionEndDate: String,
    @NonNull
    @ColumnInfo(name = "vaccines")
    var vaccines: String,
    @NonNull
    @ColumnInfo(name = "comments")
    var comments: String,
)