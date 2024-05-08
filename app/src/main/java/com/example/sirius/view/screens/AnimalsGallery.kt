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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch

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
    isAnimal: Boolean
) {

    val animalViewModel: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)
    val newsViewModel  : NewsViewModel = viewModel(factory = NewsViewModel.factory)

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
            DropdownFilters(ageList,
                breedList,
                typeList,
                onCategorySelected = { selectedCategory = it },
                onBreedSelected = { selectedBreed = it },
                onTypeSelected = { selectedType = it }
            )
        }

        val columns = 2
        var items by remember { mutableStateOf<List<Any>>(emptyList()) }

        if (isAnimal){
            when (type) {
                "AnimalsInShelter" -> {
                    userViewModel.viewModelScope.launch {
                        animalViewModel.getOurFriends().collect { animals ->
                            items = animals
                        }
                    }
                }
                "LostAnimals" -> {
                    userViewModel.viewModelScope.launch {
                        animalViewModel.getLostAnimals().collect { animals ->
                            items = animals
                        }

                    }
                }
                else -> {
                    userViewModel.viewModelScope.launch {
                        animalViewModel.getAllAnimals().collect { animals ->
                            items = animals
                        }
                    }

                }
            }
        } else {
            if (type == "GoodNews"){
                newsViewModel.viewModelScope.launch {
                    newsViewModel.getGoodNews().collect { news ->
                        items = news
                    }
                }
            } else if (type == "AllNews"){
                newsViewModel.viewModelScope.launch {
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
                item?.let {
                    if (item is Animal) {
                        AnimalCard(
                            item = item,
                            navController = navController,
                            animalViewModel = animalViewModel,
                            userViewModel = userViewModel,
                            newsViewModel = newsViewModel,
                        )
                    }

                    if(item is News){
                        NewsCard(
                            item = item,
                            navController = navController,
                            animalViewModel = animalViewModel,
                            userViewModel = userViewModel,
                            newsViewModel = newsViewModel,
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