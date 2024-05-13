package com.example.sirius.view.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sirius.model.Animal
import com.example.sirius.model.Event
import com.example.sirius.model.News
import com.example.sirius.model.SectionType
import com.example.sirius.tools.parseDateStringToLong
import com.example.sirius.tools.stringToEnumTypeAnimal
import com.example.sirius.ui.theme.Orange
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.ChatViewModel
import com.example.sirius.viewmodel.EventViewModel
import com.example.sirius.viewmodel.NewsViewModel
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun DeleteDialog(
    onDismissRequest: () -> Unit,
    titleDialog: String,
    item: Any
) {

    val eventViewModel : EventViewModel = viewModel(factory = EventViewModel.factory)
    val animalViewModel : AnimalViewModel = viewModel(factory = AnimalViewModel.factory)
    val newsViewModel : NewsViewModel = viewModel(factory = NewsViewModel.factory)

    AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = titleDialog)
            },
            text = {
                Text(text = "Are you sure?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismissRequest()
                        if (item is Animal) {
                            animalViewModel.viewModelScope.launch {
                                animalViewModel.deleteAnimal(
                                    animal = item
                                )
                            }
                        } else if (item is News) {
                            newsViewModel.viewModelScope.launch {
                                newsViewModel.deleteNews(
                                    newNew = item
                                )
                            }
                        } else if (item is Event){
                            eventViewModel.viewModelScope.launch {
                                eventViewModel.deleteEvent(item)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Orange)

                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(Orange)

                ) {
                    Text("Cancel")
                }
            }
        )
}

@Composable
fun OutlinedIcon(icon: ImageVector, modifier: Modifier, onClick: (() -> Unit)? = null) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.White,
        modifier = modifier
            .then(Modifier.size(25.dp))
            .then(Modifier.offset(y = 1.dp))
    )
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.Black,
        modifier = modifier
            .then(Modifier.clickable {
                onClick?.invoke()
            })
            .then(Modifier.size(25.dp))
    )
}

@Composable
fun AdoptAnAnimal(item: Animal, chatViewModel: ChatViewModel, userViewModel: UserViewModel, onDismiss: () -> Unit) {
    val randomUser by userViewModel.getRandomUser().collectAsState(initial = null)
    val eventViewModel: EventViewModel = viewModel(factory = EventViewModel.factory)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "You are about to start the process of adopting ${item.nameAnimal}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        text = {
            Text(
                text = "As a rule of the shelter, before you can adopt  an animal, we have to conduct a short interview. Would you like to contact us?",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    /*
                    eventViewModel.insertEvent(
                        Event(

                    ))

                     */
                 //   randomUser?.let { chatViewModel.addMessageAdoption(it.id, item) }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(Orange),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Text("Contact with the shelter")
            }
        },
        dismissButton = {},

    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnimalFormDialog(
    showDialogAdd: MutableState<Boolean>,
    animalFormState: AnimalFormState,
    typeList: List<String>,
    sectionType: SectionType,
    animalFormData: AnimalFormData? =  null,
    isEdit : Boolean,

    ) {
    val animalViewModel: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)

    val dateState = System.currentTimeMillis()


    var formData = animalFormData ?: AnimalFormData(
        0,
        "", "", "",
        waitingAdoption = 0,
        fosterCare = 0,
        shortInfo = "",
        longInfo = "",
        breed = "",
        type = "",
        entryDate = "",
        photoAnimal = "",
        inShelter = 0,
        lost = 0,
        shelter_id = 0
    )

    if (animalFormData != null) {
        formData = animalFormData
    }

    if (sectionType ==  SectionType.LOST) {
        animalFormState.lost = 1
    } else if (sectionType ==  SectionType.IN_SHELTER){
        animalFormState.inShelter = 1
    }

    AlertDialog(
        onDismissRequest = { showDialogAdd.value = false },
        title = { Text("Add New") },
        text = {
            formData = animalFormFields(
                animalFormState = animalFormState,
                dateState = dateState,
                animalViewmodel = animalViewModel,
                typeList = typeList,
                sectionType = sectionType,
                animalFormData = formData,
                isEdit
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isEdit){
                        showDialogAdd.value = false
                        animalViewModel.viewModelScope.launch {
                            stringToEnumTypeAnimal(formData.type)?.let {
                                Animal(formData.id, formData.name, formData.birthDate, formData.sex, formData.waitingAdoption, formData.fosterCare, formData.shortInfo, formData.longInfo, formData.breed,
                                    it, formData.entryDate, formData.photoAnimal, formData.inShelter, formData.lost,formData.shelter_id                                )
                            }?.let { animalViewModel.updateAnimal(it) }
                            animalFormState.clear()
                        }
                    } else {
                        if (formData.photoAnimal.isEmpty()){
                            formData.photoAnimal = "res/drawable/user_image1.jpg"
                        }
                        showDialogAdd.value = false
                        animalViewModel.viewModelScope.launch {
                            stringToEnumTypeAnimal(formData.type)?.let {
                                Animal(0, formData.name, formData.birthDate, formData.sex, formData.waitingAdoption, formData.fosterCare, formData.shortInfo, formData.longInfo, formData.breed,
                                    it, formData.entryDate, formData.photoAnimal, formData.lost, formData.inShelter,formData.shelter_id
                                )
                            }?.let { animalViewModel.insertAnimal(it) }
                            animalFormState.clear()
                        }
                    }

                },
                colors = ButtonDefaults.buttonColors(Orange),

                        enabled = animalFormState.name.isNotEmpty()  &&
                        animalFormState.sex.isNotEmpty() &&
                        animalFormState.shortInfo.isNotEmpty() &&
                        animalFormState.longInfo.isNotEmpty() &&
                        animalFormState.breed.isNotEmpty() &&
                        animalFormState.typeAnimal.isNotBlank()


            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showDialogAdd.value = false
                    animalFormState.clear()
                },
                colors = ButtonDefaults.buttonColors(Orange)

            ) {
                Text("Cancel")
            }
        },
    )
}

@Composable
fun NewsFormDialog(
    showDialogAdd: MutableState<Boolean>,
    newsFormState: NewsFormState,
    sectionType: SectionType,
    newsFormData: NewsFormData? =  null,
    isEdit : Boolean,
) {


    val newsViewmodel: NewsViewModel = viewModel(factory = NewsViewModel.factory)

    val dateState = System.currentTimeMillis()

    var formData = newsFormData ?: NewsFormData(
        0,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        0)

    if (newsFormData != null) {
        formData = newsFormData
    }

    if (sectionType ==  SectionType.GOOD_NEWS) {
        newsFormState.goodNews = 1
    } else if (sectionType ==  SectionType.WHATS_NEW){
        newsFormState.goodNews = 0
    }

    AlertDialog(
        onDismissRequest = { showDialogAdd.value = false },
        title = { Text("Add New") },
        text = {
            formData = newsFormFields(
                state = dateState,
                newsFormState = newsFormState,
                newsFormData,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isEdit){
                        showDialogAdd.value = false
                        newsViewmodel.viewModelScope.launch {
                            newsViewmodel.updateNew(News(formData.id, formData.title, formData.shortInfo, formData.longInfo, formData.publishedDate, formData.createdAt, formData.untilDate, formData.photoNews, formData.goodNews,formData.shelter_id))
                            newsFormState.clear()
                        }
                    } else {
                        if (formData.photoNews.isEmpty()){
                            formData.photoNews = "res/drawable/user_image1.jpg"
                        }
                        showDialogAdd.value = false
                        newsViewmodel.viewModelScope.launch {
                            newsViewmodel.insertNews(News(0, formData.title, formData.shortInfo, formData.longInfo, formData.publishedDate, formData.createdAt, formData.untilDate, formData.photoNews, formData.goodNews,formData.shelter_id))
                            newsFormState.clear()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(Orange),

                        enabled = newsFormState.title.isNotEmpty() &&
                        newsFormState.shortInfo.isNotEmpty() &&
                        newsFormState.longInfo.isNotEmpty()
            ) {
                Text("Add")

            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showDialogAdd.value = false
                    newsFormState.clear()
                },
                colors = ButtonDefaults.buttonColors(Orange)

            ) {
                Text("Cancel")
            }
        }
    )
}



@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EditEventDialog(showDialogEdit: MutableState<Boolean>,event: Event) {
    val eventViewModel : EventViewModel = viewModel(factory = EventViewModel.factory)

    var newDate: String
    newDate = ""
    var selectedDate by remember { mutableStateOf(parseDateStringToLong(event.dateEvent)) }
    var title by remember { mutableStateOf(event.titleEvent) }
    var description by remember { mutableStateOf(event.descriptionEvent) }
    var selectedItem by remember { mutableStateOf(event.eventType.toString()) }
    val items = listOf("cite", "worker", "volunteer")

    AlertDialog(
        onDismissRequest = { showDialogEdit.value = false },
        title = { Text("Create an event", textAlign = TextAlign.Center) },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
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
                        .height(90.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                        }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))


                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically){
                    Text("Select an option:")
                    val expanded = remember { mutableStateOf(false) }
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
                        Text(selectedItem, Modifier.padding(start = 4.dp))
                        IconButton(
                            onClick = { expanded.value = !expanded.value },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null
                            )
                        }

                }



            }
        },
        confirmButton = {
            Button(
                onClick = {
                    event.titleEvent = title
                    event.descriptionEvent = description
                    event.dateEvent = newDate
                    event.eventType = eventViewModel.stringToTypeEvent(selectedItem)

                    eventViewModel.viewModelScope.launch {
                        eventViewModel.updateEvent(event)
                    }
                },
                colors = ButtonDefaults.buttonColors(Orange),


            ) {
                Text("Add")

            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showDialogEdit.value = false
                },
                colors = ButtonDefaults.buttonColors(Orange)

            ) {
                Text("Cancel")
            }
        }
    )
}
