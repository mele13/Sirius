package com.example.sirius.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sirius.AnimalApplication
import com.example.sirius.model.Animal
import com.example.sirius.data.dao.AnimalDao
import com.example.sirius.model.LikedAnimal
import com.example.sirius.model.News
import com.example.sirius.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AnimalViewModel(private val animalDao: AnimalDao) : ViewModel() {
    private val _currentAnimal = MutableStateFlow<Animal?>(null)
    val currentUser: StateFlow<Animal?> = _currentAnimal
    fun getAllAnimals(): Flow<List<Animal>> = animalDao.getAllAnimals()

    fun getAllAnimalsOrderedByDaysEntryDate(): Flow<List<Animal>> = animalDao.getAllAnimals()

    fun getBirthYears(): Flow<List<String>> = animalDao.getBirthYears()
    fun getBreed(): Flow<List<String>> = animalDao.getBreed()
    fun getTypeAnimal(): Flow<List<String>> = animalDao.getTypeAnimal()

    fun getAnimalsByAgeDesc(birthDate: String): Flow<List<Animal>> = animalDao.getAnimalsByAgeDesc(birthDate)

    fun getAnimalsByBreed(option: String): Flow<List<Animal>> = animalDao.getAnimalsByBreed(option)
    fun getAnimalsByTypeAnimal(option: String): Flow<List<Animal>> = animalDao.getAnimalsByTypeAnimal(option)

    fun getAnimalsByIds(animalIds: List<Int>): Flow<List<Animal>> = animalDao.getAnimalsByIds(animalIds)

    fun getAnimalById(option: Int): Flow<Animal?> = animalDao.getAnimalById(option)

    fun getAnimalsByBirthYearRange(startYear: String, endYear: String): Flow<List<Animal>> = animalDao.getAnimalsByBirthYearRange(startYear, endYear)

    suspend fun insertLikedAnimal(animalId: Int, userId: Int) {
        val likedAnimal = LikedAnimal(animalId = animalId, userId = userId)
        animalDao.insertLikedAnimal(likedAnimal)
    }
    suspend fun removeLikedAnimal(userId: Int, animalId: Int) = animalDao.removeLikedAnimal(userId, animalId)
    fun getLikedAnimals(userId: Int) = animalDao.getLikedAnimals(userId)

    suspend fun updateNameAnimal(animal: Animal, newName : String) {
        animal.nameAnimal = newName
        animalDao.updateNameAnimal(animal.id, newName)
        _currentAnimal.value = animal
    }

    suspend fun updateShortInfoAnimal(animal: Animal, newShortInfo : String) {
        animal.shortInfoAnimal = newShortInfo
        animalDao.updateShortInfoAnimal(animal.id, newShortInfo)
        _currentAnimal.value = animal
    }

    suspend fun updateLongtInfoAnimal(animal: Animal, newLongtInfo : String) {
        animal.shortInfoAnimal = newLongtInfo
        animalDao.updateLongtInfoAnimal(animal.id, newLongtInfo)
        _currentAnimal.value = animal
    }

    suspend fun updatePhotoAnimal(animal: Animal, newPhoto : String) {
        animal.photoAnimal = newPhoto
        animalDao.updateLongtInfoAnimal(animal.id, newPhoto)
        _currentAnimal.value = animal
    }

    suspend fun updateAnimal(animal: Animal) {
        animalDao.updateAnimal(animal)
    }

    suspend fun deleteAnimal(animal: Animal) {
        animalDao.deleteAnimal(animal)
    }

    suspend fun getOurFriends(): Flow<List<Animal>>  {
        return animalDao.getOurFriends()
    }

    suspend fun getLostAnimals(): Flow<List<Animal>> {
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