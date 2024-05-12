package com.example.sirius.view.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.navigation.Routes
import com.example.sirius.view.components.CustomSnackbar
import com.example.sirius.view.components.Password
import com.example.sirius.view.components.Paws
import com.example.sirius.view.components.UserInputField
import com.example.sirius.viewmodel.UserViewModel

@Composable
fun SignupShelterScreen(navController: NavController, userViewModel: UserViewModel) {
    var username = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var email = remember { mutableStateOf("") }
    var signUpButtonClicked = remember { mutableStateOf(false) }
    var errorMessage = rememberSaveable { mutableStateOf<String?>(null) }

    Paws()
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .offset(y = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SignUpShelterHeader(isSystemInDarkTheme())
            // Username
            UserInputField(
                value = username,
                logInButtonClicked = signUpButtonClicked.value,
                Icons.Default.Person,
                R.string.sheltername,
                false
            )
            Spacer(modifier = Modifier.height(3.dp))
            // Email
            UserInputField(
                value = email,
                logInButtonClicked = signUpButtonClicked.value,
                icon = Icons.Default.Email,
                resource = R.string.emailowner,
                true
            )
            Spacer(modifier = Modifier.height(3.dp))
            // Password
            Password(password = password, logInButtonClicked = signUpButtonClicked.value)

            Spacer(modifier = Modifier.height(3.dp))
            // Log In
            TextButton(
                onClick = { navController.navigate(Routes.LOGIN) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .offset(y = (-8).dp)
            ) {
                Text(
                    stringResource(id = R.string.account_login),
                    style = TextStyle(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            SignUpButton(
                onClick = {
                    signUpUser(
                        username.value,
                        email.value,
                        password.value,
                        signUpButtonClicked,
                        errorMessage,
                        navController,
                        userViewModel
                    )
                }
            )
            // Error Snackbar
            errorMessage?.let { message ->
                message.value?.let {
                    CustomSnackbar(
                        message = it,
                        onDismiss = { errorMessage.value = null },
                    )
                }
            }
        }

    }
}

@Composable
fun SignUpShelterHeader(isSystemInDarkTheme: Boolean) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id =R.drawable.sirius_name),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = if (isSystemInDarkTheme) Color.White else Color.Black)
        )
        Text(
            text = stringResource(id = R.string.signup),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

    }
    Spacer(modifier = Modifier.height(4.dp))
}


