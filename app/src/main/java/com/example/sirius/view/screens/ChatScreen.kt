package com.example.sirius.view.screens


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Send

import androidx.compose.material3.Card
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.android.InternalPlatformTextApi

import androidx.navigation.NavHostController

import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.sirius.model.User
import com.example.sirius.navigation.Routes
import com.example.sirius.viewmodel.UserViewModel


@Composable
fun ChatScreen(NavController: NavHostController,userViewModel: UserViewModel){

    var userList by remember { mutableStateOf<List<User?>>(emptyList()) }
    val user by remember { mutableStateOf(userViewModel.getAuthenticatedUser()) }

    LaunchedEffect(Unit) {
        try {
            userList = userViewModel.getAllUsers()!!
        } catch (e: Exception) {
            Log.e("Error : ", "Error al acceder a la BBDD", e)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        Column(modifier = Modifier.fillMaxHeight()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(10.dp, top = 15.dp)) {

                user?.let { UserEachRow(person = it) { NavController.navigate(route = Routes.PROFILE)} }

                LazyColumn(modifier = Modifier.padding(10.dp)) {
                    items(items = userList, key = { it?.id ?: "" }) { item ->
                        if (item != null) {
                            UserEachRow(person = item) {

                            }
                        }
                    }
                }

            }

        }

    }



}


@OptIn(InternalPlatformTextApi::class)
@Composable
fun UserEachRow(
    person: User,
   // unseenMessages: List<String>,
    onClick: () -> Unit = {}
) {

   // val UserViewModel : UserViewModel = hiltViewModel()
    val imageUrlState = remember { mutableStateOf("") }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(4.dp)

    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {

            UserImage(imageUrl = person.photoUser, 50.dp)

            Text(
                text = person.username ?: "",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .align(CenterVertically)
            )





        }


    }
}


@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.noRippleEffect(onClick: () -> Unit) = composed {
    clickable(
        interactionSource = MutableInteractionSource(),
        indication = null
    ) {
        onClick()
    }
}

/*

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Messages(recipientUserId: String, navHostController: NavController, chatViewModel: ChatViewModel = hiltViewModel()) {

    val UserViewModel : UserViewModel = hiltViewModel()
    // var user: UserData? = null
    val userState = remember { mutableStateOf<UserData?>(null) }

    LaunchedEffect(UserViewModel) {
        val user = UserViewModel.getUserById(recipientUserId)

        userState.value = user
    }
    Log.i("USER",userState.value?.profilePictureUrl.toString())


    initRecipientUserId(recipientUserId, chatViewModel)

    val message: String by chatViewModel.message.observeAsState(initial = "")
    val messages: List<Map<String, Any>> by chatViewModel.messages.observeAsState(
        initial = emptyList<Map<String, Any>>().toMutableList()
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        //  contentAlignment = Alignment.TopCenter, // Alinea el texto en la parte superior y central
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            //  verticalArrangement = Arrangement.Bottom
        ) {

            Box(
                //modifier = Modifier.fillMaxSize()
                contentAlignment = Alignment.TopCenter
            ) {
                userState.value?.let { Recipient(userData = it, navController = navHostController) }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(weight = 0.85f, fill = true),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                reverseLayout = true
            ) {
                items(messages) { message ->
                    val isCurrentUser = message[Constants.IS_CURRENT_USER] as Boolean

                    SingleMessage(
                        message = message[Constants.MESSAGE].toString(),
                        isCurrentUser = isCurrentUser
                    )
                }

            }
            Box(
                // modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        chatViewModel.updateMessage(it)
                    },
                    label = {
                        Text(
                            "Escribe tu mensaje"
                        )
                    },
                    maxLines = 5,
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 1.dp)
                        .fillMaxWidth(),
                    // .weight(weight = 0.09f, fill = true),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                chatViewModel.addMessage(recipientUserId)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Boton de enviar",
                                tint = Violet
                            )
                        }
                    }
                )
            }
        }
    }


*/





