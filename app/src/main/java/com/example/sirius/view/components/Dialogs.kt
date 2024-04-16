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
import com.example.sirius.tools.stringToBoolean
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
                Text(text = "¿Estás seguro de eliminarlo?")
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
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissRequest
                ) {
                    Text("Cancelar")
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDialog(onDismissRequest: () -> Unit, item : Any, animalViewModel : AnimalViewModel, newsViewModel : NewsViewModel){

    var nameAnimal by remember { mutableStateOf("") }
    var shortInfoAnimal by remember { mutableStateOf("") }
    val longInfoAnimal by remember { mutableStateOf("") }
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

    var editedName by remember { mutableStateOf((item as? Animal)?.nameAnimal ?: "") }
    var editedShortInfoAnimal by remember { mutableStateOf((item as? Animal)?.shortInfoAnimal ?: "") }
    var editedWaitingAdoption by remember { mutableStateOf(stringToBoolean(waitingAdoptionAnimal)) }
    var editedFosterCare by remember { mutableStateOf(stringToBoolean( fosterCareAnimal)) }
    var editedPhotoAnimal by remember { mutableStateOf((item as? Animal)?.photoAnimal ?: "") }


    //News
    var editedTitle by remember { mutableStateOf((item as? News)?.titleNews ?: "") }
    var editedShortInfoNew by remember { mutableStateOf((item as? News)?.shortInfoNews ?: "") }
    var editedPhotoNews by remember { mutableStateOf((item as? News)?.photoNews ?: "") }


    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = {
            Text(when (item) {
                is Animal -> "Editar Datos ${item.nameAnimal}"
                is News -> "Editar Datos ${item.titleNews}"
                else -> ""
            })
        },
        text = {
            Column {
                when (item) {
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
                            dateValidator = {
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
                                                    editedPhotoAnimal =
                                                        imagePath
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
                                                    editedPhotoNews =
                                                        imagePath
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
                                animalViewModel.updateAnimal(animal = Animal(item.id, nameAnimal, item.birthDate, item.sexAnimal, waitingAdoptionAnimal.toInt(), fosterCareAnimal.toInt(), shortInfoAnimal, longInfoAnimal, item.breedAnimal, item.typeAnimal, item.entryDate, photoAnimal, item.inShelter, item.lost))

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
                     onDismissRequest()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    // Cerrar el diálogo sin guardar cambios
                    onDismissRequest()
                }
            ) {
                Text("Cancelar")
            }
        }
    )
}