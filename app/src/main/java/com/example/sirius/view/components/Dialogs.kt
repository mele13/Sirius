package com.example.sirius.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.sirius.model.Animal
import com.example.sirius.model.News
import com.example.sirius.tools.formatDate
import com.example.sirius.tools.parseDateStringToLong
import com.example.sirius.tools.stringToBoolean
import com.example.sirius.tools.stringToInt
import com.example.sirius.ui.theme.Black
import com.example.sirius.ui.theme.Green1
import com.example.sirius.view.screens.getDrawableResourceId
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.NewsViewModel
import kotlinx.coroutines.launch
@Composable
fun DeleteDialog(
    onDismissRequest: () -> Unit,
    titleDialog: String,
    animalViewModel: AnimalViewModel,
    newsViewModel: NewsViewModel,
    item: Any
) {

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = titleDialog)
            },
            text = {
                Text(text = "Are you sure?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismissRequest()
                        if (item is Animal) {
                            animalViewModel.viewModelScope.launch {
                                animalViewModel.deleteAnimal(
                                    animal = item
                                )
                            }
                        } else if (item is News) {
                            newsViewModel.viewModelScope.launch {
                                newsViewModel.deleteNews(
                                    newNew = item
                                )
                            }
                        }
                    }
                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissRequest
                ) {
                    Text("Cancel")
                }
            }
        )
}

@Composable
fun OutlinedIcon( icon : ImageVector, onClick: (() -> Unit)? = null){
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier
            .size(24.dp)
    )
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Black,
        modifier = Modifier
            .clickable {
                if (onClick != null) {
                    onClick()
                }
            }
            .size(20.dp)
    )
}

@Composable
fun EditDialog(
    onDismissRequest: () -> Unit,
    item : Any,
    animalViewModel : AnimalViewModel,
    newsViewModel : NewsViewModel
){
    var animalEditState by remember { mutableStateOf(AnimalEditState()) }
    var newsEditState by remember { mutableStateOf(NewsEditState()) }

    var predefinedImageList = (1..10).map { index ->
        "res/drawable/user_image$index"
    }

    handleAnimalItem(item, animalEditState)

    var editedName by remember { mutableStateOf((item as? Animal)?.nameAnimal ?: "") }
    var editedShortInfoAnimal by remember { mutableStateOf((item as? Animal)?.shortInfoAnimal ?: "") }
    var editedWaitingAdoption by remember { mutableStateOf(stringToBoolean(animalEditState.waitingAdoption)) }
    var editedFosterCare by remember { mutableStateOf(stringToBoolean( animalEditState.fosterCare)) }
    var editedPhotoAnimal by remember { mutableStateOf((item as? Animal)?.photoAnimal ?: "") }
    var editedBirthDateAnimal by remember { mutableStateOf((item as? Animal)?.birthDate ?: "") }

    val animalData = AnimalData(
        editedName = editedName,
        editedShortInfoAnimal = editedShortInfoAnimal,
        editedBirthDateAnimal = editedBirthDateAnimal,
        editedWaitingAdoption = editedWaitingAdoption,
        editedFosterCare = editedFosterCare,
        editedPhotoAnimal = editedPhotoAnimal
    )

    //News
    var editedTitle by remember { mutableStateOf((item as? News)?.titleNews ?: "") }
    var editedShortInfoNew by remember { mutableStateOf((item as? News)?.shortInfoNews ?: "") }
    var editedPhotoNews by remember { mutableStateOf((item as? News)?.photoNews ?: "") }


    var titleText = when (item) {
        is Animal -> "Editar Datos ${item.nameAnimal}"
        is News -> "Editar Datos ${item.titleNews}"
        else -> ""
    }
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = {
            Text(titleText)
        },
        text = {
            LazyColumn {
                when (item) {
                    is Animal -> item {
                        animalEditState = RenderAnimalContent(predefinedImageList, animalData)
                        println("animalEditState")
                        println(animalEditState.name)
                        println(animalEditState.shortInfo)
                        println(animalEditState.birthDate)
                        println(animalEditState.photo)
                        println(animalEditState.fosterCare)
                        println(animalEditState.waitingAdoption)
                    }
                    is News -> item {
                        newsEditState = RenderNewsContent(editedTitle, editedShortInfoNew, editedPhotoAnimal, predefinedImageList, newsEditState)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (item) {
                        is Animal -> {
                            animalViewModel.viewModelScope.launch {
                                animalViewModel.updateAnimal(animal = Animal(item.id, animalEditState.name, animalEditState.birthDate, item.sexAnimal, stringToInt(animalEditState.waitingAdoption), stringToInt(animalEditState.fosterCare), animalEditState.shortInfo, item.longInfoAnimal, item.breedAnimal, item.typeAnimal, item.entryDate, animalEditState.photo, item.inShelter, item.lost))
                            }
                        }
                        is News -> {
                            newsEditState.title = editedTitle
                            newsEditState.shortInfo = editedShortInfoAnimal
                            newsEditState.photo = editedPhotoNews
                            newsViewModel.viewModelScope.launch {
                                newsViewModel.updateNew(newNew = News(item.id, newsEditState.title, newsEditState.shortInfo, item.longInfoNews, item.publishedDate, item.createdAt, item.untilDate, newsEditState.photo, item.goodNews))
                            }

                        }
                    }
                     onDismissRequest()
                }
            ) {
                Text("Accept")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Canceler")
            }
        }
    )
}

@Composable
fun RenderNewsContent(
    editedTitle: String,
    editedShortInfoNew: String,
    editedPhotoNews: String,
    predefinedImageList: List<String>,
    newsEditState: NewsEditState
): NewsEditState {
    var title by remember { mutableStateOf(editedTitle) }
    var shortInfo by remember { mutableStateOf(editedShortInfoNew) }
    var photo by remember { mutableStateOf(editedPhotoNews) }

    TextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Tittle") }
    )
    TextField(
        value = shortInfo,
        onValueChange = { shortInfo = it },
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
                    val isSelected = photo == imagePath
                    val borderColor = (if (isSelected) Green1 else Color.Transparent)
                    Image(
                        painter = painterResource(id = getDrawableResourceId(imagePath = imagePath)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(4.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .clickable {
                                photo =
                                    imagePath
                            }
                            .border(
                                width = 2.dp,
                                color = borderColor,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                    )
                }
            }
        }
    }
    newsEditState.title = title
    newsEditState.photo = photo
    newsEditState.shortInfo = shortInfo

    return newsEditState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenderAnimalContent(
    predefinedImageList: List<String>,
   animalData: AnimalData,
):AnimalEditState {
    var name by remember(animalData.editedName) { mutableStateOf(animalData.editedName) }
    var shortInfo by remember(animalData.editedShortInfoAnimal) { mutableStateOf(animalData.editedShortInfoAnimal) }
    var waitingAdoption by remember(animalData.editedWaitingAdoption) { mutableStateOf(animalData.editedWaitingAdoption) }
    var fosterCare by remember(animalData.editedFosterCare) { mutableStateOf(animalData.editedFosterCare) }
    var photo by remember(animalData.editedPhotoAnimal) { mutableStateOf(animalData.editedPhotoAnimal) }
    var birthDate by remember(animalData.editedBirthDateAnimal) { mutableStateOf(animalData.editedBirthDateAnimal) }

    val datePickerState = rememberDatePickerState(parseDateStringToLong(birthDate))
    TextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Name") }
    )
    TextField(
        value = shortInfo,
        onValueChange = { shortInfo = it },
        label = { Text("Short Info") }
    )
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Waiting Adoption")
        Checkbox(
            checked = waitingAdoption,
            onCheckedChange = { newWaitingAdoption ->
                waitingAdoption = newWaitingAdoption
            },
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Foster Care")
        Checkbox(
            checked = fosterCare,
            onCheckedChange = { newFosterCare ->
                fosterCare = newFosterCare
            },
        )
    }
    DatePicker(
        state = datePickerState,
        modifier = Modifier.padding(16.dp),
        dateFormatter = DatePickerFormatter(),
        dateValidator = {
            true
        },
        title = {
            Text("Seleccione una fecha", fontWeight = FontWeight.Bold)
        }
    )
    birthDate = formatDate(datePickerState.selectedDateMillis!!)
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
                    val isSelected = photo == imagePath
                    val borderColor = (if (isSelected) Green1 else Color.Transparent)
                    Image(
                        painter = painterResource(id = getDrawableResourceId(imagePath = imagePath)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(4.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .clickable {
                                photo =
                                    imagePath
                            }
                            .border(
                                width = 2.dp,
                                color = borderColor,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                    )
                }
            }
        }
    }
    return AnimalEditState(
        name = name,
        shortInfo = shortInfo,
        waitingAdoption = waitingAdoption.toString(),
        fosterCare = fosterCare.toString(),
        photo = photo,
        birthDate = birthDate
    )
}

class AnimalEditState(
    var name: String = "",
    var shortInfo: String = "",
    var birthDate: String = "",
    var waitingAdoption: String = "",
    var fosterCare: String = "",
    var photo: String = ""
)

data class AnimalData(
    val editedName: String,
    val editedShortInfoAnimal: String,
    val editedBirthDateAnimal: String,
    val editedWaitingAdoption: Boolean,
    val editedFosterCare: Boolean,
    val editedPhotoAnimal: String
)

class NewsEditState(
    var title: String = "",
    var shortInfo: String = "",
    var photo: String = ""
)

private fun handleAnimalItem(item: Any, animalEditState: AnimalEditState){
    if (item is Animal){
        animalEditState.apply {
            animalEditState.name = item.nameAnimal
            animalEditState.shortInfo = item.shortInfoAnimal
            animalEditState.waitingAdoption = item.waitingAdoption.toString()
            animalEditState.fosterCare = item.fosterCare.toString()
        }
    }
}