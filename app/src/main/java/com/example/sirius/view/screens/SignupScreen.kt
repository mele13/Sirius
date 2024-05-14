package com.example.sirius.view.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.model.TypeUser
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.isEmailValid
import com.example.sirius.tools.isPasswordValid
import com.example.sirius.view.components.CustomSnackbar
import com.example.sirius.view.components.HaveAnAccount
import com.example.sirius.view.components.Password
import com.example.sirius.view.components.Paws
import com.example.sirius.view.components.UserInputField
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(navController: NavController, userViewModel: UserViewModel) {
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
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopEnd)
                .clickable {
                    navController.navigate(Routes.SIGNUPSHELTER)
                }
        ) {
            Text(
                stringResource(id = R.string.account_signup_shelter),
                style = TextStyle(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .offset(y = 80.dp),
//            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SignUpHeader(isSystemInDarkTheme())
            // Username
            UserInputField(
                value = username,
                logInButtonClicked = signUpButtonClicked.value,
                Icons.Default.Person,
                R.string.username,
                false
            )
            Spacer(modifier = Modifier.height(3.dp))
            // Email
            UserInputField(
                value = email,
                logInButtonClicked = signUpButtonClicked.value,
                icon = Icons.Default.Email,
                resource = R.string.email,
                true
            )
            Spacer(modifier = Modifier.height(3.dp))
            // Password
            Password(password = password, logInButtonClicked = signUpButtonClicked.value)
            Spacer(modifier = Modifier.height(3.dp))
            // Log In
            HaveAnAccount(
                navController = navController,
                route = Routes.LOGIN,
                resource = R.string.account_login
            )

            Spacer(modifier = Modifier.height(20.dp))


                SignUpButton(
                    onClick = {
                        signUpUser(
                            username.value,
                            email.value,
                            password.value,
                            TypeUser.user,
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
fun SignUpButton(onClick: () -> Unit) {


        // Center - Log In button
        Image(
            painter = painterResource(id = R.drawable.paw2_signup),
            contentDescription = null,
            modifier = Modifier

                .size(230.dp)
                .zIndex(-1f)
                .size(230.dp)
                .offset(x = 16.dp, y = (-100).dp)
                .clickable {
                    onClick()
                }
        )





}

@SuppressLint("CoroutineCreationDuringComposition")
fun signUpUser(
    username: String,
    email: String,
    password: String,
    role: TypeUser,
    signUpButtonClicked: MutableState<Boolean>,
    errorMessage: MutableState<String?>,
    navController: NavController,
    userViewModel: UserViewModel
) {
    signUpButtonClicked.value = true
    if (isEmailValid(email) && isPasswordValid(password)) {
        userViewModel.viewModelScope.launch {
            val success = userViewModel.registerUser(username, email, password, role)
            if (success) {
                delay(2000)
                navController.navigate(Routes.HOME)
            } else {
                errorMessage.value = "Oops! Something went wrong during user creation"
            }
        }
    } else if (!isPasswordValid(password)) {
        errorMessage.value = "Invalid password format.\nPassword must have at least 6 characters, 1 uppercase letter, and 1 special symbol\n"
    } else {
        errorMessage.value = "Invalid email format.\nExpected format: name@example.com\n"
    }
}
@Composable
fun SignUpHeader(isSystemInDarkTheme: Boolean) {
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


