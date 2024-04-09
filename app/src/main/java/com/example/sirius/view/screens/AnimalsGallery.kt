package com.example.sirius.view.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sirius.model.Animal
import com.example.sirius.model.News
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.buildAnAgeText
import com.example.sirius.tools.calculateAge
import com.example.sirius.tools.calculateAgeCategory
import com.example.sirius.tools.getYearRangeFromCategory
import com.example.sirius.tools.mapCategoryToYearRange
import com.example.sirius.tools.stringToBoolean
import com.example.sirius.ui.theme.Black
import com.example.sirius.ui.theme.Gold
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Orange
import com.example.sirius.ui.theme.Wine
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.NewsViewModel
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.Year
import java.util.Date

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnimalsGallery(
    navController: NavController,
    ageList: List<String>,
    breedList: List<String>,
    typeList: List<String>,
    userViewModel: UserViewModel,
    viewModel: AnimalViewModel,
    animalViewModel: AnimalViewModel? = null,
    newsViewModel: NewsViewModel? = null,
    type: String?,
    isAnimal: Boolean
) {
    var selectedCategory by remember { mutableStateOf("") }
    var selectedBreed by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }

    var ageDropdownExpanded by remember { mutableStateOf(false) }
    var breedDropdownExpanded by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (type != "") {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) { // es algo aqui TODO
                DropdownButton(
                    text = "Age range",
                    options = ageList.map { it },
                    selectedOption = selectedCategory,
                    onOptionSelected = { selectedCategory = it },
                    expanded = ageDropdownExpanded,
                    onExpandedChange = { expanded ->
                        ageDropdownExpanded = expanded
                    },
                    viewModel = viewModel,
                    originalText = "Age range",
                    color = Color.White,
                    aux = true,
                )
                ClearFilterIconButton(
                    onClick = { selectedCategory = "" },
                    onOptionSelected = { selectedCategory = it },
                    selectedOption = selectedCategory
                )
                DropdownButton(
                    text = "Breed",
                    options = breedList.map { it },
                    selectedOption = selectedBreed,
                    onOptionSelected = { selectedBreed = it },
                    expanded = breedDropdownExpanded,
                    onExpandedChange = { expanded ->
                        breedDropdownExpanded = expanded
                    },
                    viewModel = viewModel,
                    originalText = "Breed",
                    color = Color.White,
                )
                ClearFilterIconButton(
                    onClick = { selectedBreed = "" },
                    onOptionSelected = { selectedBreed = it },
                    selectedOption = selectedBreed
                )
                DropdownButton(
                    text = "Type",
                    options = typeList.map { it },
                    selectedOption = selectedType,
                    onOptionSelected = { selectedType = it },
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { expanded ->
                        typeDropdownExpanded = expanded
                    },
                    viewModel = viewModel,
                    originalText = "Type",
                    color = Color.White,
                )
                ClearFilterIconButton(
                    onClick = { selectedType = "" },
                    onOptionSelected = { selectedType = it },
                    selectedOption = selectedType
                )
            }
        }

        val animalsByAgeCategory = if (selectedCategory.isNotBlank()) {
            val ageRange = mapCategoryToYearRange(selectedCategory)

            val (startYear, endYear) = getYearRangeFromCategory(ageRange)

            val currentYear = Year.now().value
            val startBirthYear = currentYear - endYear
            val endBirthYear = currentYear - startYear

            viewModel.getAnimalsByBirthYearRange(startBirthYear.toString().takeLast(4), endBirthYear.toString().takeLast(4)).collectAsState(emptyList()).value
        } else {
            viewModel.getAllAnimals().collectAsState(emptyList()).value
        }

        val animalsByBreed = if (selectedBreed.isNotBlank()) {
            viewModel.getAnimalsByBreed(selectedBreed).collectAsState(emptyList()).value
        } else {
            viewModel.getAllAnimals().collectAsState(emptyList()).value
        }

        val animalsByType = if (selectedType.isNotBlank()) {
            viewModel.getAnimalsByTypeAnimal(selectedType).collectAsState(emptyList()).value
        } else {
            viewModel.getAllAnimals().collectAsState(emptyList()).value
        }

        val filteredAnimals = animalsByAgeCategory.intersect(animalsByBreed.toSet()).intersect(animalsByType.toSet())
        val columns = 2
        var items by remember { mutableStateOf<List<Any>>(emptyList()) }

        if (isAnimal){
            if (type == "AnimalsInShelter"){
                if (animalViewModel != null) {
                    userViewModel.viewModelScope.launch {
                        animalViewModel.getOurFriends().collect { animals ->
                            items = animals
                        }
                    }
                }
            } else if (type == "LostAnimals"){
                userViewModel?.viewModelScope?.launch {
                    animalViewModel?.getLostAnimals()?.collect { animals ->
                        items = animals
                    }

                }
            } else {
                userViewModel?.viewModelScope?.launch {
                    animalViewModel?.getAllAnimals()?.collect { animals ->
                        items = animals
                    }
                }

            }
        } else {
            if (type == "GoodNews"){
                newsViewModel?.viewModelScope?.launch {
                    newsViewModel.getGoodNews().collect { news ->
                        items = news
                    }
                }
            } else if (type == "AllNews"){
                newsViewModel?.viewModelScope?.launch {
                    newsViewModel.getWhatNews().collect { news ->
                        items = news
                    }
                }
            }
        }


        LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            items(items.size) { index ->
                val item = items.getOrNull(index)
                item?.let { item ->
                    if (newsViewModel != null) {
                        AnimalCard(
                            item = item,
                            navController = navController,
                            animalViewModel = viewModel,
                            userViewModel = userViewModel,
                            newsViewModel = newsViewModel,
                            type = type,
                            isAnimal = isAnimal,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClearFilterIconButton(
    onClick: () -> Unit,
    onOptionSelected: (String) -> Unit,
    selectedOption: String
) {
    if (selectedOption.isNotBlank()) {
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .offset(x = (-16).dp)
                .clickable {
                    onClick()
                    onOptionSelected("")
                }
        ) {
            Text(
                text = "x",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
            )
        }
    }
}

@Composable
fun TextWithSplit(text: String, color: Color) {
    val texto = if (text.isBlank()) "Texto Vacío" else text
    val espacioIndex = texto.indexOf(' ')

    if (espacioIndex != -1) {
        val primeraParte = texto.substring(0, espacioIndex)
        val segundaParte = texto.substring(espacioIndex + 1)

        Column {
            Text(
                text = primeraParte,
                color = color,
            )
            Text(
                text = segundaParte,
                color = color
            )
        }
    } else {
        Text(
            text = texto,
            color = color
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DropdownButton(
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
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Gold),
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
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .background(Gold)
                .border(1.dp, Color.Black)
                .clip(RoundedCornerShape(20.dp))
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
                            "Age range" -> {
                                when (option) {
                                    "Puppy", "Young", "Adult", "Senior" -> {
                                        viewModel.getAnimalsByAgeDesc(option)
                                    }
                                }
                            }
                            "Breed" -> {
                                if (option.isNotBlank()) {
                                    viewModel.getAnimalsByBreed(option)
                                }
                            }

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
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DiscouragedApi", "CoroutineCreationDuringComposition")
@Composable
fun AnimalCard(
    item: Any,
    navController: NavController,
    animalViewModel: AnimalViewModel,
    userViewModel: UserViewModel,
    newsViewModel: NewsViewModel,
    type: String?,
    isAnimal: Boolean,
) {
    var isFavorite by remember { mutableStateOf(false) }
    val age = if (item is Animal) calculateAge(item.birthDate) else ""
    val user = userViewModel.getAuthenticatedUser()

    var showDialogDelete by remember { mutableStateOf(false) }
    var showDialogEdit by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf(TextFieldValue()) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }

    var nameAnimal by remember { mutableStateOf("") }
    var shortInfoAnimal by remember { mutableStateOf("") }
    var longInfoAnimal by remember { mutableStateOf("") }
    var waitingAdoptionAnimal by remember { mutableStateOf("") }
    var fosterCareAnimal by remember { mutableStateOf("") }
    var photoAnimal by remember { mutableStateOf("") }
    var photoNews by remember { mutableStateOf("") }
    var titleNews by remember { mutableStateOf("") }
    var shortInfoNews by remember { mutableStateOf("") }

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

    if (item is Animal) {
        nameAnimal = item.nameAnimal
        shortInfoAnimal = item.shortInfoAnimal
        waitingAdoptionAnimal = item.waitingAdoption.toString()
        fosterCareAnimal = item.fosterCare.toString()
    }

    if (user != null) {
        if (type == "AnimalsInShelter" || type == "LostAnimals"|| type == null) {
            userViewModel.viewModelScope.launch {
                userViewModel.getLikedAnimals(user.id).collect { likedAnimals ->
                    isFavorite = likedAnimals.any { it.id == (item as Animal).id }
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.6f)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable {
                if (item is Animal) {
                    navController.navigate(route = Routes.ANIMALINFO + "/" + item.id)
                } else if (item is News) {
                    // Navegar a la pantalla de detalles de la noticia
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = Green1,
        ),
        border = BorderStroke(2.dp, Gold),
        shape = MaterialTheme.shapes.medium,

        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            ) {
                val context = LocalContext.current
                val photoPath = if (item is Animal) {
                    item.photoAnimal
                } else if (item is News) {
                    item.photoNews
                } else {
                    null
                }
                val firstImagePath = photoPath?.split(", ")?.get(0)?.trim()
                val resourceName = firstImagePath?.substringAfterLast("/")
                val defaultResourceName = "default_image" // Nombre de recurso predeterminado en caso de que resourceName sea nulo

                val resourceId = context.resources.getIdentifier(
                    resourceName?.replace(".jpg", "") ?: defaultResourceName, "drawable", context.packageName
                )


                if (resourceId != 0) {
                    val painter = painterResource(id = resourceId)
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = if (item is Animal) item.shortInfoAnimal else if (item is News) item.shortInfoNews else null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f)
                        )
                        if (user!!.role.trim() == "admin") {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(50.dp)
                                    .alpha(0.5f)
                                    .pointerInput(Unit) {
                                        detectTapGestures {
                                            showDialogDelete = true
                                        }
                                    }

                            )
                            if (showDialogDelete) {
                                var titleDialog = ""
                                if (item is Animal){
                                    titleDialog = "Eliminar ${item.nameAnimal}"
                                } else if (item is News){
                                    titleDialog = "Eliminar ${item.titleNews}"
                                }
                                AlertDialog(
                                    onDismissRequest = { showDialogDelete = false },
                                    title = {
                                        Text(text = titleDialog)
                                    },
                                    text = {
                                        Text(text = "¿Estás seguro de eliminarlo?")
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                showDialogDelete = false
                                                if (item is Animal){
                                                    animalViewModel.viewModelScope.launch {
                                                        animalViewModel.deleteAnimal(
                                                            animal = item
                                                        )
                                                    }
                                                } else if (item is News){
                                                    newsViewModel.viewModelScope.launch {
                                                        newsViewModel.deleteNews(
                                                            newNew = item
                                                        )
                                                    }
                                                }

                                            }
                                        ) {
                                            Text("Aceptar")
                                        }
                                    },
                                    dismissButton = {
                                        Button(
                                            onClick = { showDialogDelete = false }
                                        ) {
                                            Text("Cancelar")
                                        }
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Log.e("AnimalImage", "Recurso no encontrado para $photoPath")
                }
                if (user != null) {
                        if (user!!.role.trim() != "admin") {
                            if (isFavorite) {
                                // Mostrar ícono de favorito
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Wine,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .clickable {
                                            isFavorite = !isFavorite
                                            animalViewModel.viewModelScope.launch {
                                                animalViewModel.removeLikedAnimal(
                                                    animalId = (item as Animal).id,
                                                    userId = user.id
                                                )
                                            }
                                        }
                                )
                            } else {
                                // Mostrar ícono de no favorito
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = Wine,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .clickable {
                                            // Lógica para cambiar la favorabilidad del animal
                                            isFavorite = !isFavorite
                                            userViewModel.viewModelScope.launch {
                                                animalViewModel.insertLikedAnimal(
                                                    animalId = (item as Animal).id,
                                                    userId = user.id
                                                )
                                            }
                                        }
                                )
                            }
                        } else {
                            Box (contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .align(Alignment.TopEnd))  {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(24.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Black,
                                    modifier = Modifier
                                        .clickable {
                                            showDialogEdit = true
                                        }
                                        .size(20.dp)
                                )
                            }

                            if (showDialogEdit) {
                                //Animal
                                var editedName by remember { mutableStateOf((item as? Animal)?.nameAnimal ?: "") }
                                var editedShortInfoAnimal by remember { mutableStateOf((item as? Animal)?.shortInfoAnimal ?: "") }
                                var editedWaitingAdoption by remember { mutableStateOf(stringToBoolean(waitingAdoptionAnimal)) }
                                var editedFosterCare by remember { mutableStateOf(stringToBoolean(fosterCareAnimal)) }
                                var editedPhotoAnimal by remember { mutableStateOf((item as? Animal)?.photoAnimal ?: "") }
                                var editedBirthDate by remember { mutableStateOf((item as? Animal)?.birthDate ?: "") }

                                //News
                                var editedTitle by remember { mutableStateOf((item as? News)?.titleNews ?: "") }
                                var editedShortInfoNew by remember { mutableStateOf((item as? News)?.shortInfoNews ?: "") }
                                var editedPhotoNews by remember { mutableStateOf((item as? News)?.photoNews ?: "") }


                                AlertDialog(
                                    onDismissRequest = { showDialogEdit = false },
                                    title = {
                                        Text(when (item) {
                                            is Animal -> "Editar Datos ${item.nameAnimal}"
                                            is News -> "Editar Datos ${item.titleNews}"
                                            else -> ""
                                        })
                                    },
                                    text = {
                                        Column {
                                            when (val currentItem = item) {
                                                is Animal -> {
                                                    TextField(
                                                        value = editedName,
                                                        onValueChange = { editedName = it },
                                                        label = { Text("Nombre") }
                                                    )
                                                    TextField(
                                                        value = editedShortInfoAnimal,
                                                        onValueChange = { editedShortInfoAnimal = it },
                                                        label = { Text("Short Info") }
                                                    )
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text("Waiting Adoption")
                                                        Checkbox(
                                                            checked = editedWaitingAdoption,
                                                            onCheckedChange = {
                                                                editedWaitingAdoption = it
                                                            },
                                                        )
                                                    }
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text("Foster Care")
                                                        Checkbox(
                                                            checked = editedFosterCare,
                                                            onCheckedChange = {
                                                                editedFosterCare = it
                                                            },
                                                        )
                                                    }
                                                    DatePicker(
                                                        state = rememberDatePickerState(),
                                                        modifier = Modifier.padding(16.dp),
                                                        dateFormatter = DatePickerFormatter(),
                                                        dateValidator = { date ->
                                                            true
                                                        },
                                                        title = {
                                                            Text("Seleccione una fecha", fontWeight = FontWeight.Bold)
                                                        }
                                                    )
                                                    Text("Change Photo")
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
                                                                    val isSelected = editedPhotoAnimal == imagePath
                                                                    Image(
                                                                        painter = painterResource(id = getDrawableResourceId(imagePath = imagePath)),
                                                                        contentDescription = null,
                                                                        modifier = Modifier
                                                                            .size(50.dp)
                                                                            .padding(4.dp)
                                                                            .clip(MaterialTheme.shapes.extraSmall)
                                                                            .clickable {
                                                                                editedPhotoAnimal = imagePath
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
                                                    /*TextField(
                                                        value = editedLongInfo,
                                                        onValueChange = { editedLongInfo = it },
                                                        label = { Text("Long Info") }
                                                    )*/
                                                }
                                                is News -> {
                                                    // Texto editable para News
                                                    TextField(
                                                        value = editedTitle,
                                                        onValueChange = { editedTitle = it },
                                                        label = { Text("Título") }
                                                    )
                                                    TextField(
                                                        value = editedShortInfoNew,
                                                        onValueChange = { editedShortInfoNew = it },
                                                        label = { Text("Short Info") }
                                                    )
                                                    Text("Change Photo")
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
                                                                    val isSelected = editedPhotoNews == imagePath
                                                                    Image(
                                                                        painter = painterResource(id = getDrawableResourceId(imagePath = imagePath)),
                                                                        contentDescription = null,
                                                                        modifier = Modifier
                                                                            .size(50.dp)
                                                                            .padding(4.dp)
                                                                            .clip(MaterialTheme.shapes.extraSmall)
                                                                            .clickable {
                                                                                editedPhotoNews = imagePath
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
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                when (item) {
                                                    is Animal -> {
                                                        nameAnimal = editedName
                                                        shortInfoAnimal = editedShortInfoAnimal
                                                        waitingAdoptionAnimal = if (editedWaitingAdoption) "1" else "0"
                                                        fosterCareAnimal = if (editedFosterCare) "1" else "0"
                                                        photoAnimal = editedPhotoAnimal
                                                        //longInfoAnimal = editedLongInfo
                                                        animalViewModel.viewModelScope.launch {
                                                            animalViewModel.updateAnimal(animal = Animal(item.id, nameAnimal, item.birthDate, item.sexAnimal, waitingAdoptionAnimal.toInt(), fosterCareAnimal.toInt(), shortInfoAnimal, longInfoAnimal, item.breedAnimal, item.typeAnimal, item.entryDate, photoAnimal, item.in_shelter, item.lost))

                                                        }
                                                    }
                                                    is News -> {
                                                        titleNews = editedTitle
                                                        shortInfoNews = editedShortInfoAnimal
                                                        photoNews = editedPhotoNews
                                                        newsViewModel.viewModelScope.launch {
                                                            newsViewModel.updateNew(newNew = News(item.id, titleNews, shortInfoNews, item.longInfoNews, item.publishedDate, item.createdAt, item.untilDate, photoNews, item.goodNews))
                                                        }

                                                    }
                                                }
                                                // Cerrar el diálogo
                                                showDialogEdit = false
                                            }
                                        ) {
                                            Text("Aceptar")
                                        }
                                    },
                                    dismissButton = {
                                        Button(
                                            onClick = {
                                                // Cerrar el diálogo sin guardar cambios
                                                showDialogEdit = false
                                            }
                                        ) {
                                            Text("Cancelar")
                                        }
                                    }
                                )
                            }

                        }


                }
            }
            Spacer(Modifier.padding(4.dp))
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .offset(y = (-15).dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                if (item is Animal) {
                    val adoptionText = if (item.waitingAdoption == 1) {
                        "Adoption"
                    } else {
                        "Pre Adoption"
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = adoptionText,
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .background(color = Orange, shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 2.dp, vertical = 4.dp)
                        )

                        if (item.fosterCare == 1) {
                            Text(
                                text = "In Foster Care",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .background(color = Orange, shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 2.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                var title = ""
                if (item is Animal) {
                    title = "${item.nameAnimal}, ${buildAnAgeText(age, item.birthDate, true)}"
                } else if (item is News) {
                    title = item.titleNews
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterHorizontally)
                )
                var description = ""
                if (item is Animal) {
                    description = item.shortInfoAnimal
                } else if (item is News){
                    description = item.shortInfoNews
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    softWrap = true,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )

            }
        }
    }
}

