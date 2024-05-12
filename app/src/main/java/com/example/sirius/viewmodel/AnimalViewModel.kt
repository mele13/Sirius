package com.example.sirius.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sirius.AnimalApplication
import com.example.sirius.data.dao.AnimalDao
import com.example.sirius.model.Animal
import com.example.sirius.model.LikedAnimal
import kotlinx.coroutines.flow.Flow

class AnimalViewModel(private val animalDao: AnimalDao) : ViewModel() {
    fun getAllAnimals(): Flow<List<Animal>> = animalDao.getAllAnimals()

    suspend fun insertAnimal(animal: Animal) {
        animalDao.insertAnimal(animal)
    }

    fun getAllAnimalsOrderedByDaysEntryDate(): Flow<List<Animal>> = animalDao.getAllAnimals()

    fun getBirthYears(): Flow<List<String>> = animalDao.getBirthYears()
    fun getBreed(): Flow<List<String>> = animalDao.getBreed()
    fun getTypeAnimal(): Flow<List<String>> = animalDao.getTypeAnimal()

    fun getAnimalsByAgeDesc(birthDate: String): Flow<List<Animal>> = animalDao.getAnimalsByAgeDesc(birthDate)

    fun getAnimalsByBreed(option: String): Flow<List<Animal>> = animalDao.getAnimalsByBreed(option)
    fun getAnimalsByTypeAnimal(option: String): Flow<List<Animal>> = animalDao.getAnimalsByTypeAnimal(option)

    fun getAnimalById(option: Int): Flow<Animal?> = animalDao.getAnimalById(option)


    suspend fun insertLikedAnimal(animalId: Int, userId: Int) {
        val likedAnimal = LikedAnimal(animalId = animalId, userId = userId)
        animalDao.insertLikedAnimal(likedAnimal)
    }
    suspend fun removeLikedAnimal(userId: Int, animalId: Int) = animalDao.removeLikedAnimal(userId, animalId)

    suspend fun updateAnimal(animal: Animal) {
        animalDao.updateAnimal(animal)
    }



    suspend fun deleteAnimal(animal: Animal) {
        animalDao.deleteAnimal(animal)
    }

    fun getOurFriends(): Flow<List<Animal>>  {
        return animalDao.getOurFriends()
    }

    fun getLostAnimals(): Flow<List<Animal>> {
        return animalDao.getLostAnimals()
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnimalApplication)
                AnimalViewModel(application.database.animalDao())
            }
        }
    }
}