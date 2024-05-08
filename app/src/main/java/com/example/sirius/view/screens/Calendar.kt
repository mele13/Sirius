
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import com.example.sirius.model.Event
import com.example.sirius.model.TypeUser
import com.example.sirius.model.User
import com.example.sirius.tools.parseDateStringToLong
import com.example.sirius.view.components.DatePickerItem
import com.example.sirius.view.components.EventCard
import com.example.sirius.viewmodel.EventViewModel
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DiscouragedApi", "SuspiciousIndentation")
@Composable
fun CalendarScreen(eventViewModel: EventViewModel, userViewModel: UserViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var date: String
    date = ""

    val events by eventViewModel.getEvents().collectAsState(initial = null)
    val user by remember { mutableStateOf(userViewModel.getAuthenticatedUser()) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            SectionTitle("Calendar")
        }
        item {
            Button(onClick = { showDialog = true }) {
                Text(text = "Create event")
                if (showDialog) {
                    user?.let {
                        CreateEventDialog(onDismiss = { showDialog = false },eventViewModel,
                            it
                        )
                    }
                }
            }
        }

        item {
            DatePickerItem(
                state = System.currentTimeMillis(),
                onDateSelected = { dateAux -> date = dateAux
                    events?.let { user?.let { it1 -> ShowEventsbyCalendar(it, it1,date, eventViewModel) } }
                },
                title = "Entry Date"
            )
        }
        item{
            Text("Events")
        }

        }
        }




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowEventsbyCalendar(events: List<Event>, user: User,fecha : String, eventViewModel: EventViewModel) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        events.forEach { event ->
              if (parseDateStringToLong(event.dateEvent) >= parseDateStringToLong(fecha)) {
                  /*
                    Text(
                        text = "Titulo: ${event.titleEvent}",
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = event.descriptionEvent,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Para: ${event.dateEvent}",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "----------------------"
                    )

                   */
                  EventCard(event,eventViewModel,user)
                }
            }
    }
}
@Composable
fun CreateEventDialog(onDismiss: () -> Unit,eventViewModel: EventViewModel,user: User) {
    var newDate: String
    newDate = ""
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf("") }
    val itemsAOW = listOf("cite", "worker", "volunteer")
    val itemsV = listOf("cite", "volunteer")

    Dialog(onDismissRequest = ({ onDismiss() })) {
        Surface(
            modifier = Modifier
                .width(300.dp)
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(text = "Create event")
                DatePickerItem(
                    state = selectedDate,
                    onDateSelected = { date ->
                        newDate = date
                    },
                    title = "Select a date for the event"
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event title") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                        }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description of the event") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Ocultar el teclado cuando se presiona "Done"
                            // No es necesario para Compose Desktop
                            //hideKeyboard(context)
                        }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Select an option:")
                val expanded = remember { mutableStateOf(false) }

                IconButton(
                    onClick = { expanded.value = !expanded.value },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null
                    )
                }
                Box {

                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        if (user.role != TypeUser.volunteer) {
                            itemsAOW.forEach { item ->
                                DropdownMenuItem(
                                    {
                                        Text(text = item)
                                    }, onClick = {
                                        selectedItem = item
                                        expanded.value = false
                                    })
                            }
                        } else {
                            itemsV.forEach { item ->
                                DropdownMenuItem(
                                    {
                                        Text(text = item)
                                    }, onClick = {
                                        selectedItem = item
                                        expanded.value = false
                                    })
                            }
                        }
                        Text(selectedItem)
                    }

                    Row() {
                        Button(onClick = { onDismiss()}) {
                            Text(text = "Cancel")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            val newEvent = Event(
                                id = 0,
                                titleEvent = title,
                                descriptionEvent = description,
                                dateEvent = newDate,
                                UserID = user.id.toString(),
                                eventType = eventViewModel.stringToTypeEvent(selectedItem)
                            )
                            eventViewModel.viewModelScope.launch {
                                eventViewModel.insertEvent(newEvent)
                            }
                            onDismiss()
                        }) {

                            Text(text = "Accept")
                        }

                    }
                }
            }
        }
    }
}






