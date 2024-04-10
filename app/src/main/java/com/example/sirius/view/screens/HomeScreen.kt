package com.example.sirius.view.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.model.Animal
import com.example.sirius.model.News
import com.example.sirius.navigation.Routes
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Green4
import com.example.sirius.viewmodel.UserViewModel

@SuppressLint("CoroutineCreationDuringComposition", "DiscouragedApi")
@Composable
fun HomeScreen(
    navController: NavController,
    animalList: List<Animal>,
    newsList: List<News>,
    userViewModel: UserViewModel,
) {
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
                    userViewModel = userViewModel
                )
                Section(
                    title = stringResource(id = R.string.animalsIntro),
                    list = animalList.filter { it.in_shelter == 1 },
                    isAnimalSection = true,
                    navController = navController,
                    userViewModel = userViewModel
                )
                Section(
                    title = stringResource(id = R.string.lostIntro),
                    list = animalList.filter { it.lost == 1 },
                    isAnimalSection = true,
                    navController = navController,
                    userViewModel = userViewModel
                )
                Section(
                    title = stringResource(id = R.string.goodNewsIntro),
                    list = newsList.filter { it.goodNews == 1 },
                    isAnimalSection = false,
                    navController = navController,
                    userViewModel = userViewModel
                )
            }
        }
    }
}

@Composable
fun Section(
    title: String,
    list: List<Any>,
    isAnimalSection: Boolean,
    navController: NavController,
    userViewModel: UserViewModel,
) {
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
                animalFormState = animalFormState
            ) {
            }
        } else {
            NewsFormDialog(
                showDialogAdd = showDialogAdd,
                newsFormState = newsFormState
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

@Composable
private fun AnimalFormDialog(
    showDialogAdd: MutableState<Boolean>,
    animalFormState: AnimalFormState,
    onAddClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { showDialogAdd.value = false },
        title = { Text("Agregar Nuevo") },
        text = {

            AnimalFormFields(animalFormState = animalFormState)
        },

        confirmButton = {
            Button(
                onClick = {
                    onAddClick()
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

@Composable
private fun NewsFormDialog(
    showDialogAdd: MutableState<Boolean>,
    newsFormState: NewsFormState,
    onAddClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { showDialogAdd.value = false },
        title = { Text("Agregar Nuevo") },
        text = {

            NewsFormFields(newsFormState = newsFormState)
        },

        confirmButton = {
            Button(
                onClick = {
                    onAddClick()
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

@Composable
private fun AnimalFormFields(animalFormState: AnimalFormState) {
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
                selectedDate = animalFormState.birthDate,
                onDateSelected = { date ->
                    animalFormState.birthDate = date
                }
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
            CustomTextField(
                value = animalFormState.breed,
                onValueChange = { animalFormState.breed = it },
                label = "Breed"
            )
        }
        item {
            CustomTextField(
                value = animalFormState.typeAnimal,
                onValueChange = { animalFormState.typeAnimal = it },
                label = "Type animal"
            )
        }
        item {
            DatePickerItem(
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
                    animalFormState.photoAnimal = imagePath
                }
            )
        }

        item {
            StatusCheckbox(
                labelText = "In shelter",
                checked = animalFormState.inShelter,
                onCheckedChange = { isChecked ->
                    animalFormState.inShelter = isChecked
                }
            )
        }
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

@Composable
private fun NewsFormFields(newsFormState: NewsFormState) {
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
                selectedDate = newsFormState.publishedDate,
                onDateSelected = { date ->
                    newsFormState.publishedDate = date
                }
            )
        }
        item {
            DatePickerItem(
                selectedDate = newsFormState.createdAt,
                onDateSelected = { date ->
                    newsFormState.createdAt = date
                }
            )
        }
        item {
            DatePickerItem(
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
                    newsFormState.photoNews = imagePath
                }
            )
        }
        item {
            StatusCheckbox(
                labelText = "Good News",
                checked = newsFormState.goodNews,
                onCheckedChange = { isChecked ->
                    newsFormState.goodNews = isChecked
                }
            )
        }

    }
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
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    DatePicker(
        state = rememberDatePickerState(),
        modifier = Modifier.fillMaxWidth(),
        dateFormatter = DatePickerFormatter(),
        dateValidator = { date ->
            true
        },
        title = {
            Text("Birth Date", fontWeight = FontWeight.Bold)
        }
    )
}

@Composable
fun PhotoPicker(
    predefinedImageList: List<String>,
    selectedImage: String,
    onImageSelected: (String) -> Unit
) {
    Column {
        Text("Change Photo")
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
                            )
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