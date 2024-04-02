package com.example.sirius

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.sirius.view.screens.HomeScreen
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.NewsViewModel
import org.junit.Rule
import org.junit.Test

class IUTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreenTest() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            val animalVm: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)
            val animalList by animalVm.getAllAnimalsOrderedByDaysEntryDate().collectAsState(initial = emptyList())
            val newsVm: NewsViewModel = viewModel(factory = NewsViewModel.factory)
            val newsList by newsVm.getNews().collectAsState(initial = emptyList())

            HomeScreen(navController = navController, animalList = animalList, newsList = newsList)
        }
        Thread.sleep(5000)
    }
}