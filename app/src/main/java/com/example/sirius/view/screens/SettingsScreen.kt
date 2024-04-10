package com.example.sirius.view.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sirius.model.Shelter
import com.example.sirius.navigation.Routes
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Orange
import com.example.sirius.view.components.BarSearch
import com.example.sirius.viewmodel.ShelterViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(shelterViewModel: ShelterViewModel, navController: NavController){
    val textState = remember { mutableStateOf(TextFieldValue("")) }

    var shelters = shelterViewModel.getAllShelters().collectAsState(emptyList()).value

    val showDialogAdd = remember { mutableStateOf(false) }
    var dialogType = ""

    Column {
        BarSearch(state = textState, placeHolder = "Search here..." , modifier = Modifier.fillMaxWidth() )


        val searchedText = textState.value.text


        LazyColumn(modifier = Modifier.padding(10.dp)) {
            items(items = shelters.filter {
                it?.name?.contains(searchedText, ignoreCase = true) ?: false
            }, key = { it?.id ?: "" }) { item ->
                if (item != null) {
                   com.example.sirius.view.screens.Shelter(item = item, shelters.indexOf(item), navController, shelterViewModel )
                }
            }
        }

        AddButton(
            showDialogAdd,
            dialogType,
            Modifier.align(End)
        )

        if( showDialogAdd.value){
            ShelterFormDialog(showDialogAdd = showDialogAdd , shelterViewModel =  shelterViewModel)
        }
    }

}

@Composable
fun Shelter(item: Any, index: Int, navController : NavController, shelterViewModel: ShelterViewModel) {

    val border = if (index % 2 == 0) Green1 else Orange

    when (item) {
        is Shelter -> {
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(route = Routes.ABOUTUS + "/" + item.id)
                    }
                    .padding(4.dp),
                border = BorderStroke(1.dp, border),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column( modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = item.email)
                    }

                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Recibido",
                        tint = Red,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterVertically)
                            .padding(8.dp)
                            .clickable {
                                shelterViewModel.viewModelScope.launch {
                                    shelterViewModel.deleteShelter(item)
                                }

                            }
                    )
                }

            }
        }
    }

}


@SuppressLint("UnrememberedMutableState")
@Composable
fun ShelterFormDialog(
    item : Any? = null,
    showDialogAdd: MutableState<Boolean>,
    shelterViewModel: ShelterViewModel
) {

    var name by mutableStateOf("")
    var about_us by mutableStateOf("")
    var latitude by mutableStateOf("")
    var longitude by mutableStateOf("")
    var schedule by mutableStateOf("")
    var shelters_data by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")


    var editedName by remember {
        mutableStateOf((item as? Shelter)?.name ?: "")
    }
    var editedAbout_us by remember {
        mutableStateOf((item as? Shelter)?.aboutUs ?: "")
    }
    var editedLatitude by remember {
        mutableStateOf(((item as? Shelter)?.location ?: "").substringBefore(";"))
    }

    var editedLongitude by remember {
        mutableStateOf(((item as? Shelter)?.location ?: "").substringAfter(";"))
    }

    var editedSchedule by remember {
        mutableStateOf((item as? Shelter)?.schedule ?: "")
    }
    var editedShelters_data by remember {
        mutableStateOf((item as? Shelter)?.sheltersData ?: "")
    }
    var editedEmail by remember {
        mutableStateOf((item as? Shelter)?.email ?: "")
    }
    var editedPhone by remember {
        mutableStateOf((item as? Shelter)?.phone ?: "")
    }



    AlertDialog(
        onDismissRequest = { showDialogAdd.value = false },
        title = { Text("Agregar Nuevo") },
        text = {
            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                item {
                    CustomTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = "Shelter's name"
                    )
                }
                item {
                    CustomTextField(
                        value = editedAbout_us,
                        onValueChange = { editedAbout_us = it },
                        label = "About us"
                    )
                }

                item {
                    CustomTextField(
                        value = editedLatitude,
                        onValueChange = { editedLatitude = it },
                        label = "Latitude"
                    )

                    CustomTextField(
                        value = editedLongitude,
                        onValueChange = { editedLongitude = it },
                        label = "Longitude"
                    )
                }
                item {
                    CustomTextField(
                        value = editedSchedule,
                        onValueChange = { editedSchedule = it },
                        label = "Schedule"
                    )
                }
                item {
                    CustomTextField(
                        value = editedShelters_data,
                        onValueChange = { editedShelters_data = it },
                        label = "Shelters data"
                    )
                }
                item {
                    CustomTextField(
                        value = editedEmail,
                        onValueChange = { editedEmail = it },
                        label = "Email"
                    )
                }
                item {
                    CustomTextField(
                        value = editedPhone,
                        onValueChange = { editedPhone = it },
                        label = "Phone"
                    )
                }

            }
        },

        confirmButton = {
            Button(
                onClick = {
                    val shelter = Shelter(editedName, editedAbout_us, "$editedLatitude;$editedLongitude",editedSchedule, editedShelters_data, editedEmail, editedPhone )
                    if(item == null){

                        shelterViewModel.viewModelScope.launch {
                            shelterViewModel.insertShelter(
                                shelter
                            )
                        }
                    }else {

                        val updatedShelter = (item as Shelter)?.copy(item.id, editedName, editedAbout_us, "$editedLatitude;$editedLongitude",editedSchedule, editedShelters_data, editedEmail, editedPhone)

                        println(shelter.toString())
                        shelterViewModel.viewModelScope.launch {
                            if (updatedShelter != null) {
                                shelterViewModel.updateShelter(updatedShelter)
                            }
                        }
                    }


                    showDialogAdd.value = false
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            Button(
                onClick = { showDialogAdd.value = false }
            ) {
                Text("Cancelar")
            }
        }
    )
}


@SuppressLint("UnrememberedMutableState")
@Composable
private fun ShelterFormFields(shelterViewModel: ShelterViewModel) {

    var name by mutableStateOf("")
    var about_us by mutableStateOf("")
    var latitude by mutableStateOf("")
    var longitude by mutableStateOf("")
    var schedule by mutableStateOf("")
    var shelters_data by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")




    /*
    val shelter = Shelter(
        name = name,
        aboutUs = about_us,
        location = (latitude.toDoubleOrNull() ?: 0.0).toString() + ";" + (longitude.toDoubleOrNull() ?: 0.0).toString(),
        schedule = schedule,
        sheltersData = shelters_data,
        email = email,
        phone = phone
    )
    */

}
