package com.example.sirius.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sirius.AnimalApplication
import com.example.sirius.data.dao.EventDAO
import com.example.sirius.model.Event
import com.example.sirius.model.TypeEvent
import kotlinx.coroutines.flow.Flow

class EventViewModel(private val eventDao: EventDAO) : ViewModel() {

    fun getEvents(): Flow<List<Event>> = eventDao.getEvent()

    fun getUnallocatedEvents(): Flow<List<Event>> = eventDao.getUnallocatedEvents()

    suspend fun insertEvent(newEvent: Event) {
        eventDao.insertEvent(newEvent)
    }

    suspend fun updateEvent(newEvent: Event) {
        eventDao.updateEvent(newEvent)
    }

    suspend fun deleteEvent(newEvent: Event) {
        eventDao.deleteEvent(newEvent)
    }
    fun stringToTypeEvent(string: String): TypeEvent {
        return when (string.toLowerCase()) {
            "medical" -> TypeEvent.medical
            "cite" -> TypeEvent.cite
            "worker" -> TypeEvent.worker
            "volunteer" -> TypeEvent.volunteer
            "adoption" -> TypeEvent.adoption
            else -> TypeEvent.volunteer
        }
    }

    fun assignEvent(id: Int, userId : Int) = eventDao.assignEvent(id, userId)

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnimalApplication)
                EventViewModel(application.database.eventDao())
            }
        }
    }
}
