package com.example.sirius.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sirius.AnimalApplication
import com.example.sirius.data.dao.ClinicalRecordDao
import com.example.sirius.model.ClinicalRecord
import kotlinx.coroutines.flow.Flow

class ClinicalRecordViewModel(private val clinicalRecordDao: ClinicalRecordDao) : ViewModel() {
    suspend fun insertClinicalRecord(clinicalRecord: ClinicalRecord) {
        clinicalRecordDao.insertClinicalRecord(clinicalRecord)
    }

    suspend fun updateClinicalRecord(clinicalRecord: ClinicalRecord) {
        clinicalRecordDao.updateClinicalRecord(clinicalRecord)
    }

    fun getClinicalRecordsForAnimal(animalId: Int): Flow<List<ClinicalRecord>> {
        return clinicalRecordDao.getClinicalRecordsForAnimal(animalId)
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnimalApplication)
                ClinicalRecordViewModel(application.database.clinicalRecordDao())
            }
        }
    }
}