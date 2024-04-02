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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sirius.model.Animal
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.buildAnAgeText
import com.example.sirius.tools.calculateAge
import com.example.sirius.tools.calculateAgeCategory
import com.example.sirius.tools.getYearRangeFromCategory
import com.example.sirius.tools.mapCategoryToYearRange
import com.example.sirius.ui.theme.Gold
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Orange
import com.example.sirius.ui.theme.Wine
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.Year

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnimalsGallery(
    navController: NavController,
    ageList: List<String>,
    breedList: List<String>,
    typeList: List<String>,
    userViewModel: UserViewModel,
    viewModel: AnimalViewModel
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

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) { // es algo aqui TODO
            DropdownButton(
                text = "Age range",
                options = ageList.map { it.toString() },
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

        val animalsByAgeCategory = if (selectedCategory.isNotBlank()) {
            val ageRange = mapCategoryToYearRange(selectedCategory)
            println("Age Range: $ageRange") // Imprime la categoría convertida en rango de años

            val (startYear, endYear) = getYearRangeFromCategory(ageRange)
            println("Start Year: $startYear, End Year: $endYear") // Imprime el inicio y fin del rango de años

            val currentYear = Year.now().value
            val startBirthYear = currentYear - endYear
            val endBirthYear = currentYear - startYear
            println("Birth Year Range: $startBirthYear-$endBirthYear") // Imprime el rango de años de nacimiento

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

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredAnimals.size) { index ->
                val animal = filteredAnimals.elementAtOrNull(index)
                animal?.let { animal ->
                    AnimalCard(
                        animal = animal,
                        navController = navController,
                        viewModel = viewModel,
                        userViewModel = userViewModel
                    )
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

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DiscouragedApi", "CoroutineCreationDuringComposition")
@Composable
fun AnimalCard(
    animal: Animal,
    navController: NavController,
    viewModel: AnimalViewModel,
    userViewModel: UserViewModel
) {
    var isFavorite by remember { mutableStateOf(false) }
    val age = calculateAge(animal.birthDate)
    val userId = userViewModel.getAuthenticatedUser()?.id

    if (userId != null) {
        userViewModel.viewModelScope.launch {
            userViewModel.getLikedAnimals(userId).collect { likedAnimals ->
                isFavorite = likedAnimals.any { it.id == animal.id }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.6f)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable {
                navController.navigate(route = Routes.ANIMALINFO + "/" + animal.id)
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
                val photoPath = animal.photoAnimal
                val firstImagePath = photoPath.split(", ")[0].trim()
                val resourceName = firstImagePath.substringAfterLast("/")
                val resourceId = context.resources.getIdentifier(
                    resourceName.replace(".jpg", ""), "drawable", context.packageName
                )

                if (resourceId != 0) {
                    val painter = painterResource(id = resourceId)
                    Image(
                        painter = painter,
                        contentDescription = animal.shortInfoAnimal,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                    )
                } else {
                    Log.e("AnimalImage", "Recurso no encontrado para ${animal.photoAnimal}")
                }
                if (userId != null) {
                    if (isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Wine,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clickable {
                                    isFavorite = !isFavorite
                                    userViewModel.viewModelScope.launch {
                                        viewModel.removeLikedAnimal(
                                            animalId = animal.id,
                                            userId = userId
                                        )
                                    }
                                }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Wine,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clickable {
                                    isFavorite = !isFavorite
                                    userViewModel.viewModelScope.launch {
                                        viewModel.insertLikedAnimal(
                                            animalId = animal.id,
                                            userId = userId
                                        )
                                    }
                                }
                        )
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
                // Texto de adopción
                val adoptionText = if (animal.waitingAdoption == 1) {
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

                    if (animal.fosterCare == 1) {
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

                Text(
                    text = "${animal.nameAnimal}, ${buildAnAgeText(age, animal.birthDate, true)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = animal.shortInfoAnimal,
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
