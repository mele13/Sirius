package com.example.sirius.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.navigation.Routes
import com.example.sirius.ui.theme.Green1
import com.example.sirius.view.components.CustomSnackbar
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import com.example.sirius.tools.isEmailValid
import com.example.sirius.tools.isPasswordValid
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController, userViewModel: UserViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var signUpButtonClicked by remember { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordVisibility by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .background(color = Color.Yellow)
//            .padding(16.dp)
    ) {
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
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = {
                    Text(
                        stringResource(id = R.string.username),
                        style = TextStyle(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .background(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                        MaterialTheme.shapes.medium
                    ),
                textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (signUpButtonClicked && username.isBlank()) Color.Red else Green1,
                    unfocusedBorderColor = if (signUpButtonClicked && username.isBlank()) Color.Red else Green1,
                )
            )
            Spacer(modifier = Modifier.height(3.dp))
            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        stringResource(id = R.string.email),
                        style = TextStyle(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .background(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                        MaterialTheme.shapes.medium
                    ),
                textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (email.isNotBlank() && !isEmailValid(email)
                                             || signUpButtonClicked && email.isBlank()) Color.Red
                                         else Green1,
                    unfocusedBorderColor = if (email.isNotBlank() && !isEmailValid(email)
                                               || signUpButtonClicked && email.isBlank()) Color.Red
                                           else Green1,
                )
            )
            Spacer(modifier = Modifier.height(3.dp))
            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        stringResource(id = R.string.password),
                        style = TextStyle(color = if (isSystemInDarkTheme()) Color.White
                                                  else Color.Black)
                    )
                },
                singleLine = true,
                visualTransformation = if (passwordVisibility) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .background(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                        MaterialTheme.shapes.medium
                    ),
                textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (password.isNotBlank()) {
                        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                            Icon(
                                painter = if (passwordVisibility) painterResource(id = R.drawable.visibility)
                                          else painterResource(id = R.drawable.visibility_off),
                                contentDescription = if (passwordVisibility) "Hide password" else "Show password",
                                modifier = Modifier.aspectRatio(0.5f)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (password.isNotBlank() && !isPasswordValid(password)
                                             || signUpButtonClicked && password.isBlank()) Color.Red
                                         else Green1,
                    unfocusedBorderColor = if (password.isNotBlank() && !isPasswordValid(password)
                                               || signUpButtonClicked && password.isBlank()) Color.Red
                                           else Green1,
                )
            )
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
            // Sign Up button
            TextButton(
                onClick = {
                    userViewModel.viewModelScope.launch {
                        signUpButtonClicked = true
                        if (isEmailValid(email) && isPasswordValid(password)) {
                            val success = userViewModel.registerUser(username, email, password)
                            if (success) {
                                delay(2000)
                                navController.navigate(Routes.HOME)
                            } else {
                                errorMessage = "Oops! Something went wrong during user creation"
                            }
                        } else if (!isPasswordValid(password)) {
                            errorMessage = "Invalid password format.\nPassword must have at least 6 characters, 1 uppercase letter, and 1 special symbol\n"
                        } else {
                            errorMessage = "Invalid email format.\nExpected format: name@example.com\n"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .offset(y = 23.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White)
            ) {
                Text(
                    stringResource(id = R.string.signup),
                    color = Color.White,
                    fontSize = 25.sp
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
        // Bottom left
        Image(
            painter = painterResource(id = R.drawable.paw1),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(230.dp)
                .absoluteOffset((-6).dp)
                .zIndex(-1f)
        )
        // Center - Log In button
        Image(
            painter = painterResource(id = R.drawable.paw2),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(230.dp)
                .offset(x = 16.dp, y = 130.dp)
                .zIndex(-1f)
        )
        // Top right big
        Image(
            painter = painterResource(id = R.drawable.paw3),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(150.dp)
                .offset(x = 10.dp, y = (-30).dp)
                .zIndex(-2f)
        )
        // Top right small
        Image(
            painter = painterResource(id = R.drawable.paw4),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(120.dp)
                .offset(x = 20.dp, y = 152.dp)
                .zIndex(-2f)
        )
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
