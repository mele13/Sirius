package com.example.sirius.view.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.model.Shelter
import com.example.sirius.model.TypeUser
import com.example.sirius.model.User
import com.example.sirius.navigation.Routes
import com.example.sirius.navigation.Routes.HOME
import com.example.sirius.tools.Location
import com.example.sirius.tools.getZoneNames
import com.example.sirius.tools.parseLocationsFlow
import com.example.sirius.ui.theme.Gold
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Orange
import com.example.sirius.view.components.BarSearch
import com.example.sirius.view.components.CustomTextField
import com.example.sirius.view.components.FloatingButton
import com.example.sirius.view.components.StatusCheckbox
import com.example.sirius.viewmodel.ShelterViewModel
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

var filteredShelter = arrayListOf(0)
@Composable
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState",
    "UnrememberedMutableState"
)
fun SettingsScreen(shelterViewModel: ShelterViewModel, navController: NavController, userViewModel: UserViewModel, edit: Boolean){
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val shelters = shelterViewModel.getAllShelters().collectAsState(emptyList()).value
    val sheltersAllowAdoption = shelterViewModel.getSheltersAllowDonations().collectAsState(emptyList()).value

    val user = userViewModel.getAuthenticatedUser()
    val showDialogAdd = remember { mutableStateOf(false) }
    val provisionalArray = remember { mutableStateOf(arrayListOf<Int>()) }

    val isSelectedList = remember { mutableStateListOf<Boolean>() }
    val lastIsSelectedState = remember { mutableStateOf(isSelectedList.any { it }) }

    val allowAdoption = remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val locationsFlow: Flow<List<String>> = shelterViewModel.getLocation()
    val parsedLocationsFlow: Flow<List<Location>> = parseLocationsFlow(locationsFlow)

    val context = LocalContext.current


    var locations by remember { mutableStateOf(emptyList<Location>()) }
    LaunchedEffect(Unit) {
        parsedLocationsFlow.collect { parsedLocations ->
            locations = parsedLocations
        }
    }

    val zoneNames = getZoneNames(context, locations)

    var selectedLocation: Location? by remember { mutableStateOf(null) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!edit) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.filter
                    ),
                    contentDescription = stringResource(id = R.string.filter),
                    modifier = Modifier
                        .clickable {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }
                )
            }
            BarSearch(
                state = textState,
                placeHolder = "Search here...",
                modifier = Modifier.fillMaxWidth()
            )
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text("Filters", modifier = Modifier.padding(16.dp))
                    Divider()
                    StatusCheckbox(
                        labelText = "Allow Adoption",
                        checked = allowAdoption.value,
                        onCheckedChange = { newValue ->
                            allowAdoption.value = newValue
                            if (newValue) {
                                provisionalArray.value.clear()
                                shelterViewModel.getSheltersAllowDonations()
                            } else {
                                shelterViewModel.getAllShelters()
                            }
                        }
                    )
                    selectedLocation = zoneDropdown(zoneNames, provisionalArray, locations)
                }
            },
            gesturesEnabled = false
        ) {
            val sheltersToDisplay = when {
                allowAdoption.value && selectedLocation != null -> {
                    val locationShelters = shelterViewModel.getSheltersLocation(
                        selectedLocation!!.latitude.toString(),
                        selectedLocation!!.longitude.toString()
                    ).collectAsState(emptyList()).value
                    sheltersAllowAdoption + locationShelters
                }
                allowAdoption.value -> sheltersAllowAdoption
                selectedLocation != null -> shelterViewModel.getSheltersLocation(
                    selectedLocation!!.latitude.toString(),
                    selectedLocation!!.longitude.toString()
                ).collectAsState(emptyList()).value

                else -> shelters
            }

            LaunchedEffect(isSelectedList) {
                lastIsSelectedState.value = isSelectedList.lastOrNull() ?: false
            }

            Column {
                val searchedText = textState.value.text

                if (sheltersToDisplay.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.padding(10.dp)) {
                        items(
                            items = sheltersToDisplay,
                            key = { it.id }
                        ) { item ->
                            var isSelected = shelter(item = item, shelters.indexOf(item), navController, shelterViewModel, user, edit, provisionalArray)
                            isSelectedList.add(isSelected)
                        }
                    }
                }
                lastIsSelectedState.value = isSelectedList.any { it }
                isSelectedList.clear()

                if (user != null && user.role == TypeUser.admin && edit) {
                    FloatingButton(icon = Icons.Default.Add, Modifier.align(Alignment.End)) {
                        showDialogAdd.value = true
                    }
                }


                if(!edit){
                    Button(onClick = {
                        filteredShelter = provisionalArray.value
                        navController.navigate(HOME)

                    },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        enabled = lastIsSelectedState.value,
                        colors = ButtonDefaults.buttonColors(Orange)
                    ) {
                        Text("Accept")
                    }

                }

                if (showDialogAdd.value) {
                    ShelterFormDialog(
                        showDialogAdd = showDialogAdd,
                        shelterViewModel = shelterViewModel
                    )
                }
            }
        }
    }

}

@Composable
fun shelter(item: Any, index: Int, navController: NavController, shelterViewModel: ShelterViewModel, user: User?, edit: Boolean, provisional: MutableState<ArrayList<Int>>): Boolean {
    val border = if (index % 2 == 0) Green1 else Orange
    var isSelected by remember {
        mutableStateOf(false)
    }
    when (item) {
        is Shelter -> {
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (edit) navController.navigate(route = Routes.ABOUTUS + "/" + item.id)
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
                    if(!edit) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { newSelected ->
                                isSelected = newSelected
                                if (isSelected) {
                                    provisional.value += item.id
                                } else {
                                    provisional.value -= item.id
                                }
                            },
                            colors = CheckboxDefaults.colors(Orange)
                        )
                    }


                    if (user != null && user.role == TypeUser.admin && edit) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Received",
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
    return isSelected
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ShelterFormDialog(
    item : Any? = null,
    showDialogAdd: MutableState<Boolean>,
    shelterViewModel: ShelterViewModel
) {

    var editedName by remember {
        mutableStateOf((item as? Shelter)?.name ?: "")
    }
    var editedAboutUs by remember {
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
    var editedSheltersData by remember {
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
        title = { Text("Add New") },
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
                        value = editedAboutUs,
                        onValueChange = { editedAboutUs = it },
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
                        value = editedSheltersData,
                        onValueChange = { editedSheltersData = it },
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
                    val shelter = (item as? Shelter)?.idOwner?.let {
                        Shelter(editedName, editedAboutUs, "$editedLatitude;$editedLongitude",editedSchedule, editedSheltersData, editedEmail, editedPhone,
                            it
                        )
                    }
                    if(item == null){
                        shelterViewModel.viewModelScope.launch {
                            if (shelter != null) {
                                shelterViewModel.insertShelter(
                                    shelter
                                )
                            }
                        }
                    }else {
                        val updatedShelter = (item as Shelter).copy(
                            id = item.id,
                            name = editedName,
                            aboutUs = editedAboutUs,
                            location = "$editedLatitude;$editedLongitude",
                            schedule = editedSchedule,
                            sheltersData = editedSheltersData,
                            email = editedEmail,
                            phone = editedPhone
                        )
                        shelterViewModel.viewModelScope.launch {
                            shelterViewModel.updateShelter(updatedShelter)
                        }
                    }
                    showDialogAdd.value = false
                },
                colors = ButtonDefaults.buttonColors(Orange)

            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(
                onClick = { showDialogAdd.value = false },
                colors = ButtonDefaults.buttonColors(Orange)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun zoneDropdown(
    zoneNames: List<String?>,
    provisionalArray: MutableState<ArrayList<Int>>,
    locations: List<Location>
): Location? { // Cambiado el tipo de retorno a Location?
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(-1) }
    var selectedLocation: MutableState<Location?> = remember { mutableStateOf(null) }

    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp)
            .clickable(onClick = { expanded = true })
            .background(color = Gold, shape = RoundedCornerShape(4.dp))
    ) {
        Text(
            text = if (selectedIndex != -1) {
                zoneNames.getOrNull(selectedIndex) ?: "Select Area"
            } else {
                "Select Area"
            },
            modifier = Modifier.padding(8.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            zoneNames.forEachIndexed { index, zoneName ->
                DropdownMenuItem(
                    {
                        if (zoneName != null && selectedLocation != null) {
                            Text(text = zoneName)
                        } else {
                            Text(text = "Select Area")
                        }
                    },
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        selectedLocation.value = locations[index]
                        provisionalArray.value.clear()
                    }
                )
            }
        }
    }
    return selectedLocation.value
}

