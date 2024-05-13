package com.example.sirius.view.screens


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.sirius.R
import com.example.sirius.model.Chat
import com.example.sirius.model.TypeUser
import com.example.sirius.model.User
import com.example.sirius.navigation.Routes
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Green3
import com.example.sirius.ui.theme.Orange
import com.example.sirius.view.components.FloatingButton
import com.example.sirius.view.components.SingleMessage
import com.example.sirius.viewmodel.ChatViewModel
import com.example.sirius.viewmodel.UserViewModel
import java.util.Locale

@SuppressLint("DiscouragedApi", "CoroutineCreationDuringComposition")
@Composable
fun ChatScreen(navController: NavHostController,
               chatViewModel: ChatViewModel,
               userViewModel: UserViewModel,
               filteredShelters: ArrayList<Int>)
{
    var userList by remember { mutableStateOf<List<User?>>(emptyList()) }
    var ownerList by remember { mutableStateOf<List<User?>>(emptyList()) }
    var workerList by remember { mutableStateOf<List<User?>>(emptyList()) }
    var adminList by remember { mutableStateOf<List<User?>>(emptyList()) }
    var usersList by remember { mutableStateOf<List<User?>>(emptyList()) }


    val user by remember { mutableStateOf(userViewModel.getAuthenticatedUser()) }


    LaunchedEffect(Unit) {
        try {

            userList = userViewModel.getUserWithRoleUser()
            if(user?.role == TypeUser.admin){
                ownerList = userViewModel.getOwner()

            }else if( user?.role == TypeUser.owner || user?.role == TypeUser.volunteer || user?.role == TypeUser.worker){
                workerList = user?.shelterId?.let { userViewModel.getUsersFromMyShelter(it, user!!.id) }!!
                adminList = userViewModel.getAdmin()
            }else{
                usersList = userViewModel.getUsersFromMyShelter(filteredShelters[0], user!!.id)

            }





        } catch (e: Exception) {
            Log.e("Error : ", "Error when accessing the database", e)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = BottomEnd
    ){

        if(user?.role ?: "" == TypeUser.worker || user?.role ?: "" == TypeUser.volunteer){
            FloatingButton(Icons.Outlined.Notifications, Modifier.align(BottomEnd)) {
                navController.navigate(Routes.ADOPTION)
            }
        }

    }

    val unseenMessagesState = chatViewModel.getUnseenMessages().collectAsState(initial = emptyList())

    val unseenMessages by remember {
        unseenMessagesState
    }


    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(10.dp)) {

                if(user?.role == TypeUser.admin){
                    Text(text = "Shelters Owners")
                    Chats(
                        userList = ownerList,
                        user = user,
                        unseenMessages = unseenMessages,
                        chatViewModel = chatViewModel,
                        userViewModel = userViewModel,
                        navController = navController,
                        modifier = Modifier.  height(300.dp)
                    )
                    Text(text = "Users")

                } else if( user?.role == TypeUser.volunteer || user?.role == TypeUser.worker){
                    Text(text = "Shelters Staff")

                    Chats(
                        userList = workerList,
                        user = user,
                        unseenMessages = unseenMessages,
                        chatViewModel = chatViewModel,
                        userViewModel = userViewModel,
                        navController = navController,
                        modifier = Modifier. height(300.dp)

                    )

                    Text(text = "Users")

                }else if( user?.role == TypeUser.owner){
                    Text(text = "Shelters Staff")

                    Chats(
                        userList = workerList + adminList,
                        user = user,
                        unseenMessages = unseenMessages,
                        chatViewModel = chatViewModel,
                        userViewModel = userViewModel,
                        navController = navController,
                        modifier = Modifier. height(300.dp)

                    )

                    Text(text = "Users")

                }

                if(user?.role == TypeUser.user){
                    Chats(
                        userList = usersList,
                        user = user,
                        unseenMessages = unseenMessages,
                        chatViewModel = chatViewModel,
                        userViewModel = userViewModel,
                        navController = navController,
                        modifier = Modifier. fillMaxSize()

                    )
                }else{
                    Chats(
                        userList = userList,
                        user = user,
                        unseenMessages = unseenMessages,
                        chatViewModel = chatViewModel,
                        userViewModel = userViewModel,
                        navController = navController,
                        modifier = Modifier. fillMaxSize()

                    )
                }

            }
        }
    }
}

@Composable
fun Chats(userList : List<User?>, user: User?, unseenMessages: List<Int>, chatViewModel: ChatViewModel,userViewModel: UserViewModel, navController: NavHostController, modifier: Modifier){
    LazyColumn(modifier = modifier
        .padding(10.dp)
        .then(modifier)) {
        items(items = userList, key = { it?.id ?: "" }) { item ->
            if (item != null) {
                var lastMessage by remember(item.id) { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    try {
                        val chatID = user?.let { chatViewModel.generateChatId(it.id, item.id) }
                        lastMessage = chatID?.let { chatViewModel.getLastMessage(it) }
                    } catch (e: Exception) {
                        Log.e("Error: ", "Chat ID not found", e)
                    }
                }
                UserEachRow(person = item, lastMessage = lastMessage, unseenMessages = unseenMessages, userViewModel = userViewModel, chatViewModel = chatViewModel) {
                    navController.navigate(Routes.CHAT + "/${item.id}")
                }
            }
        }
    }
}




@Composable
fun MyProfile(
    person: User,
    onClick: () -> Unit = {}
)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            UserImage(imageUrl = person.photoUser, 40.dp,
                Modifier
                    .clip(shape = CircleShape)
                    .size(40.dp))

            Text(
                text  = person.username.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .align(CenterVertically)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .drawBehind {
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1f
                    )
                }
        )
    }
}

@Composable
fun UserEachRow(
    person: User,
    lastMessage: String?,
    unseenMessages: List<Int>,
    userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = CenterVertically) {


            UserImage(
                imageUrl = person.photoUser,
                40.dp,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(8.dp))
                    .size(40.dp)
            )


            Column {
                
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = person.username.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        },
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier
                            .padding(start = 10.dp)
                        //.align(CenterVertically)
                    )
                    
                    if(person.role == TypeUser.admin){
                        Image(
                            painter = painterResource(id = R.drawable.admin_icon),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    }else if(person.role == TypeUser.worker){
                        Image(
                            painter = painterResource(id = R.drawable.worker_icon),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)

                        )
                    } else if(person.role == TypeUser.volunteer){
                        Image(
                            painter = painterResource(id = R.drawable.volunteer_icon),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)

                        )
                    }else if(person.role == TypeUser.owner){
                        Image(
                            painter = painterResource(id = R.drawable.owner_icon),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)

                        )
                    }
                }
                

                Content(lastMessage, person, unseenMessages, userViewModel, chatViewModel = chatViewModel)


            }


        }

        DrawLine()

    }
}


@Composable
fun Content(lastMessage: String?, person: User, unseenMessages: List<Int>, userViewModel: UserViewModel, chatViewModel: ChatViewModel){

    val chatId = chatViewModel.getChatId().collectAsState(initial = emptyList())
    val authenticatedUserId = userViewModel.getAuthenticatedUser()?.id.toString()

    Row(Modifier.fillMaxSize()) {
        if (lastMessage != null) {
            Text(
                text = lastMessage.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                },
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Green1
                ),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            )
        }


        val chatID = chatViewModel.generateChatId(authenticatedUserId.toInt(), person.id)

        if(chatID in chatId.value && person.id in unseenMessages ){
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Recibido",
                    tint = Green3,
                    modifier = Modifier.size(24.dp)
                )
        }


    }
}
@Composable
fun DrawLine(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .drawBehind {
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1f
                )
            }
    )
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun Messages(navController: NavController, recipientUserId: Int, userViewModel : UserViewModel, chatViewModel : ChatViewModel) {

    val userState = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userViewModel) {
        val user = userViewModel.getUserById(recipientUserId)

        userState.value = user
    }

    initRecipientUserId(recipientUserId, chatViewModel)

    val message: String by chatViewModel.message.observeAsState(initial = "")
    val messages: List<Chat> by chatViewModel.messages.observeAsState(
        initial = emptyList()
    )

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            userState.value?.let { MyProfile(person = it) { navController.navigate(route = Routes.PROFILE) } }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(weight = 0.85f, fill = true),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                reverseLayout = true
            ) {
                items(messages) { message ->
                    SingleMessage(
                        message = message.message,
                        isCurrentUser = message.sentBy == (userViewModel.getAuthenticatedUser()?.id
                            ?: 0)
                    )
                }
            }
            Box(
                //  modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        chatViewModel.updateMessage(it)
                        // message = it
                    },
                    label = {
                        Text(
                            "Write Something..."
                        )
                    },
                    maxLines = 5,
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 1.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                chatViewModel.addMessage(recipientUserId)
                                chatViewModel.updateMessage("")
                            },

                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send button",
                                tint = Orange
                            )
                        }
                    }
                )
            }
        }
    }
}



fun initRecipientUserId(recipientUserId: Int, chatViewModel: ChatViewModel) {
    chatViewModel.initRecipientUserId(recipientUserId)
}