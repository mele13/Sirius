package com.example.sirius.navigation

import androidx.compose.runtime.Composable
import com.example.sirius.R
import com.example.sirius.model.TypeUser
import com.example.sirius.viewmodel.UserViewModel

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
fun createDestinations(userViewModel: UserViewModel): List<Destinations> {

    return if (userViewModel.getAuthenticatedUser()?.role?.equals(TypeUser.user) == true || userViewModel.getAuthenticatedUser()?.equals(null) != false) {
        listOf(
            createDestination(Routes.HOME, R.drawable.home_icon, R.string.home),
            createDestination(Routes.ANIMALS, R.drawable.animals_icon, R.string.animals),
            createDestination(Routes.CHAT, R.drawable.chat_icon, R.string.chat),
            createDestination(Routes.DONATIONS, R.drawable.donations_icon, R.string.donations),
            createDestination(Routes.ABOUTUS, R.drawable.aboutus_icon, R.string.aboutUs),
        )
    } else if(userViewModel.getAuthenticatedUser()?.role?.equals(TypeUser.owner) == true || userViewModel.getAuthenticatedUser()?.equals(null) != false) {
        listOf(
            createDestination(Routes.HOME, R.drawable.home_icon, R.string.home),
            createDestination(Routes.ANIMALS, R.drawable.animals_icon, R.string.animals),
            createDestination(Routes.CHAT, R.drawable.chat_icon, R.string.chat),
            createDestination(Routes.CALENDAR, R.drawable.calendar,R.string.calendar ),
            createDestination(Routes.MANAGEMENT, R.drawable.managment, R.string.handling),
        )
    }
    else{
        listOf(
            createDestination(Routes.HOME, R.drawable.home_icon, R.string.home),
            createDestination(Routes.ANIMALS, R.drawable.animals_icon, R.string.animals),
            createDestination(Routes.CHAT, R.drawable.chat_icon, R.string.chat),
            createDestination(Routes.CALENDAR, R.drawable.calendar,R.string.calendar ),
            createDestination(Routes.ABOUTUS, R.drawable.aboutus_icon, R.string.aboutUs),
        )
    }
}

data class Destinations(
    val route: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val iconTextId: Int
)

object Routes {
    const val HOME = "home"
    const val SHELTER = "shelter"
    const val ANIMALS = "animals"
    const val DONATIONS = "donations"
    const val ABOUTUS = "about us"
    const val ANIMALINFO = "animal info"
    const val CLINICALRECORD = "clinical record"
    const val SPONSORING = "animal sponsoring"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val SIGNUPSHELTER = "signup shelter"
    const val LANDINGPAGE = "landing page"
    const val PROFILE = "profile"
    const val LOADING = "loading"
    const val CHAT = "chat"
    const val SETTIGNS = "settings"
    const val NEWSINFO = "news info"
    const val CALENDAR = "calendar"
    const val MANAGEMENT = "management"
    const val SHELTERLIST = "shelters"
}
