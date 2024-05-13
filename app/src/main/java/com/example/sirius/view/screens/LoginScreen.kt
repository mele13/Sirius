package com.example.sirius.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.navigation.Routes
import com.example.sirius.view.components.CustomSnackbar
import com.example.sirius.view.components.HaveAnAccount
import com.example.sirius.view.components.Password
import com.example.sirius.view.components.Paws
import com.example.sirius.view.components.UserInputField
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, userViewModel: UserViewModel) {
    var username = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var logInButtonClicked by remember { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    Paws()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .offset(y = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id =R.drawable.sirius_name),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                )
                Text(
                    text = stringResource(id = R.string.login),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Username
            UserInputField(username, logInButtonClicked, Icons.Default.Person, R.string.username, false)
            Spacer(modifier = Modifier.height(8.dp))
            // Password
            Password(password, logInButtonClicked)
            Spacer(modifier = Modifier.height(8.dp))
            // Sign Up


            HaveAnAccount(navController = navController, route = Routes.SIGNUP, resource = R.string.account_signup )
            Spacer(modifier = Modifier.height(20.dp))

            ConstraintLayout(
                modifier = Modifier.fillMaxSize()
            ) {
                val (image, text) = createRefs()

                // Center - Log In button
                Image(
                    painter = painterResource(id = R.drawable.paw2),
                    contentDescription = null,
                    modifier = Modifier
                        .constrainAs(image) {
                            centerTo(parent)
                        }
                        .size(230.dp)
                        .zIndex(-1f)
                        .size(230.dp)
                        .offset(x = 16.dp, y = (-100).dp)
                        .clickable {
                            userViewModel.viewModelScope.launch {
                                logInButtonClicked = true
                                val success = userViewModel.login(username.value, password.value)
                                when(success){
                                    "false" -> errorMessage = "Invalid username or password"
                                    else -> navController.navigate(Routes.SHELTER)
                                }


                            }
                        }
                )

                Text(
                    text = stringResource(id = R.string.login),
                    color = Color.White,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .constrainAs(text) {
                            centerTo(parent)
                            //  centerTo(image)
                        }
                        .offset(x = 6.dp, y = (-80).dp)
                )
            }

            // Error Snackbar
            errorMessage?.let { message ->
                CustomSnackbar(
                    message = message,
                    onDismiss = { errorMessage = null },
                )
            }
        }
    }
}

