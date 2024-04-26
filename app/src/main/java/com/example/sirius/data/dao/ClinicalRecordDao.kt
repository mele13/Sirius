package com.example.sirius.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sirius.model.ClinicalRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicalRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClinicalRecord(clinicalRecord: ClinicalRecord)

    @Update
    suspend fun updateClinicalRecord(clinicalRecord: ClinicalRecord)

    @Query("SELECT * FROM ClinicalRecord WHERE animal_id = :animalId")
    fun getClinicalRecordsForAnimal(animalId: Int): Flow<List<ClinicalRecord>>
}