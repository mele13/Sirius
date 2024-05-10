package com.example.sirius.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.tools.isEmailValid
import com.example.sirius.ui.theme.Green1


@Composable
fun Password(password : MutableState<String>, logInButtonClicked : Boolean){
    var passwordVisibility by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password.value,
        onValueChange = { password.value = it },
        label = {
            Text(
                stringResource(id = R.string.password),
                style = TextStyle(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
            )
        },
        singleLine = true,
        visualTransformation = passwordVisualTransformation(passwordVisibility),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                MaterialTheme.shapes.medium
            ),
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )
        },
        trailingIcon = passwordVisibilityIcon(password.value.isNotBlank(), passwordVisibility) {
            passwordVisibility = !passwordVisibility
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (logInButtonClicked && password.value.isBlank()) Color.Red else Green1,
            unfocusedBorderColor = if (logInButtonClicked && password.value.isBlank()) Color.Red else Green1,
        ),
    )
}



@Composable
private fun passwordVisualTransformation(passwordVisibility: Boolean): VisualTransformation =
    if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()

@Composable
private fun passwordVisibilityIcon(
    isNotBlank: Boolean,
    passwordVisibility: Boolean,
    onClick: () -> Unit
): (@Composable () -> Unit)? {
    return if (isNotBlank) {
        {
            IconButton(onClick = onClick) {
                Icon(
                    painter = if (passwordVisibility) painterResource(id = R.drawable.visibility)
                    else painterResource(id = R.drawable.visibility_off),
                    contentDescription = if (passwordVisibility) "Hide password" else "Show password",
                    modifier = Modifier.aspectRatio(0.5f)
                )
            }
        }
    } else {
        null
    }
}


@Composable

fun Paws(){
    Box(Modifier.fillMaxSize()) {
        // Bottom left
        Image(
            painter = painterResource(id = R.drawable.paw1),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(230.dp)
                .absoluteOffset((-6).dp)
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
fun UserInputField(
    value: MutableState<String>,
    logInButtonClicked: Boolean,
    icon: ImageVector,
    resource: Int,
    isEmail: Boolean
) {
    OutlinedTextField(
        value = value.value,
        onValueChange = { value.value = it },
        label = {
            Text(
                stringResource(resource),
                style = TextStyle(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
            )
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                MaterialTheme.shapes.medium
            ),
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        colors = if (isEmail) {
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (value.value.isNotBlank() && !isEmailValid(value.value)
                    || logInButtonClicked && value.value.isBlank()) Color.Red else Green1,
                unfocusedBorderColor = if (value.value.isNotBlank() && !isEmailValid(value.value)
                    || logInButtonClicked && value.value.isBlank()) Color.Red else Green1,
            )
        } else {
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (logInButtonClicked && value.value.isBlank()) Color.Red else Green1,
                unfocusedBorderColor = if (logInButtonClicked && value.value.isBlank()) Color.Red else Green1,
            )
        }
    )
}
@Composable
fun HaveAnAccount(navController : NavController, route : String, resource: Int){
    TextButton(
        onClick = { navController.navigate(route)  },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .offset(y = (-8).dp)
    ) {
        Text(
            stringResource(id = resource),
            style = TextStyle(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
            textAlign = TextAlign.Center
        )
    }
}