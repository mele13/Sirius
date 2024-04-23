package com.example.sirius.view.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.model.Animal
import com.example.sirius.model.News
import com.example.sirius.model.SectionType
import com.example.sirius.model.TypeUser
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.booleanToInt
import com.example.sirius.tools.formatDate
import com.example.sirius.tools.stringToEnumTypeAnimal
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Green4
import com.example.sirius.view.components.BarSearch
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.NewsViewModel
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch

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
    val dateState = System.currentTimeMillis()

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
                    list = newsList.filter { it.goodNews == 0 },
                    isAnimalSection = false,
                    navController = navController,
                    userViewModel = userViewModel,
                    dateState = dateState,

                    typeList = typeList,
                )
                Section(
                    title = stringResource(id = R.string.animalsIntro),
                    list = animalList.filter { it.inShelter == 1 },
                    isAnimalSection = true,
                    navController = navController,
                    userViewModel = userViewModel,
                    dateState = dateState,

                    typeList = typeList,
                )
                Section(
                    title = stringResource(id = R.string.lostIntro),
                    list = animalList.filter { it.lost == 1 },
                    isAnimalSection = true,
                    navController = navController,
                    userViewModel = userViewModel,
                    dateState = dateState,

                    typeList = typeList,
                )
                Section(
                    title = stringResource(id = R.string.goodNewsIntro),
                    list = newsList.filter { it.goodNews == 1 },
                    isAnimalSection = false,
                    navController = navController,
                    userViewModel = userViewModel,
                    dateState = dateState,

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
    dateState: Long,
    typeList: List<String>) {

    val newsViewModel  : NewsViewModel = viewModel(factory = NewsViewModel.factory)

    val typeRuta = determineRoute(isAnimalSection, title)

    val showDialogAdd = remember { mutableStateOf(false) }
    val animalFormState = rememberAnimalFormState()
    val newsFormState = rememberNewsFormState()

    RowWithTitle(title = title, userViewModel = userViewModel, typeRuta = typeRuta, navController = navController)

    BoxWithContent(
        list = list,
        isAnimalSection = isAnimalSection,
        navController = navController,
        showDialogAdd = showDialogAdd
    )

    if (showDialogAdd.value) {
        if (isAnimalSection){
            AnimalFormDialog(
                showDialogAdd = showDialogAdd,
                animalFormState = animalFormState,
                dateState = dateState,
                typeList = typeList,
                sectionType = determineSectionType(title)
            ) {
            }
        } else {
            NewsFormDialog(
                showDialogAdd = showDialogAdd,
                newsFormState = newsFormState,
                dateState = dateState,
                newsViewmodel = newsViewModel,
                sectionType = determineSectionType(title)
            ) {
            }
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
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(6.dp)
        )
        if (userViewModel.getAuthenticatedUser()?.role?.equals(TypeUser.admin) == true) {
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
    showDialogAdd: MutableState<Boolean>
) {
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
                    NewsItem(news = item as News)
                    "news"
                }
            }
        }
        AddButton(showDialogAdd, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
fun AddButton(
    showDialogAdd: MutableState<Boolean>,
    modifier: Modifier,
    icon : ImageVector? = Icons.Default.Add) {
    SmallFloatingActionButton(
        onClick = {
            showDialogAdd.value = true
        },
        modifier = Modifier
            .padding(5.dp)
            .then(modifier),
        shape = CircleShape
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AnimalFormDialog(
    showDialogAdd: MutableState<Boolean>,
    animalFormState: AnimalFormState,
    dateState: Long,
    typeList: List<String>,
    sectionType: SectionType,
    onAddClick: () -> Unit,
) {
    val animalViewModel: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)

    var formData = AnimalFormData("", "", "",
        waitingAdoption = false,
        fosterCare = false,
        shortInfo = "",
        longInfo = "",
        breed = "",
        type = "",
        entryDate = "",
        photoAnimal = "",
        inShelter = false,
        lost = false
    )

    if (sectionType ==  SectionType.LOST) {
        animalFormState.lost = true
    } else if (sectionType ==  SectionType.IN_SHELTER){
        animalFormState.inShelter = true
    }

    AlertDialog(
        onDismissRequest = { showDialogAdd.value = false },
        title = { Text("Agregar Nuevo") },
        text = {
            formData = animalFormFields(
                animalFormState = animalFormState,
                dateState = dateState,
                animalViewmodel = animalViewModel,
                typeList = typeList,
                sectionType = sectionType,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (formData.photoAnimal.isEmpty()){
                        formData.photoAnimal = "res/drawable/user_image1.jpg"
                    }
                    onAddClick()
                    showDialogAdd.value = false
                    animalViewModel.viewModelScope.launch {
                        stringToEnumTypeAnimal(formData.type)?.let {
                            Animal(0, formData.name, formData.birthDate, formData.sex, booleanToInt(formData.waitingAdoption), booleanToInt(formData.fosterCare), formData.shortInfo, formData.longInfo, formData.breed,
                                it, formData.entryDate, formData.photoAnimal, booleanToInt(formData.lost), booleanToInt(formData.inShelter))
                        }?.let { animalViewModel.insertAnimal(it) }
                        animalFormState.clear()
                    }
                },
                enabled = animalFormState.name.isNotEmpty() &&
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
                }
            ) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun NewsFormDialog(
    showDialogAdd: MutableState<Boolean>,
    newsFormState: NewsFormState,
    dateState: Long,
    newsViewmodel: NewsViewModel,
    sectionType: SectionType,
    onAddClick: () -> Unit,
) {
    var formData = NewsFormData("", "", "", "", "", "", "", false)

    if (sectionType ==  SectionType.GOOD_NEWS) {
        newsFormState.goodNews = true
    } else if (sectionType ==  SectionType.WHATS_NEW){
        newsFormState.goodNews = false
    }

    AlertDialog(
        onDismissRequest = { showDialogAdd.value = false },
        title = { Text("Add New") },
        text = {
            formData = newsFormFields(state = dateState, newsFormState = newsFormState)
        },
        confirmButton = {
            Button(
                onClick = {
                    if (formData.photoNews.isEmpty()){
                        formData.photoNews = "res/drawable/user_image2.jpg"
                    }
                    onAddClick()
                    showDialogAdd.value = false
                    newsViewmodel.viewModelScope.launch {
                        newsViewmodel.inserNews(News(0, formData.title, formData.shortInfo, formData.longInfo, formData.publishedDate, formData.createdAt, formData.untilDate, formData.photoNews, booleanToInt(formData.goodNews) ))
                    }
                    newsFormState.clear()
                },
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
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun animalFormFields(
    animalFormState: AnimalFormState,
    dateState: Long,
    animalViewmodel: AnimalViewModel,
    typeList: List<String>,
    sectionType: SectionType
): AnimalFormData {

    val formData = AnimalFormData(
        name = animalFormState.name,
        birthDate = animalFormState.birthDate,
        sex = animalFormState.sex,
        waitingAdoption = animalFormState.waitingAdoption,
        fosterCare = animalFormState.fosterCare,
        shortInfo = animalFormState.shortInfo,
        longInfo = animalFormState.longInfo,
        breed = animalFormState.breed,
        type = animalFormState.typeAnimal,
        entryDate = animalFormState.entryDate,
        photoAnimal = animalFormState.photoAnimal,
        inShelter = animalFormState.inShelter,
        lost = animalFormState.lost
    )

    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        item {
            CustomTextField(
                value = animalFormState.name,
                onValueChange = { animalFormState.name = it },
                label = "Name animal"
            )
        }
        item {
            DatePickerItem(
                state = dateState,
                onDateSelected = { date ->
                    animalFormState.birthDate = date
                },
                title = "Birth Date"
            )
        }
        item {
            SexCheckbox(animalFormState)
        }
        item {
            StatusCheckbox(
                labelText = "Waiting Adoption",
                checked = animalFormState.waitingAdoption,
                onCheckedChange = { isChecked ->
                    animalFormState.waitingAdoption = isChecked
                }
            )
        }
        item {
            StatusCheckbox(
                labelText = "Foster Care",
                checked = animalFormState.fosterCare,
                onCheckedChange = { isChecked ->
                    animalFormState.fosterCare = isChecked
                }
            )
        }
        item {
            CustomTextField(
                value = animalFormState.shortInfo,
                onValueChange = { animalFormState.shortInfo = it },
                label = "Short info"
            )
        }
        item {
            CustomTextField(
                value = animalFormState.longInfo,
                onValueChange = { animalFormState.longInfo = it },
                label = "Long info"
            )
        }
        item {
            val breedList by animalViewmodel.getBreed().collectAsState(emptyList())
            val textState = remember { mutableStateOf(TextFieldValue(animalFormState.breed)) }
            BarSearch(state = textState, placeHolder = "Breed", modifier = Modifier)

            val searchedText = textState.value.text
            val filteredBreed = breedList.filter {
                it.contains(searchedText, ignoreCase = true)
            }

            if (filteredBreed.isEmpty() && searchedText.isNotEmpty()) {
                Text(
                    text = searchedText,
                    modifier = Modifier.padding(10.dp)
                )
                formData.breed = searchedText
                animalFormState.breed = searchedText
            } else {
                Column {
                    filteredBreed.forEach { breed ->
                        Text(
                            text = breed,
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    textState.value = TextFieldValue(breed)
                                    animalFormState.breed = breed
                                }
                        )
                    }
                }
            }
        }
        item {
            var selectedType by remember { mutableStateOf("") }
            Text("Type animal")
            DropdownFiltersHome(
                typeList,
                onTypeSelected = { selectedType = it }
            )
            animalFormState.typeAnimal = selectedType
        }
        item {
            DatePickerItem(
                state = dateState,
                onDateSelected = { date ->
                    animalFormState.entryDate = date
                },
                title = "Entry Date"
            )
        }
        item {
            PhotoPicker(
                selectedImage = animalFormState.photoAnimal,
                onImageSelected = { imagePath ->
                    animalFormState.photoAnimal = imagePath
                }
            )
        }
        if (sectionType != SectionType.IN_SHELTER){
            item {
                StatusCheckbox(
                    labelText = "In shelter",
                    checked = animalFormState.inShelter,
                    onCheckedChange = { isChecked ->
                        animalFormState.inShelter = isChecked
                    }
                )
            }
        }
        if (sectionType != SectionType.LOST) {
            item {
                StatusCheckbox(
                    labelText = "Lost",
                    checked = animalFormState.lost,
                    onCheckedChange = { isChecked ->
                        animalFormState.lost = isChecked
                    }
                )
            }
        }
    }
    return formData
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DropdownFiltersHome(
    typeList: List<String>,
    onTypeSelected: (String) -> Unit
){
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    var selectedType by rememberSaveable { mutableStateOf("") }

    DropdownButtonHome(
        text = "Type",
        options = typeList.map { it },
        selectedOption = selectedType,
        onOptionSelected = {
            selectedType = it
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

    Box {
        Button(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier
                .padding(5.dp),
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
        ) {

            options.distinct().forEach { option ->
                DropdownMenuItem(
                    {
                        Text(text = option)
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
                    }
                )
            }
        }
    }
}

@Composable
private fun newsFormFields(
    state: Long,
    newsFormState: NewsFormState,
): NewsFormData {

    val formData = NewsFormData(
        title = newsFormState.title,
        shortInfo = newsFormState.shortInfo,
        longInfo = newsFormState.longInfo,
        publishedDate = newsFormState.publishedDate,
        createdAt = newsFormState.createdAt,
        untilDate = newsFormState.untilDate,
        photoNews = newsFormState.photoNews,
        goodNews = newsFormState.goodNews,
    )

    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        item {
            CustomTextField(
                value = newsFormState.title,
                onValueChange = { newsFormState.title = it },
                label = "Title new"
            )
        }
        item {
            CustomTextField(
                value = newsFormState.shortInfo,
                onValueChange = { newsFormState.shortInfo = it },
                label = "Short Info"
            )
        }
        item {
            CustomTextField(
                value = newsFormState.longInfo,
                onValueChange = { newsFormState.longInfo = it },
                label = "Long Info"
            )
        }
        item {
            DatePickerItem(
                state = state,
                onDateSelected = { date ->
                    newsFormState.publishedDate = date
                },
                title = "Published Date"
            )
        }
        item {
            DatePickerItem(
                state = state,
                onDateSelected = { date ->
                    newsFormState.createdAt = date
                },
                title = "Create At"
            )
        }
        item {
            DatePickerItem(
                state = state,
                onDateSelected = { date ->
                    newsFormState.untilDate = date
                },
                title = "Until Date"
            )
        }
        item {
            PhotoPicker (
                selectedImage = newsFormState.photoNews,
                onImageSelected = { imagePath ->
                    newsFormState.photoNews = imagePath
                }
            )
        }
    }
    return formData
}

@Composable
private fun SexCheckbox(animalFormState: AnimalFormState) {
    val bothEmpty = animalFormState.sex.isEmpty()
    val textColor = if (bothEmpty) Color.Red else LocalContentColor.current

    Text("Select Sex")
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("M", color = textColor)
            Checkbox(
                checked = animalFormState.sex == "M",
                onCheckedChange = { isChecked ->
                    animalFormState.sex = if (isChecked) "M" else ""
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("F", color = textColor)
            Checkbox(
                checked = animalFormState.sex == "F",
                onCheckedChange = { isChecked ->
                    animalFormState.sex = if (isChecked) "F" else ""
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerItem(
    state: Long,
    title: String,
    onDateSelected: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState(state)

    DatePicker(
        state = datePickerState,
        showModeToggle = true,
        modifier = Modifier.fillMaxWidth(),
        dateFormatter = DatePickerFormatter(),
        dateValidator = {
            true
        },
        title = {
            Text(title, fontWeight = FontWeight.Bold)
        },
    )

    onDateSelected(formatDate(datePickerState.selectedDateMillis!!))
}


@Composable
fun PhotoPicker(
    selectedImage: String,
    onImageSelected: (String) -> Unit,
) {
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
                    val isSelected = selectedImage == imagePath
                    Image(
                        painter = painterResource(id = getDrawableResourceId(imagePath = imagePath)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(4.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .clickable {
                                onImageSelected(imagePath)
                            }
                            .border(
                                width = 2.dp,
                                color = if (isSelected) Green1 else Color.Transparent,
                                shape = MaterialTheme.shapes.extraSmall
                            ),
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val textColor = if (value.isEmpty()) Color.Red else LocalContentColor.current
    val textStyle = TextStyle(color = textColor)

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = textStyle) },
        modifier = modifier.padding(bottom = 8.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        singleLine = true,
    )
}

@Composable
fun StatusCheckbox(
    labelText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(labelText)
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Stable
class AnimalFormState {
    fun clear() {
        name = ""
        birthDate = ""
        sex = ""
        waitingAdoption = false
        fosterCare = false
        shortInfo = ""
        longInfo = ""
        breed = ""
        typeAnimal = ""
        entryDate = ""
        photoAnimal = ""
        inShelter = false
        lost = false
    }

    var name by mutableStateOf("")
    var sex by mutableStateOf("")
    var waitingAdoption by mutableStateOf(false)
    var fosterCare by mutableStateOf(false)
    var shortInfo by mutableStateOf("")
    var longInfo by mutableStateOf("")
    var breed by mutableStateOf("")
    var typeAnimal by mutableStateOf("")
    var photoAnimal by mutableStateOf("")
    var birthDate by mutableStateOf("")
    var entryDate by mutableStateOf("")
    var inShelter by mutableStateOf(false)
    var lost by mutableStateOf(false)
}

@Stable
class NewsFormState {
    fun clear() {
        title = ""
        shortInfo = ""
        longInfo = ""
        publishedDate = ""
        createdAt = ""
        untilDate = ""
        photoNews = ""
        goodNews = false
    }

    var title by mutableStateOf("")
    var shortInfo by mutableStateOf("")
    var longInfo by mutableStateOf("")
    var publishedDate by mutableStateOf("")
    var createdAt by mutableStateOf("")
    var untilDate by mutableStateOf("")
    var photoNews by mutableStateOf("")
    var goodNews by mutableStateOf(false)
}

@Composable
fun rememberAnimalFormState(): AnimalFormState {
    return remember { AnimalFormState() }
}

@Composable
fun rememberNewsFormState(): NewsFormState {
    return remember { NewsFormState() }
}

@SuppressLint("DiscouragedApi")
@Composable
fun NewsItem(news: News) {
    val context = LocalContext.current
    val resourceName = news.photoNews.substringAfterLast("/")
    val resourceId = context.resources.getIdentifier(
        resourceName.replace(".jpg", ""), "drawable", context.packageName
    )

    if (resourceId != 0) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val painter = painterResource(id = resourceId)
            SquareImage(painter = painter)

            Text(
                text = news.titleNews,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                softWrap = true,
                modifier = Modifier.width(100.dp)
            )
        }
    } else {
        Log.e("NewsItem", "Resource not found for ${news.photoNews}")
    }
}

@SuppressLint("DiscouragedApi")
@Composable
private fun AnimalItem(animal: Animal, navController: NavController) {
    val context = LocalContext.current
    val photoPath = animal.photoAnimal
    val firstImagePath = photoPath.split(", ")[0].trim()
    val resourceName = firstImagePath.substringAfterLast("/")
    val resourceId = context.resources.getIdentifier(
        resourceName.replace(".jpg", ""), "drawable", context.packageName
    )

    if (resourceId != 0) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val painter = painterResource(id = resourceId)
            SquareImage(painter = painter, onClick = {
                navController.navigate(route = Routes.ANIMALINFO + "/" + animal.id)
            })
            Text(
                text = animal.nameAnimal,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                softWrap = true,
                modifier = Modifier.width(100.dp)
            )
        }
    } else {
        Log.e("AnimalItem", "Resource not found for ${animal.photoAnimal}")
    }
}

@Composable
fun SquareImage(
    painter: Painter,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .padding(4.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .border(2.dp, Green4)
            .run {
                if (onClick != null) clickable { onClick.invoke() } else this
            }
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.extraSmall)
        )
    }
}

data class NewsFormData(
    var title: String,
    var shortInfo: String,
    var longInfo: String,
    var publishedDate: String,
    var createdAt: String,
    var untilDate: String,
    var photoNews: String,
    var goodNews: Boolean
)

data class AnimalFormData(
    var name: String,
    var birthDate: String,
    var sex: String,
    var waitingAdoption: Boolean,
    var fosterCare: Boolean,
    var shortInfo: String,
    var longInfo: String,
    var breed: String,
    var type: String,
    var entryDate: String,
    var photoAnimal: String,
    var inShelter: Boolean,
    var lost: Boolean
)

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