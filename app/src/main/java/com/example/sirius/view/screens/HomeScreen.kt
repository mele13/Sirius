package com.example.sirius.view.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.model.Animal
import com.example.sirius.model.News
import com.example.sirius.model.SectionType
import com.example.sirius.model.TypeUser
import com.example.sirius.navigation.Routes
import com.example.sirius.ui.theme.Orange
import com.example.sirius.view.components.AnimalFormData
import com.example.sirius.view.components.AnimalFormDialog
import com.example.sirius.view.components.AnimalFormState
import com.example.sirius.view.components.AnimalItem
import com.example.sirius.view.components.FloatingButton
import com.example.sirius.view.components.NewsFormData
import com.example.sirius.view.components.NewsFormDialog
import com.example.sirius.view.components.NewsItem
import com.example.sirius.view.components.rememberAnimalFormState
import com.example.sirius.view.components.rememberNewsFormState
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition", "DiscouragedApi")
@Composable
fun HomeScreen(
    navController: NavController,
    animalList: List<Animal>,
    newsList: List<News>,
    userViewModel: UserViewModel,
    typeList: List<String>,
) {
    val filtredShelters = filteredShelter
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
                Section(
                    title = stringResource(id = R.string.newsIntro),
                    list = newsList.filter { it.goodNews == 0 && filtredShelters.contains(it.shelter_id) },
                    isAnimalSection = false,
                    navController = navController,
                    userViewModel = userViewModel,
                    typeList = typeList,
                )
                Section(
                    title = stringResource(id = R.string.animalsIntro),
                    list = animalList.filter { it.inShelter == 1 && filtredShelters.contains(it.shelter_id)},
                    isAnimalSection = true,
                    navController = navController,
                    userViewModel = userViewModel,
                    typeList = typeList,
                )
                Section(
                    title = stringResource(id = R.string.lostIntro),
                    list = animalList.filter { it.lost == 1 && filtredShelters.contains(it.shelter_id)},
                    isAnimalSection = true,
                    navController = navController,
                    userViewModel = userViewModel,
                    typeList = typeList,
                )
                Section(
                    title = stringResource(id = R.string.goodNewsIntro),
                    list = newsList.filter { it.goodNews == 1 && filtredShelters.contains(it.shelter_id)},
                    isAnimalSection = false,
                    navController = navController,
                    userViewModel = userViewModel,
                    typeList = typeList,
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Section(
    title: String,
    list: List<Any>,
    isAnimalSection: Boolean,
    navController: NavController,
    userViewModel: UserViewModel,
    typeList: List<String>) {

    val typeRuta = determineRoute(isAnimalSection, title)

    val showDialogAdd = remember { mutableStateOf(false) }
    val animalFormState = rememberAnimalFormState()
    val newsFormState = rememberNewsFormState()

    RowWithTitle(title = title, userViewModel = userViewModel, typeRuta = typeRuta, navController = navController)

    BoxWithContent(
        list = list,
        isAnimalSection = isAnimalSection,
        navController = navController,
        showDialogAdd = showDialogAdd,
        userViewModel = userViewModel
    )

    var animalFormData = AnimalFormData(
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

    var newsFormData = NewsFormData(
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
    if (showDialogAdd.value) {
        if (isAnimalSection){
            AnimalFormDialog(
                showDialogAdd = showDialogAdd,
                animalFormState = animalFormState,
                typeList = typeList,
                sectionType = determineSectionType(title),
                animalFormData = animalFormData,
                isEdit = false
            )
        } else {
            NewsFormDialog(
                showDialogAdd = showDialogAdd,
                newsFormState = newsFormState,
                sectionType = determineSectionType(title),
                newsFormData,
                false
            )
        }
    }
}

@Composable
private fun determineRoute(isAnimalSection: Boolean, title: String): String {
    return when {
        isAnimalSection && title == stringResource(id = R.string.animalsIntro) -> "AnimalsInShelter"
        isAnimalSection && title == stringResource(id = R.string.lostIntro) -> "LostAnimals"
        !isAnimalSection && title == stringResource(id = R.string.goodNewsIntro) -> "GoodNews"
        else -> "AllNews"
    }
}

@Composable
private fun RowWithTitle(
    title: String,
    userViewModel: UserViewModel,
    typeRuta: String,
    navController: NavController
) {
    var user = userViewModel.getAuthenticatedUser()
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(6.dp)
        )
        if (user != null && user.role != TypeUser.user) {
            EditIcon(navController, typeRuta)
        }
    }
}

@Composable
private fun EditIcon(navController: NavController, typeRuta: String) {
    Icon(
        imageVector = Icons.Default.Edit,
        contentDescription = null,
        tint = Color.Black,
        modifier = Modifier.clickable {
            navController.navigate(route = Routes.ANIMALS + "/$typeRuta")
        }
    )
}

@Composable
private fun BoxWithContent(
    list: List<Any>,
    isAnimalSection: Boolean,
    navController: NavController,
    showDialogAdd: MutableState<Boolean>,
    userViewModel: UserViewModel
) {
    val user = userViewModel.getAuthenticatedUser()

    var dialogType = ""
    Box {
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(list) { item ->
                dialogType = if (isAnimalSection) {
                    AnimalItem(animal = item as Animal, navController = navController)
                    "animal"
                } else {
                    NewsItem(news = item as News, navController = navController)
                    "news"
                }
            }
        }
        if (user?.role == TypeUser.admin || user?.role == TypeUser.owner || user?.role == TypeUser.worker || user?.role == TypeUser.volunteer) {
                FloatingButton(icon = Icons.Default.Add, Modifier.align(Alignment.BottomEnd)) {
                    showDialogAdd.value = true
                }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DropdownFiltersHome(
    typeList: List<String>,
    animalFormData: AnimalFormData,
    animalFormState: AnimalFormState,
    onTypeSelected: (String) -> Unit
){
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    var selectedType by rememberSaveable { mutableStateOf("") }

    DropdownButtonHome(
        text = "Type",
        options = typeList.map { it },
        selectedOption = animalFormData.type,
        onOptionSelected = {
            selectedType = it
            animalFormData.type = it
            animalFormState.typeAnimal = it
            onTypeSelected(it)
        },
        expanded = typeDropdownExpanded,
        onExpandedChange = { expanded ->
            typeDropdownExpanded = expanded
        },
        updateSelectedType = { selectedType = it }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DropdownButtonHome(
    text: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    updateSelectedType: (String) -> Unit
) {

    val animalViewModel: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)

    val overlayWidth = with(LocalDensity.current) { 250.dp} // Puedes ajustar el ancho aquí

    Box (Modifier.fillMaxWidth()){
        Button(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Orange),
            contentPadding = PaddingValues(5.dp)
        ) {
            TextWithSplit(
                text = selectedOption.ifBlank { text },
                color = Color.White
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.align(Alignment.Center).width(overlayWidth)

        ) {

            options.distinct().forEach { option ->
                DropdownMenuItem(


                    {
                        Text(text = option,
                            textAlign = TextAlign.Center )
                    },
                    onClick = {
                        when (text) {
                            "Type" -> {
                                if (option.isNotBlank()) {
                                    animalViewModel.getAnimalsByTypeAnimal(option)
                                }
                            }
                        }
                        updateSelectedType(option)
                        onOptionSelected(option)
                        onExpandedChange(false)
                    },
                    colors = MenuDefaults.itemColors(Orange),
                )
            }
        }
    }
}

@Composable
fun determineSectionType(title: String): SectionType {
    return when (title) {
        stringResource(id = R.string.goodNewsIntro) -> SectionType.GOOD_NEWS
        stringResource(id = R.string.animalsIntro) -> SectionType.IN_SHELTER
        stringResource(id = R.string.lostIntro) -> SectionType.LOST
        stringResource(id = R.string.newsIntro) -> SectionType.WHATS_NEW
        else -> SectionType.OTHER
    }
}