package com.example.sirius

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.sirius.model.User
import com.example.sirius.navigation.NavigationController
import com.example.sirius.ui.theme.SiriusTheme
import com.example.sirius.viewmodel.UserViewModel
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by lazy {
        val animalApplication = application as AnimalApplication
        animalApplication.initContext(applicationContext)
        UserViewModel((application as AnimalApplication).userDao)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = applicationContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        setContent {
            SiriusTheme {
                NavigationController(userViewModel)
            }
        }

        lifecycleScope.launchWhenCreated {
            if (userViewModel.currentUser.value == null) {
                val userInfoJson = sharedPreferences.getString("user_info", null)
                if (userInfoJson != null) {
                    val user = Gson().fromJson(userInfoJson, User::class.java)
                    userViewModel.login(user.username, user.password)
                }
            }
        }
    }
}
