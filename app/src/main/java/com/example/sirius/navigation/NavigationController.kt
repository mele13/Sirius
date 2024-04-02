package com.example.sirius.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sirius.view.components.NavigationActions
import com.example.sirius.view.components.NavigationContent
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.NewsViewModel
import com.example.sirius.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationController(
    userViewModel: UserViewModel,
    animalViewModel: AnimalViewModel,
    newsViewModel: NewsViewModel
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        val navigateAction = remember(navController) {
            NavigationActions(navController)
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val selectedDestination = navBackStackEntry?.destination?.route ?: Routes.HOME

        NavigationContent(
            modifier = Modifier,
            navController = navController,
            userViewModel = userViewModel,
            selectedDestination = selectedDestination,
            navigateDestination = navigateAction::navigateTo,
            animalViewModel = animalViewModel,
            newsViewModel = newsViewModel,
        )
    }
}