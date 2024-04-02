package com.example.sirius.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sirius.AnimalApplication
import com.example.sirius.data.dao.NewsDao
import com.example.sirius.model.News
import kotlinx.coroutines.flow.Flow

class NewsViewModel(private val newsDao: NewsDao) : ViewModel() {

    fun getNews(): Flow<List<News>> = newsDao.getNews()

    fun getNewsByTitle(title: String): Flow<List<News>> = newsDao.getNewsByTitle(title)

//    fun getNewsById(newsId: Int): News? = viewModelScope.launch {
//        newsDao.getNewsById(newsId)
//    }

    suspend fun deleteAllNews() {
        newsDao.deleteAllNews()
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnimalApplication)
                NewsViewModel(application.database.newsDao())
            }
        }
    }
}
