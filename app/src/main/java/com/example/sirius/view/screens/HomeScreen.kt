package com.example.sirius.view.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sirius.R
import com.example.sirius.model.Animal
import com.example.sirius.model.News
import com.example.sirius.model.SectionType
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.booleanToInt
import com.example.sirius.tools.calculateAgeCategory
import com.example.sirius.tools.formatDate
import com.example.sirius.tools.stringToEnumTypeAnimal
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Green4
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.NewsViewModel
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.util.Date
import com.example.sirius.view.components.BarSearch

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition", "DiscouragedApi")
@Composable
fun HomeScreen(
    navController: NavController,
    animalList: List<Animal>,
    newsList: List<News>,
    userViewModel: UserViewModel,
    animalViewModel: AnimalViewModel,
    newsViewmodel: NewsViewModel,
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
                    animalViewModel = animalViewModel,
                    newsViewmodel = newsViewmodel,
                    typeList = typeList,
                )
                Section(
                    title = stringResource(id = R.string.animalsIntro),
                    list = animalList.filter { it.in_shelter == 1 },
                    isAnimalSection = true,
                    navController = navController,
                    userViewModel = userViewModel,
                    dateState = dateState,
                    animalViewModel = animalViewModel,
                    newsViewmodel = newsViewmodel,
                    typeList = typeList,
                )
                Section(
                    title = stringResource(id = R.string.lostIntro),
                    list = animalList.filter { it.lost == 1 },
                    isAnimalSection = true,
                    navController = navController,
                    userViewModel = userViewModel,
                    dateState = dateState,
                    animalViewModel = animalViewModel,
                    newsViewmodel = newsViewmodel,
                    typeList = typeList,
                )
                Section(
                    title = stringResource(id = R.string.goodNewsIntro),
                    list = newsList.filter { it.goodNews == 1 },
                    isAnimalSection = false,
                    navController = navController,
                    userViewModel = userViewModel,
                    dateState = dateState,
                    animalViewModel = animalViewModel,
                    newsViewmodel = newsViewmodel,
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
    animalViewModel: AnimalViewModel,
    newsViewmodel: NewsViewModel,
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
        showDialogAdd = showDialogAdd
    )

    if (showDialogAdd.value) {
        if (isAnimalSection){
            AnimalFormDialog(
                showDialogAdd = showDialogAdd,
                animalFormState = animalFormState,
                dateState = dateState,
                animalViewmodel = animalViewModel,
                typeList = typeList,
                sectionType = determineSectionType(title),
                onDialogDismiss = { animalFormState.clear() }
            ) {
            }
        } else {
            NewsFormDialog(
                showDialogAdd = showDialogAdd,
                newsFormState = newsFormState,
                dateState = dateState,
                newsViewmodel = newsViewmodel,
                sectionType = determineSectionType(title),
                onDialogDismiss = { newsFormState.clear() }
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
        if (userViewModel.getAuthenticatedUser()?.role?.trim() == "admin") {
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
                if (isAnimalSection) {
                    AnimalItem(animal = item as Animal, navController = navController)
                    dialogType = "animal"
                } else {
                    NewsItem(news = item as News)
                    dialogType = "news"
                }
            }
        }
        AddButton(showDialogAdd, dialogType, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
fun AddButton(
    showDialogAdd: MutableState<Boolean>,
    dialogType: String,
    align: Modifier,
    icon : ImageVector? = Icons.Default.Add) {
    SmallFloatingActionButton(
        onClick = {
            showDialogAdd.value = true
        },
        modifier = Modifier
            .padding(5.dp)
            .then(align),
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
    animalViewmodel: AnimalViewModel,
    typeList: List<String>,
    sectionType: SectionType,
    onDialogDismiss: () -> Unit,
    onAddClick: () -> Unit,
    ) {

    var formData = AnimalFormData("", "", "", false, false, "", "", "", "", "", "", false, false)

    when (sectionType) {
        SectionType.LOST -> {
            animalFormState.lost = true
        }
        SectionType.IN_SHELTER, SectionType.LOST -> {
            animalFormState.inShelter = true
        }
        else -> {
            // No hacer nada para otros tipos de sección
        }
    }

    AlertDialog(
        onDismissRequest = { showDialogAdd.value = false },
        title = { Text("Agregar Nuevo") },
        text = {
            formData = AnimalFormFields(
                animalFormState = animalFormState,
                dateState = dateState,
                animalViewmodel = animalViewmodel,
                typeList = typeList,
                sectionType = sectionType,
            )
        },

        confirmButton = {
            Button(
                onClick = {
                    println("formData")
                    println(formData)
                    println(formData.birthDate)
                    onAddClick()
                    showDialogAdd.value = false
                    animalViewmodel?.viewModelScope?.launch {
                        stringToEnumTypeAnimal(formData.type)?.let {
                            Animal(0, formData.name, formData.birthDate, formData.sex, booleanToInt(formData.waitingAdoption), booleanToInt(formData.fosterCare), formData.shortInfo, formData.longInfo, formData.breed,
                                it, formData.entryDate, formData.photoAnimal, booleanToInt(formData.lost), booleanToInt(formData.inShelter))
                        }?.let { animalViewmodel.insertAnimal(it) }

                    }
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

@Composable
private fun NewsFormDialog(
    showDialogAdd: MutableState<Boolean>,
    newsFormState: NewsFormState,
    dateState: Long,
    newsViewmodel: NewsViewModel,
    sectionType: SectionType,
    onDialogDismiss: () -> Unit,
    onAddClick: () -> Unit,
) {
    var formData = NewsFormData("", "", "", "", "", "", "", false)

    when (sectionType) {
        SectionType.GOOD_NEWS -> {
            newsFormState.goodNews = true
        }
        SectionType.WHATS_NEW -> {
            newsFormState.goodNews = false
        }
        else -> {
            // No hacer nada para otros tipos de sección
        }
    }

    AlertDialog(
        onDismissRequest = { showDialogAdd.value = false },
        title = { Text("Agregar Nuevo") },
        text = {
            formData = NewsFormFields(state = dateState, newsFormState = newsFormState, sectionType = sectionType)
        },
        confirmButton = {
            Button(
                onClick = {
                    println("formData")
                    println(formData)
                    onAddClick()
                    showDialogAdd.value = false
                    newsViewmodel?.viewModelScope?.launch {
                        newsViewmodel.inserNews(News(0, formData.title, formData.shortInfo, formData.longInfo, formData.publishedDate, formData.createdAt, formData.untilDate, formData.photoNews, booleanToInt(formData.goodNews) ))
                    }
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AnimalFormFields(
    animalFormState: AnimalFormState,
    dateState: Long,
    animalViewmodel: AnimalViewModel,
    typeList: List<String>,
    sectionType: SectionType
): AnimalFormData {
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
                selectedDate = animalFormState.birthDate,
                onDateSelected = { date ->
                    animalFormState.birthDate = date
                }
            )
            println("animalFormState.birthDate")
            println(animalFormState.birthDate)
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
            val textState = remember { mutableStateOf(TextFieldValue("")) }
            BarSearch(state = textState, placeHolder = "Breed", modifier = Modifier)

            val searchedText = textState.value.text
            val filteredBreed = breedList.filter {
                it?.contains(searchedText, ignoreCase = true) ?: false
            }

            if (filteredBreed.isEmpty() && searchedText.isNotEmpty()) {
                Text(
                    text = searchedText,
                    modifier = Modifier.padding(10.dp)
                )
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
                animalViewmodel,
                onTypeSelected = { selectedType = it }
            )
            animalFormState.typeAnimal = selectedType
        }
        item {
            DatePickerItem(
                state = dateState,
                selectedDate = animalFormState.entryDate,
                onDateSelected = { date ->
                    animalFormState.entryDate = date
                }
            )
        }
        item {
            PhotoPicker(
                predefinedImageList = predefinedImageList,
                selectedImage = animalFormState.photoAnimal,
                onImageSelected = { imagePath ->
                    animalFormState.photoAnimal = imagePath.toString()
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
        animalViewModel: AnimalViewModel,
        onTypeSelected: (String) -> Unit
){
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf("") }

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
        viewModel = animalViewModel,
        originalText = "Type",
        color = Color.White,
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
    viewModel: AnimalViewModel,
    originalText: String,
    color: Color,
    aux: Boolean = false,
) {
    Box {
        Button(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier
                .padding(5.dp),
            contentPadding = PaddingValues(5.dp)
        ) {
            TextWithSplit(
                text = selectedOption.ifBlank { originalText },
                color = color
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
        ) {
            val uniqueOptions = if (aux) {
                val ageCategories = options.map { calculateAgeCategory(it) }.distinct()
                ageCategories.map { it }
            } else {
                options.distinct()
            }
            uniqueOptions.forEachIndexed { index, option ->
                if (index > 0) {
                    Divider(color = Color.Black, thickness = 1.dp)
                }
                DropdownMenuItem(
                    {
                        Text(text = option)
                    },
                    onClick = {
                        when (text) {
                            "Type" -> {
                                if (option.isNotBlank()) {
                                    viewModel.getAnimalsByTypeAnimal(option)
                                }
                            }
                        }
                        onOptionSelected(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsFormFields(
    state: Long,
    newsFormState: NewsFormState,
    sectionType: SectionType,
    ): NewsFormData {
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

    val dateState = rememberDatePickerState()

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
                selectedDate = newsFormState.publishedDate,
                onDateSelected = { date ->
                    newsFormState.publishedDate = date
                }
            )
        }
        item {
            DatePickerItem(
                state = state,
                selectedDate = newsFormState.createdAt,
                onDateSelected = { date ->
                    newsFormState.createdAt = date
                }
            )
        }
        item {
            DatePickerItem(
                state = state,
                selectedDate = newsFormState.untilDate,
                onDateSelected = { date ->
                    newsFormState.untilDate = date
                }
            )
        }
        item {
            PhotoPicker(
                predefinedImageList = predefinedImageList,
                selectedImage = newsFormState.photoNews,
                onImageSelected = { imagePath ->
                    newsFormState.photoNews = imagePath.toString()
                }
            )
        }

        /*item {
            StatusCheckbox(
                labelText = "Good News",
                checked = newsFormState.goodNews,
                onCheckedChange = { isChecked ->
                    newsFormState.goodNews = isChecked
                }
            )
        }*/
    }
    println("return formData")
    println(formData)
    return formData
}

@Composable
private fun SexCheckbox(animalFormState: AnimalFormState) {
    Text("Select Sex")
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("M")
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
            Text("F")
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
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState(state)
    var selectedDate by remember { mutableStateOf(selectedDate) }
    LaunchedEffect(selectedDate) {
        onDateSelected(selectedDate)
    }
    DatePicker(
        state = datePickerState,
        showModeToggle = true,
        modifier = Modifier.fillMaxWidth(),
        dateFormatter = DatePickerFormatter(),
        dateValidator = {
            true
        },
        title = {
            Text("${selectedDate}", fontWeight = FontWeight.Bold)
        },
    )
    println("selectedDate")
    println(selectedDate)
    onDateSelected(selectedDate)
}


@Composable
fun PhotoPicker(
    predefinedImageList: List<String>,
    selectedImage: String,
    onImageSelected: (Uri) -> Unit
) {
    Column {
        var imageUri: Uri? by remember { mutableStateOf(null) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()) {
            it?.let { uri ->
                imageUri = uri
                onImageSelected(imageUri!!)
            }
        }
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp),
                contentScale = ContentScale.Crop
            )
        }
        Button(
            onClick = {
                launcher.launch(arrayOf("image/*"))
            }
        ) {
            Text(text = "Pick Image")
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
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.padding(bottom = 8.dp)
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
        stringResource(id = R.string.newsIntro) -> SectionType.GOOD_NEWS
        stringResource(id = R.string.animalsIntro) -> SectionType.IN_SHELTER
        stringResource(id = R.string.lostIntro) -> SectionType.LOST
        stringResource(id = R.string.newsIntro) -> SectionType.WHATS_NEW
        else -> SectionType.OTHER
    }
}