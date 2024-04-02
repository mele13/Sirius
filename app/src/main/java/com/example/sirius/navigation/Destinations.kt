package com.example.sirius.navigation

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.sirius.R

@Composable
fun createDestination(route: String, selectedIcon: Int, iconTextId: Int): Destinations {
    return Destinations(
        route = route,
        selectedIcon = selectedIcon,
        unselectedIcon = selectedIcon,
        iconTextId = iconTextId,
    )
}

@Composable
fun createDestinations(): List<Destinations> {
    return listOf(
        createDestination(Routes.HOME, R.drawable.home_icon, R.string.home),
        createDestination(Routes.ANIMALS, R.drawable.animals_icon, R.string.animals),
        createDestination(Routes.DONATIONS, R.drawable.donations_icon, R.string.donations),
        createDestination(Routes.ABOUTUS, R.drawable.aboutus_icon, R.string.aboutUs),
    )
}

data class Destinations(
    val route: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val iconTextId: Int
)

object Routes {
    const val HOME = "home"
    const val ANIMALS = "animals"
    const val DONATIONS = "donations"
    const val ABOUTUS = "about us"
    const val ANIMALINFO = "animal info"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val LANDINGPAGE = "landing page"
    const val PROFILE = "profile"
    const val LOADING = "loading"
}
