package com.example.sirius.data.dao

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sirius.model.Animal
import com.example.sirius.model.LikedAnimal
import com.example.sirius.model.TypeAnimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Dao
interface AnimalDao {
    @Insert
    suspend fun insertAnimal(animal: Animal)

    @Update
    suspend fun updateAnimal(animal: Animal)

    @Delete
    suspend fun deleteAnimal(animal: Animal)

    @Query("SELECT * from Animal")
    fun getAllAnimals(): Flow<List<Animal>>

    @Query("SELECT * FROM Animal ORDER BY entry_date DESC")
    fun getAllAnimalsOrderedByDaysEntryDate(): Flow<List<Animal>>

    @Query("SELECT * FROM Animal WHERE name = :name ORDER BY entry_date DESC")
    fun getAnimalByName(name: String): Flow<List<Animal>>

    @Query("SELECT * FROM Animal WHERE id = :id")
    fun getAnimalById(id: Int): Flow<Animal?>

    @Query("SELECT * FROM Animal WHERE entry_date = :entryDate ORDER BY entry_date DESC")
    fun getAnimalByTimeShelter(entryDate: String): Flow<List<Animal>>

    @Query("SELECT * FROM Animal WHERE type_animal = :typeAnimal ORDER BY entry_date DESC")
    fun getAnimalByTypeAnimal(typeAnimal: String): Flow<List<Animal>>

    @Query("SELECT * FROM Animal WHERE breed = :breed ORDER BY entry_date DESC")
    fun getAnimalByBreed(breed: String): Flow<List<Animal>>

    @Query("SELECT * FROM Animal WHERE id IN (:animalIds)")
    fun getAnimalsByIds(animalIds: List<Int>): Flow<List<Animal>>

    @Query("SELECT * FROM Animal WHERE birth_date = :birthDate ORDER BY birth_date ASC")
    fun getAnimalsByAgeASC(birthDate: Int): Flow<List<Animal>>

    @Query("SELECT * FROM Animal WHERE SUBSTR(birth_date, 1, 4) = :year ORDER BY birth_date DESC")
    fun getAnimalsByAgeDesc(year: String): Flow<List<Animal>>;

    @Query("SELECT DISTINCT SUBSTR(birth_date, 1, 4) AS birth_year FROM Animal ORDER BY birth_year ASC")
    fun getBirthYears(): Flow<List<String>>

    @Query("SELECT * FROM Animal WHERE breed = :breed")
    fun getAnimalsByBreed(breed: String): Flow<List<Animal>>

    @Query("SELECT DISTINCT breed FROM Animal")
    fun getBreed(): Flow<List<String>>

    @Query("SELECT * FROM Animal WHERE type_animal = :typeAnimal")
    fun getAnimalsByTypeAnimal(typeAnimal: String): Flow<List<Animal>>

    @Query("SELECT DISTINCT type_animal FROM Animal")
    fun getTypeAnimal(): Flow<List<String>>

    @Query("SELECT animal.* FROM Animal INNER JOIN LikedAnimal ON animal.id = LikedAnimal.animal_id WHERE LikedAnimal.user_id = :userId")
    fun getLikedAnimals(userId: Int): Flow<List<Animal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedAnimal(likedAnimal: LikedAnimal)

    @Query("DELETE FROM LikedAnimal WHERE user_id = :userId AND animal_id = :animalId")
    suspend fun removeLikedAnimal(userId: Int, animalId: Int)

    @Query("SELECT * FROM Animal WHERE SUBSTR(birth_date, 1, 4) BETWEEN :startYear AND :endYear")
    fun getAnimalsByBirthYearRange(startYear: String, endYear: String): Flow<List<Animal>>
}