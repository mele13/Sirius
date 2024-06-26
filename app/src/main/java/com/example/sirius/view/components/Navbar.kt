package com.example.sirius.view.components

import AboutUsScreen
import CalendarScreen
import DonationsScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sirius.R
import com.example.sirius.model.TypeUser
import com.example.sirius.navigation.Destinations
import com.example.sirius.navigation.Routes
import com.example.sirius.navigation.createDestinations
import com.example.sirius.ui.theme.Green3
import com.example.sirius.view.screens.AdoptionApplications
import com.example.sirius.view.screens.AnimalInfo
import com.example.sirius.view.screens.AnimalSponsor
import com.example.sirius.view.screens.AnimalsGallery
import com.example.sirius.view.screens.ChatScreen
import com.example.sirius.view.screens.ClinicalRecord
import com.example.sirius.view.screens.HandlingScreen
import com.example.sirius.view.screens.HomeScreen
import com.example.sirius.view.screens.LandingPage
import com.example.sirius.view.screens.LoadingPage
import com.example.sirius.view.screens.LoginScreen
import com.example.sirius.view.screens.Messages
import com.example.sirius.view.screens.NewsInfo
import com.example.sirius.view.screens.ProfileScreen
import com.example.sirius.view.screens.SettingsScreen
import com.example.sirius.view.screens.ShelterList
import com.example.sirius.view.screens.SignupScreen
import com.example.sirius.view.screens.SignupShelterScreen
import com.example.sirius.view.screens.filteredShelter
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.ChatViewModel
import com.example.sirius.viewmodel.EventViewModel
import com.example.sirius.viewmodel.NewsViewModel
import com.example.sirius.viewmodel.ShelterViewModel
import com.example.sirius.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    eventViewModel: EventViewModel,
    selectedDestination: String,
    navigateDestination: (Destinations) -> Unit,
) {

    val animalViewModel: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)
    val newsViewModel : NewsViewModel = viewModel(factory = NewsViewModel.factory)
    val shelterViewModel : ShelterViewModel = viewModel(factory = ShelterViewModel.factory)
    val user = userViewModel.getAuthenticatedUser()
    var filteredShelter = filteredShelter




    if(user?.role == TypeUser.admin){
        val shelters = shelterViewModel.getAllSheltersId().collectAsState(emptyList()).value
        filteredShelter.addAll(shelters)
    } else if( user?.role == TypeUser.owner || user?.role == TypeUser.worker || user?.role == TypeUser.volunteer) {
        val shelters =
            user.let { userViewModel.getShelterByUserId(it.id).collectAsState(emptyList()).value }
        if (shelters != null) {
            filteredShelter.addAll(shelters)
        }
    } else{
        filteredShelter = filteredShelter
    }


    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End
        ) {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute !in listOf(
                    Routes.LOGIN,
                    Routes.SIGNUP,
                    Routes.LANDINGPAGE,
                    Routes.LOADING,
                    Routes.ANIMALINFO,
                    Routes.ANIMALINFO + "/{id}",
                    Routes.PROFILE,
                    Routes.SIGNUPSHELTER,
                    Routes.SHELTERLIST
                )
            ) {
                if(user?.role == TypeUser.user) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.filter),
                            contentDescription = stringResource(id = R.string.filter),
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(Routes.SHELTERLIST)
                                }
                                .padding(top = 16.dp, start = 16.dp)
                        )
                        ProfileButton(
                            onClick = {
                                if (userViewModel.getAuthenticatedUser() != null)
                                    navController.navigate(Routes.PROFILE)
                                else {
                                    navController.navigate(Routes.LOGIN)
                                }
                            },
                            modifier = Modifier
                                .padding(top = 16.dp, end = 16.dp)

                        )
                    }
                }else{
                    ProfileButton(
                        onClick = {
                            if (userViewModel.getAuthenticatedUser() != null)
                                navController.navigate(Routes.PROFILE)
                            else {
                                navController.navigate(Routes.LOGIN)
                            }
                        },
                        modifier = Modifier
                            .padding(top = 16.dp, end = 16.dp)
                    )
                }

            }
            NavHost(
                modifier = Modifier.weight(1f),
                navController = navController,
                startDestination =
                if (user != null && user.role == TypeUser.user) {
                    Routes.SHELTERLIST
                }else{
                    Routes.LOADING
                }

            ) {
                composable(route = Routes.HOME) {
                    val animalList by animalViewModel.getAllAnimalsOrderedByDaysEntryDate().collectAsState(initial = emptyList())
                    val newsList by newsViewModel.getNews().collectAsState(initial = emptyList())
                    val typeList by animalViewModel.getTypeAnimal().collectAsState(emptyList())

                    HomeScreen(navController = navController, animalList = animalList, newsList = newsList, userViewModel = userViewModel, typeList = typeList)
                }
                composable(route = Routes.ANIMALS) {
                    val ageList by animalViewModel.getBirthYears().collectAsState(emptyList())
                    val breedList by animalViewModel.getBreed().collectAsState(emptyList())
                    val typeList by animalViewModel.getTypeAnimal().collectAsState(emptyList())
                    AnimalsGallery(
                        navController = navController,
                        ageList = ageList,
                        breedList = breedList,
                        typeList = typeList,
                        userViewModel = userViewModel,
                        type = null,
                        isAnimal = true,
                        filteredShelters = filteredShelter
                    )
                }
                composable(route = Routes.ANIMALS + "/{type}",
                    arguments = listOf(navArgument(name = "type") {
                        type = NavType.StringType
                    })) {
                    val isAnimal = it.arguments?.getString("type")?.contains("Animals", ignoreCase = true) == true

                    val ageList by animalViewModel.getBirthYears().collectAsState(emptyList())
                    val breedList by animalViewModel.getBreed().collectAsState(emptyList())
                    val typeList by animalViewModel.getTypeAnimal().collectAsState(emptyList())
                    AnimalsGallery(
                        navController = navController,
                        ageList = ageList,
                        breedList = breedList,
                        typeList = typeList,
                        userViewModel = userViewModel,
                        type = it.arguments?.getString("type"),
                        isAnimal = isAnimal,
                        filteredShelters = filteredShelter
                    )
                }
                composable(route = Routes.DONATIONS) {
                    DonationsScreen()
                }
                composable(route = Routes.ABOUTUS) {
                    AboutUsScreen(shelterViewModel = shelterViewModel, userViewModel = userViewModel)
                }
                composable(route = Routes.ABOUTUS + "/{id}",
                    arguments = listOf(navArgument(name = "id") {
                        type = NavType.IntType
                    })) {

                    AboutUsScreen(
                        it.arguments?.getInt("id"),
                        shelterViewModel,
                        userViewModel
                    )
                }
                composable(route = Routes.CHAT) {
                    ChatScreen(navController, chatViewModel, userViewModel, filteredShelters = filteredShelter)
                }
                composable(route = Routes.SETTIGNS) {
                    SettingsScreen(shelterViewModel, navController, userViewModel, true)
                }

                composable(route = Routes.ADOPTION) {
                    AdoptionApplications(userViewModel,chatViewModel)
                }


                composable(route = Routes.CHAT + "/{recipient_user}",
                    arguments = listOf(navArgument(name = "recipient_user") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) { navBackStackEntry ->
                    val recipientUser = navBackStackEntry.arguments?.getInt("recipient_user") ?: -1
                    Messages(navController, recipientUser, userViewModel, chatViewModel)
                }
                composable(route = Routes.ANIMALINFO + "/{id}",
                    arguments = listOf(navArgument(name = "id") {
                        type = NavType.IntType
                    })) {

                    AnimalInfo(
                        it.arguments?.getInt("id"),
                        animalViewModel,
                        userViewModel,
                        chatViewModel,
                        navController,

                    )
                }
                composable(route = Routes.NEWSINFO + "/{id}",
                    arguments = listOf(navArgument(name = "id") {
                        type = NavType.IntType
                    })) {

                    NewsInfo(
                        it.arguments?.getInt("id"),
                        newsViewModel,
                        userViewModel,
                        )
                }
                composable(route = Routes.SHELTER) {
                    val animalList by animalViewModel.getAllAnimalsOrderedByDaysEntryDate().collectAsState(initial = emptyList())
                    val newsList by newsViewModel.getNews().collectAsState(initial = emptyList())
                    val typeList by animalViewModel.getTypeAnimal().collectAsState(emptyList())

                    HomeScreen(navController = navController, animalList = animalList, newsList = newsList, userViewModel = userViewModel, typeList = typeList)
                }
                composable(route = Routes.MANAGEMENT) {
                    val shelterId = shelterViewModel.getSheltersOwner(userViewModel.getAuthenticatedUser()!!.id).collectAsState(null).value
                    HandlingScreen(id = shelterId)
                }
                composable(route = Routes.LOGIN) {
                    LoginScreen(navController = navController, userViewModel = userViewModel)
                }
                composable(route = Routes.SIGNUP) {
                    SignupScreen(navController = navController, userViewModel = userViewModel)
                }
                composable(route = Routes.SIGNUPSHELTER) {
                    SignupShelterScreen(navController = navController, userViewModel = userViewModel)
                }
                composable(route = Routes.CALENDAR){
                    CalendarScreen(userViewModel = userViewModel, eventViewModel = eventViewModel)
                }
                composable(route = Routes.LANDINGPAGE) {
                    LandingPage(navController = navController)
                }
                composable(route = Routes.LOADING){
                    LoadingPage(navController = navController, 0)
                }
                composable(route = Routes.LOADING + "/{id}",
                    arguments = listOf(navArgument(name = "id") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) { navBackStackEntry ->
                    val id = navBackStackEntry.arguments?.getInt("id") ?: -1
                    LoadingPage(navController, id)
                }
                composable(route = Routes.PROFILE) {
                    ProfileScreen(
                        navController = navController,
                        userViewModel = userViewModel,
                        eventViewModel = eventViewModel
                    )
                }
                composable(route = Routes.CLINICALRECORD + "/{id}",
                    arguments = listOf(navArgument(name = "id") {
                        type = NavType.IntType
                    })) {

                    ClinicalRecord(it.arguments?.getInt("id"))
                }

                composable(route = Routes.SPONSORING + "/{id}",
                    arguments = listOf(navArgument(name = "id") {
                            type = NavType.IntType
                    })) {
                    it.arguments?.getInt("id")?.let { it1 -> AnimalSponsor(id = it1) }
                }
                composable(route = Routes.SHELTERLIST) {
                    ShelterList(navController = navController, shelterViewModel = shelterViewModel, userViewModel = userViewModel)
                }
            }
            if (currentRoute !in listOf(
                    Routes.LANDINGPAGE, Routes.SIGNUP, Routes.LOGIN,
                    Routes.LOADING, Routes.LOADING + "/{id}", Routes.SIGNUPSHELTER, Routes.SHELTERLIST
                )
            ) {
                Navbar(
                    selectedDestination = selectedDestination,
                    navigateDestination = navigateDestination,
                    userViewModel
                )
            }
        }
    }
}

@Composable
private fun selectColor(destination: Destinations, selectedDestination: String): Color {
    val selected = selectedDestination == destination.route
    return if (selected) Green3 else if (!isSystemInDarkTheme()) Color.Black else Color.White
}
@Composable
fun Navbar(
    selectedDestination: String,
    navigateDestination: (Destinations) -> Unit,
    userViewModel: UserViewModel
) {
    val destinations = createDestinations(userViewModel = userViewModel)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        destinations.forEach { destination ->


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable {
                        navigateDestination(destination)
                    }
                    .weight(1f)
                    .background(if (selectedDestination == destination.route) Green3.copy(alpha = 0.2f) else Color.Transparent)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    painter = painterResource(
                        id = if (selectedDestination == destination.route) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        }
                    ),
                    contentDescription = stringResource(id = destination.iconTextId),
                    tint = if (selectedDestination == destination.route) Green3 else if (!isSystemInDarkTheme()) Color.Black else Color.White,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = destination.iconTextId),
                    color = selectColor(destination = destination, selectedDestination = selectedDestination),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ProfileButton(onClick: () -> Unit, modifier: Modifier) {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "Profile",
        modifier = modifier.clickable { onClick() },
    )
}

class NavigationActions(private val navController: NavHostController) {
    fun navigateTo(destination: Destinations) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }
}

