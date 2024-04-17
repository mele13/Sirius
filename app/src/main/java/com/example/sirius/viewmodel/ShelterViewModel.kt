package com.example.sirius.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sirius.AnimalApplication
import com.example.sirius.data.dao.ShelterDao
import com.example.sirius.model.Shelter
import kotlinx.coroutines.flow.Flow

class ShelterViewModel(private val shelterDao: ShelterDao) : ViewModel() {

    fun getAllShelters() : Flow<List<Shelter>> = shelterDao.getAllShelters()

    fun getShelterById(id : Int) : Flow<Shelter?> = shelterDao.getShelterById(id)

    suspend fun insertShelter(shelter: Shelter) {
        shelterDao.insertShelter(shelter)
    }
    suspend fun deleteShelter(shelter: Shelter) {
        shelterDao.removeShelter(shelter.id)
    }

    suspend fun updateShelter(shelter: Shelter) {
        shelterDao.updateShelter(shelter)
    }


    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnimalApplication)
                ShelterViewModel(application.database.shelterDao())
            }
        }
    }

}

