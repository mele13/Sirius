package com.example.sirius.view.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sirius.model.Animal
import com.example.sirius.model.News
import com.example.sirius.tools.calculateAgeCategory
import com.example.sirius.ui.theme.Gold
import com.example.sirius.view.components.AnimalCard
import com.example.sirius.view.components.NewsCard
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.NewsViewModel
import com.example.sirius.viewmodel.UserViewModel
import com.example.sirius.view.screens.filteredShelter
import com.squareup.wire.internal.countNonNull

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DropdownFilters(ageList: List<String>,
                    breedList: List<String>,
                    typeList: List<String>,
                    onCategorySelected: (String) -> Unit,
                    onBreedSelected: (String) -> Unit,
                    onTypeSelected: (String) -> Unit
){
    var ageDropdownExpanded by remember { mutableStateOf(false) }
    var breedDropdownExpanded by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf("") }
    var selectedBreed by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }

    val ageRange  = "Age range"

    var filteredShelters = filteredShelter

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        DropdownButton(
            text = ageRange,
            options = ageList.map { it },
            selectedOption = selectedCategory,
            onOptionSelected = {
                selectedCategory = it
                onCategorySelected(it)
            },
            expanded = ageDropdownExpanded,
            onExpandedChange = { expanded ->
                ageDropdownExpanded = expanded
            },
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
            onOptionSelected = {
                selectedBreed = it
                onBreedSelected(it)
            },
            expanded = breedDropdownExpanded,
            onExpandedChange = { expanded ->
                breedDropdownExpanded = expanded
            },

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
            onOptionSelected = {
                selectedType = it
                onTypeSelected(it)
            },
            expanded = typeDropdownExpanded,
            onExpandedChange = { expanded ->
                typeDropdownExpanded = expanded
            },

        )
        ClearFilterIconButton(
            onClick = { selectedType = "" },
            onOptionSelected = { selectedType = it },
            selectedOption = selectedType
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnimalsGallery(
    navController: NavController,
    ageList: List<String>,
    breedList: List<String>,
    typeList: List<String>,
    userViewModel: UserViewModel,
    type: String?,
    isAnimal: Boolean,
    filteredShelters: ArrayList<Int>
) {
    val animalViewModel: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)
    val newsViewModel: NewsViewModel = viewModel(factory = NewsViewModel.factory)

    var selectedCategory by remember { mutableStateOf("") }
    var selectedBreed by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (type != "") {
            DropdownFilters(
                ageList,
                breedList,
                typeList,
                onCategorySelected = { selectedCategory = it },
                onBreedSelected = { selectedBreed = it },
                onTypeSelected = { selectedType = it }
            )
        }

        val items = remember { mutableStateListOf<Any>() }


        LaunchedEffect(isAnimal, type) {
            fetchItems(isAnimal, type, animalViewModel, newsViewModel, items)
        }

        val filteredItems = items.filter { item ->
        when (item) {
            is Animal -> filteredShelters.contains(item.shelter_id)
            is News -> filteredShelters.contains(item.shelter_id)
            else -> true
        }
    }

        items.clear()
        items.addAll(filteredItems)


        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredItems.size) { index ->
                val item = filteredItems.getOrNull(index)
                when (item) {
                    is Animal ->
                        AnimalCard(item = item, navController = navController, userViewModel = userViewModel)
                    is News ->
                        NewsCard(item = item, navController = navController, userViewModel = userViewModel)
                }
            }
        }
    }
}

private suspend fun fetchItems(
    isAnimal: Boolean,
    type: String?,
    animalViewModel: AnimalViewModel,
    newsViewModel: NewsViewModel,
    items: MutableList<Any>
) {
    if (isAnimal) {
        when (type) {
            "AnimalsInShelter" -> animalViewModel.getOurFriends().collect { items.addAll(it) }
            "LostAnimals" -> animalViewModel.getLostAnimals().collect { items.addAll(it) }
            else -> animalViewModel.getAllAnimals().collect { items.addAll(it) }
        }
    } else {
        when (type) {
            "GoodNews" -> newsViewModel.getGoodNews().collect { items.addAll(it) }
            "AllNews" -> newsViewModel.getWhatNews().collect { items.addAll(it) }
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
    val text = text.ifBlank { "Empty text" }
    val spaceIndex = text.indexOf(' ')

    if (spaceIndex != -1) {
        val firstPart = text.substring(0, spaceIndex)
        val secondPart = text.substring(spaceIndex + 1)

        Column {
            Text(
                text = firstPart,
                color = color,
            )
            Text(
                text = secondPart,
                color = color
            )
        }
    } else {
        Text(
            text = text,
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
    aux: Boolean = false,
) {

    val viewModel: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)

    Box {
        DropdownButtonBox(
            text = text,
            selectedOption = selectedOption,
            onExpandedChange = onExpandedChange,
            expanded = expanded
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .background(Gold)
                .border(1.dp, Color.Black)
                .clip(RoundedCornerShape(20.dp))
        ) {
            val uniqueOptions = getUniqueOptions(options, aux)
            uniqueOptions.forEachIndexed { index, option ->
                if (index > 0) {
                    Divider(color = Color.Black, thickness = 1.dp)
                }
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        handleDropdownItemClick(text, option, viewModel, onOptionSelected, onExpandedChange)
                    }
                )
            }
        }
    }
}

@Composable
private fun DropdownButtonBox(
    text: String,
    selectedOption: String,
    onExpandedChange: (Boolean) -> Unit,
    expanded: Boolean
) {
    Button(
        onClick = { onExpandedChange(!expanded) },
        modifier = Modifier
            .padding(5.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(Gold),
        contentPadding = PaddingValues(5.dp)
    ) {
        TextWithSplit(
            text = selectedOption.ifBlank { text },
            color = Color.White
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getUniqueOptions(options: List<String>, aux: Boolean): List<String> {
    return if (aux) {
        val ageCategories = options.map { calculateAgeCategory(it) }.distinct()
        ageCategories.map { it }
    } else {
        options.distinct()
    }
}

private fun handleDropdownItemClick(
    text: String,
    option: String,
    viewModel: AnimalViewModel,
    onOptionSelected: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
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