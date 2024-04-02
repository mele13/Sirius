package com.example.sirius.view.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.model.Animal
import com.example.sirius.model.User
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.buildAnAgeText
import com.example.sirius.tools.calculateAge
import com.example.sirius.tools.isEmailValid
import com.example.sirius.tools.isPasswordValid
import com.example.sirius.ui.theme.Gold
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Orange
import com.example.sirius.view.components.CustomSnackbar
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    animalViewModel: AnimalViewModel
) {
    val user by remember { mutableStateOf(userViewModel.getAuthenticatedUser()) }
    val username by remember { mutableStateOf(user?.username ?: "") }
    val email by remember { mutableStateOf(user?.email ?: "") }
    var imageUrl by remember { mutableStateOf(user?.photoUser ?: "") }
    val likedAnimals by userViewModel.getLikedAnimals(user?.id ?: -1).collectAsState(emptyList())
    val currentUser by userViewModel.currentUser.collectAsState()
    var showUpdateImageDialog by remember { mutableStateOf(false) }

    val predefinedImageList = listOf(
        "res/drawable/user_image1",
        "res/drawable/user_image2",
        "res/drawable/user_image3",
        "res/drawable/user_image4",
        "res/drawable/user_image5",
        "res/drawable/user_image6",
        "res/drawable/user_image7",
        "res/drawable/user_image8",
        "res/drawable/user_image9",
        "res/drawable/user_image10"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp, start = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { showUpdateImageDialog = true },
                        modifier = Modifier
                            .size(20.dp)
                            .zIndex(20f)
                            .offset(x = (-70).dp, y = (30).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
                UserImage(imageUrl = imageUrl)
            }
            item {
                if (showUpdateImageDialog) {
                    user?.let { it ->
                        ShowAlertDialog(
                            user = it,
                            predefinedImageList = predefinedImageList,
                            onImageSelected = { newImage ->
                                userViewModel.viewModelScope.launch {
                                    user?.let { userViewModel.updateProfilePhoto(it, newImage) }
                                }
                                currentUser?.let {
                                    imageUrl = it.photoUser
                                }
                            },
                            onDismiss = { showUpdateImageDialog = false }
                        )
                    }
                }
            }
            // User info
            item {
                ProfileItem(labelId = R.string.username, initialValue = username, userViewModel)
                ProfileItem(labelId = R.string.email, initialValue = email, userViewModel)
                // Change Password Button
                user?.let { ChangePasswordButton(userViewModel, it) }
            }
            // Log Out Button
            item {
                LogoutButton(
                    onLogoutClick = {
                        userViewModel.viewModelScope.launch {
                            userViewModel.logout()
                            navController.navigate(Routes.LOGIN)
                        }
                    }
                )
            }
            // Friends you like
            item {
                Spacer(modifier = Modifier.height(16.dp))
                if (likedAnimals.isNotEmpty()) {
                    LikedAnimalsSection(likedAnimals, animalViewModel, navController)
                } else {
                    Text(
                        text = stringResource(id = R.string.no_liked_friends),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ShowAlertDialog(
    user: User,
    predefinedImageList: List<String>,
    onImageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.new_profile_image),
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                val chunkedImages = predefinedImageList.chunked(5)
                chunkedImages.forEach { rowImages ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowImages.forEach { imagePath ->
                            Image(
                                painter = painterResource(id = getDrawableResourceId(imagePath = imagePath)),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(4.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .clickable {
                                        onImageSelected(imagePath)
                                        onDismiss()
                                    }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun ProfileItem(
    labelId: Int,
    initialValue: String,
    userViewModel: UserViewModel,
    onEditClick: (() -> Unit)? = null,
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedValue by remember { mutableStateOf(initialValue) }
    val originalValue by remember { mutableStateOf(initialValue) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val label = stringResource(id = labelId)
    val user by remember { mutableStateOf(userViewModel.getAuthenticatedUser()) }

    LaunchedEffect(originalValue) {
        editedValue = originalValue
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (labelId != R.string.password) {
                Text(text = label)
            }
            if (!isEditing) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                            isEditing = true
                            onEditClick?.invoke()
                        }
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            isEditing = false
                            if (editedValue.isBlank()) {
                                errorMessage = "Fields cannot be empty"
                                editedValue = originalValue
                            } else {
                                when (labelId) {
                                    R.string.username ->
                                        userViewModel.viewModelScope.launch {
                                            user?.let {
                                                userViewModel.updateUserName(
                                                    user = it,
                                                    newUserName = editedValue
                                                )
                                            }
                                        }

                                    R.string.email ->
                                        userViewModel.viewModelScope.launch {
                                            if (isEmailValid(editedValue)) {
                                                user?.let {
                                                    val updateSuccessful = userViewModel.updateEmail(
                                                        user = it,
                                                        newEmail = editedValue
                                                    )
                                                    if (!updateSuccessful) {
                                                        errorMessage = "Unable to update email. Please try again."
                                                        editedValue = originalValue
                                                    }
                                                }
                                            } else {
                                                errorMessage =
                                                    "Invalid email format.\nExpected format: name@example.com\n"
                                                editedValue = originalValue
                                            }
                                        }
                                }
                            }
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = editedValue,
            onValueChange = { editedValue = it },
            singleLine = true,
            readOnly = !isEditing,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (!errorMessage.isNullOrBlank() || editedValue.isBlank()) Color.Red else Green1,
                unfocusedBorderColor = if (!errorMessage.isNullOrBlank() || editedValue.isBlank()) Color.Red else Green1
            )
        )
    }
    errorMessage?.let { message ->
        CustomSnackbar(
            message = message,
            onDismiss = { errorMessage = null },
        )
    }
}

@Composable
fun LogoutButton(onLogoutClick: () -> Unit) {

    Button(
        onClick = { onLogoutClick() },
        modifier = Modifier,
        colors = ButtonDefaults.buttonColors(Gold),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ExitToApp,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.logout))
        }
    }
}

@Composable
fun ChangePasswordButton(userViewModel: UserViewModel, user: User) {
    var isChangePasswordVisible by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var successfulPasswordChange by remember { mutableStateOf(true) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordChangeButton by remember { mutableStateOf(false) }
    var passwordVisibilityCp by remember { mutableStateOf(false) }
    var passwordVisibilityNp by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            isChangePasswordVisible = true
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(text = stringResource(id = R.string.change_password))
    }
    if (isChangePasswordVisible) {
        AlertDialog(
            onDismissRequest = {
                isChangePasswordVisible = false
                currentPassword = ""
                newPassword = ""
            },
            title = { Text(stringResource(id = R.string.change_password)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                        },
                        label = { Text(stringResource(id = R.string.current_password)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (passwordChangeButton && currentPassword.isBlank()) Color.Red else Green1,
                            unfocusedBorderColor = if (passwordChangeButton && currentPassword.isBlank()) Color.Red else Green1
                        ),
                        visualTransformation = if (passwordVisibilityCp) VisualTransformation.None
                                               else PasswordVisualTransformation(),
                        trailingIcon = {
                            if (currentPassword.isNotBlank()) {
                                IconButton(onClick = { passwordVisibilityCp = !passwordVisibilityCp }) {
                                    Icon(
                                        painter = if (passwordVisibilityCp) painterResource(id = R.drawable.visibility)
                                        else painterResource(id = R.drawable.visibility_off),
                                        contentDescription = if (passwordVisibilityCp) "Hide password" else "Show password",
                                        modifier = Modifier.aspectRatio(0.5f)
                                    )
                                }
                            }
                        },
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                        },
                        label = { Text(stringResource(id = R.string.new_password)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (passwordChangeButton && currentPassword.isBlank()) Color.Red else Green1,
                            unfocusedBorderColor = if (passwordChangeButton && currentPassword.isBlank()) Color.Red else Green1
                        ),
                        visualTransformation = if (passwordVisibilityNp) VisualTransformation.None
                                               else PasswordVisualTransformation(),
                        trailingIcon = {
                            if (newPassword.isNotBlank()) {
                                IconButton(onClick = { passwordVisibilityNp = !passwordVisibilityNp }) {
                                    Icon(
                                        painter = if (passwordVisibilityNp) painterResource(id = R.drawable.visibility)
                                        else painterResource(id = R.drawable.visibility_off),
                                        contentDescription = if (passwordVisibilityNp) "Hide password" else "Show password",
                                        modifier = Modifier.aspectRatio(0.5f)
                                    )
                                }
                            }
                        },
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        passwordChangeButton = true
                        userViewModel.viewModelScope.launch {
                            try {
                                if (isPasswordValid(newPassword)) {
                                    successfulPasswordChange = userViewModel.updatePassword(
                                        user,
                                        currentPassword = currentPassword,
                                        newPassword = newPassword
                                    )
                                    if (successfulPasswordChange) {
                                        isChangePasswordVisible = false
                                    } else {
                                        errorMessage = "Failed to update password."
                                    }
                                } else {
                                    errorMessage = "Invalid password format.\nPassword must " +
                                            "have at least 6 characters, 1 uppercase letter, " +
                                            "and 1 special symbol\n"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Failed to update password."
                                isChangePasswordVisible = false
                            } finally {
                                currentPassword = ""
                                newPassword = ""
                                passwordChangeButton = true
                                isChangePasswordVisible = false
                            }
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.save))
                }
            },
            dismissButton = { passwordChangeButton = false }
        )
    }
    errorMessage?.let { message ->
        CustomSnackbar(
            message = message,
            onDismiss = { errorMessage = null },
        )
    }
    if (errorMessage.isNullOrBlank() && !successfulPasswordChange) {
        CustomSnackbar(
            message = "${stringResource(id = R.string.error_wd_title)}\n${stringResource(id = R.string.error_wd_text)}\n",
            onDismiss = { successfulPasswordChange = true }
        )
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun UserImage(imageUrl: String) {
    Image(
        painter = painterResource(id = getDrawableResourceId(imagePath = imageUrl)),
        contentDescription = null,
        modifier = Modifier
            .size(200.dp)
            .clip(MaterialTheme.shapes.small)
            .zIndex(-1f)
            .padding(bottom = 4.dp)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LikedAnimalsSection(likedAnimals: List<Animal>, animalViewModel: AnimalViewModel, navController: NavController) {
    Text(text = stringResource(id = R.string.liked_friends))
    Column {
        likedAnimals.forEach() { likedAnimal ->
            AnimalCardGallery(navController, likedAnimal)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnimalCardGallery(navController: NavController, likedAnimal: Animal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                navController.navigate(route = Routes.ANIMALINFO + "/" + likedAnimal.id)
            },
        colors = CardDefaults.cardColors(
            containerColor = Green1,
        ),
        border = BorderStroke(2.dp, Gold),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val resourceId = getDrawableResourceId(imagePath = likedAnimal.photoAnimal, true)
                if (resourceId != 0) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = likedAnimal.nameAnimal,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(shape = MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                ) {
                    val age = calculateAge(likedAnimal.birthDate)
                    Text(
                        text = "${likedAnimal.nameAnimal}, ${buildAnAgeText(age, likedAnimal.birthDate, true)}",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 2.dp)
                    )

                    Text(
                        text = "Type: ${likedAnimal.breedAnimal}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = "Sex: ${likedAnimal.sexAnimal}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = "Info: ${likedAnimal.shortInfoAnimal}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
//            Button(
//                onClick = {
//                    navController.navigate(route = Routes.ANIMALINFO + "/" + likedAnimal.id)
//                },
//                modifier = Modifier
//                    .align(Alignment.End),
//                colors = ButtonDefaults.buttonColors(Orange)
//            ) {
//                Text(text = stringResource(id = R.string.details), color = Color.White)
//            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun getDrawableResourceId(imagePath: String, firstImage: Boolean = false): Int {
    val context = LocalContext.current
    var resourceName = imagePath.substringAfterLast("/")

    if (firstImage) {
        resourceName = imagePath.split(", ")[0].trim().substringAfterLast("/")
    }

    val resourceId = context.resources.getIdentifier(
        resourceName.replace(".jpg", ""), "drawable", context.packageName
    )

    return if (resourceId != 0) {
        resourceId
    } else {
        Log.e("UserImage", "Recurso no encontrado para $imagePath")
        0
    }
}

@Composable
fun CreateOutlinedFieldText(textId: Int, newValue: String) {

}
