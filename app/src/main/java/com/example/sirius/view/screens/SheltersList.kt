package com.example.sirius.view.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.sirius.viewmodel.ShelterViewModel
import com.example.sirius.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition", "DiscouragedApi")
@Composable
fun ShelterList(
    navController: NavController,
    shelterViewModel: ShelterViewModel,
    userViewModel: UserViewModel
) {
    SettingsScreen(shelterViewModel = shelterViewModel, navController = navController, userViewModel = userViewModel)
}