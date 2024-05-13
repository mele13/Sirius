package com.example.sirius.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sirius.model.Shelter
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelterDao {

    @Query("SELECT * from Shelters")
    fun getAllShelters(): Flow<List<Shelter>>

    @Query("SELECT id from Shelters")
    fun getAllSheltersId(): Flow<List<Int>>

    @Query("SELECT * FROM Shelters WHERE name = :name")
    fun getShelterByName(name: String): Flow<Shelter?>

    @Query("SELECT * FROM Shelters WHERE id = :id")
    fun getShelterById(id: Int): Flow<Shelter?>

    @Query("DELETE FROM Shelters WHERE id = :id ")
    suspend fun removeShelter(id: Int)

    @Insert
    suspend fun insertShelter(shelter: Shelter)


    @Query("UPDATE Shelters SET name = :newName WHERE id = :shelterId")
    suspend fun updateShelterName(shelterId: Int, newName: String)

    @Query("UPDATE Shelters SET name = :newAboutUs WHERE id = :shelterId")
    suspend fun updateShelterAboutUs(shelterId: Int, newAboutUs: String)
    @Query("UPDATE Shelters SET name = :newLocation WHERE id = :shelterId")
    suspend fun updateShelterLocation(shelterId: Int, newLocation: String)
    @Query("UPDATE Shelters SET name = :newSchedule WHERE id = :shelterId")
    suspend fun updateShelterSchedule(shelterId: Int, newSchedule: String)
    @Query("UPDATE Shelters SET name = :newData WHERE id = :shelterId")
    suspend fun updateShelterData(shelterId: Int, newData: String)

    @Query("UPDATE Shelters SET name = :newPhone WHERE id = :shelterId")
    suspend fun updateShelterPhone(shelterId: Int, newPhone: String)

    @Query("UPDATE Shelters SET name = :newEmail WHERE id = :shelterId")
    suspend fun updateShelterEmail(shelterId: Int, newEmail: String)

    @Query("SELECT id FROM Shelters WHERE id_owner = :id")
    fun getSheltersOwner(id: Int): Flow<Int?>

    @Update
    suspend fun updateShelter(shelter: Shelter)

}