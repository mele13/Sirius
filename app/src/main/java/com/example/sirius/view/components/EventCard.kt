package com.example.sirius.view.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import com.example.sirius.model.Event
import com.example.sirius.model.TypeEvent
import com.example.sirius.model.TypeUser
import com.example.sirius.model.User
import com.example.sirius.tools.parseDateStringToLong
import com.example.sirius.viewmodel.EventViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DiscouragedApi", "CoroutineCreationDuringComposition")
@Composable
fun EventCard(event: Event, eventViewModel: EventViewModel,user: User) {
    var showDialog by remember { mutableStateOf(false) }
    var permission by remember { mutableStateOf(false)}
    var dialogActivated by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if(event.eventType == TypeEvent.volunteer){
                if(user.role != TypeUser.volunteer || user.id.toString() == event.UserID){
                    permission = true
                }
            }
            if(event.eventType == TypeEvent.worker){
                if(user.role == TypeUser.admin || user.role == TypeUser.owner){
                    permission = true
                }
                if(user.role == TypeUser.worker && user.id.toString() == event.UserID)     {
                    permission = true
                    }
                }
            if(event.eventType == TypeEvent.cite){
                if(user.role == TypeUser.admin || user.role == TypeUser.owner){
                    permission = true
                }
                if(user.id.toString() == event.UserID)     {
                    permission = true
                }
            }
            if(permission) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    IconButton(onClick = {
                        //confirmDialog(event,eventViewModel)
                        dialogActivated = true;
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Eliminar"
                        )
                    }
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Eliminar"
                        )
                    }
                    if (showDialog) {
                        EditEventDialog(
                            onDismiss = { showDialog = false },
                            eventViewModel = eventViewModel,
                            user = user,
                            event = event
                        )
                    }

                    if (dialogActivated) {
                        ConfirmDialog(
                            onDismiss = { dialogActivated = false },
                            event,
                            eventViewModel
                        )
                    }
                }
            }
            Text(text = event.titleEvent)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = event.descriptionEvent)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = event.dateEvent)
        }

    }
}

@Composable
fun EditEventDialog(onDismiss: () -> Unit,eventViewModel: EventViewModel,user: User,event: Event) {
    val context = LocalContext.current
    var newFecha: String
    newFecha = "";
    var unit: Unit
    var selectedDate by remember { mutableStateOf(parseDateStringToLong(event.dateEvent)) }
    var titulo by remember { mutableStateOf(event.titleEvent) }
    var description by remember { mutableStateOf(event.descriptionEvent) }
    var selectedItem by remember { mutableStateOf(event.eventType.toString()) }
    val items = listOf("cite", "workeer", "volunteer")

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
                Text(text = "Create an event")
                DatePickerItem(
                    state = selectedDate,
                    onDateSelected = { date ->
                        newFecha = date
                    },
                    title = "Select a date for the event"
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Title of the event") },
                    modifier = Modifier.fillMaxWidth(),
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
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description of the event") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp), // Ajuste de altura a 90dp (3 líneas de altura),
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
                        items.forEach { item ->
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


                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    event.titleEvent = titulo
                    event.descriptionEvent = description
                    event.dateEvent = newFecha
                    event.eventType = eventViewModel.stringToTypeEvent(selectedItem)

                    eventViewModel.viewModelScope.launch {
                        eventViewModel.updateEvent(event)
                    }
                    onDismiss() }) {

                    Text(text = "Accept")
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ConfirmDialog(onDismiss: () -> Unit, event: Event, eventViewModel: EventViewModel) {


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



                    Text("Are you sure do you wanna delete this event?")
                Row(  modifier = Modifier
                    .padding(16.dp),
                ) {


                    Button(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }

                    Button(onClick = {
                        eventViewModel.viewModelScope.launch {
                            eventViewModel.deleteEvent(event)
                        }
                    }) {
                        Text("Accept")
                    }
                }

            }
        }
    }
}