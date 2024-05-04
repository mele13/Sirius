package com.example.sirius.view.components

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.sirius.model.News
import com.example.sirius.model.SectionType
import com.example.sirius.model.TypeAnimal
import com.example.sirius.model.TypeUser
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.CheckIfAnimalIsFavorite
import com.example.sirius.tools.buildAnAgeText
import com.example.sirius.tools.calculateAge
import com.example.sirius.tools.intToBoolean
import com.example.sirius.ui.theme.Black
import com.example.sirius.ui.theme.Gold
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Orange
import com.example.sirius.ui.theme.Wine
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.NewsViewModel
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DiscouragedApi", "CoroutineCreationDuringComposition")
@Composable
fun AnimalCard(
    item: Any,
    navController: NavController,
    animalViewModel: AnimalViewModel,
    userViewModel: UserViewModel,
    newsViewModel: NewsViewModel,
) {
    var isFavorite by remember { mutableStateOf(false) }
    val age = if (item is Animal) calculateAge(item.birthDate) else ""
    val user = userViewModel.getAuthenticatedUser()

    var showDialogDelete by remember { mutableStateOf(false) }
    var showDialogEdit = remember { mutableStateOf(false) }

    var nameAnimal by remember { mutableStateOf("") }
    var shortInfoAnimal by remember { mutableStateOf("") }
    var waitingAdoptionAnimal by remember { mutableStateOf(false) }
    var fosterCareAnimal by remember { mutableStateOf(false) }
    var inShelter by remember { mutableStateOf(false) }
    var lost by remember { mutableStateOf(false) }


    var photoAnimal by remember { mutableStateOf("") }
    var photoNews by remember { mutableStateOf("") }

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
        waitingAdoptionAnimal = intToBoolean(item.waitingAdoption)
        fosterCareAnimal = intToBoolean(item.fosterCare)
        inShelter = intToBoolean(item.inShelter)
        lost = intToBoolean(item.lost)
    }

    if (item is Animal && user != null) {
        isFavorite =
            item?.let { CheckIfAnimalIsFavorite(userId = user.id, animal = it, userViewModel = userViewModel) } == true
    }

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.6f)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable {
                navigateToDetails(item, navController)
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
                val photoPath = when (item) {
                    is Animal -> {
                        item.photoAnimal
                    }
                    is News -> {
                        item.photoNews
                    }
                    else -> {
                        null
                    }
                }
                val firstImagePath = photoPath?.split(", ")?.get(0)?.trim()
                val resourceName = firstImagePath?.substringAfterLast("/")
                val defaultResourceName =
                    "default_image"

                val resourceId = context.resources.getIdentifier(
                    resourceName?.replace(".jpg", "") ?: defaultResourceName,
                    "drawable",
                    context.packageName
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
                        ShowDeleteDialog(
                            item = item,
                            animalViewModel = animalViewModel,
                            newsViewModel = newsViewModel,
                            showDialogDelete = showDialogDelete,
                            onDismiss = { showDialogDelete = false }
                        )
                    }
                } else {
                    Log.e("AnimalImage", "Resource not found $photoPath")
                }
                if (user != null) {
                    if (user!!.role != TypeUser.admin) {
                        if (isFavorite) {
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
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = Wine,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clickable {
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
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Black,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clickable {
                                    showDialogEdit.value = true
                                }
                        )
                        if (showDialogEdit.value) {


                            //Animal
                            var editedName by remember {
                                mutableStateOf(
                                    (item as? Animal)?.nameAnimal ?: ""
                                )
                            }

                            var editedBirthDate by remember {
                                mutableStateOf(
                                    (item as? Animal)?.birthDate ?: ""
                                )
                            }

                            var editedAnimalSex by remember {
                                mutableStateOf(
                                    (item as? Animal)?.sexAnimal ?: ""
                                )
                            }

                            var editedWaitingAdoption by remember {
                                mutableStateOf(
                                    waitingAdoptionAnimal
                                )
                            }

                            var editedFosterCare by remember {
                                mutableStateOf(
                                    fosterCareAnimal

                                )
                            }

                            var editedShortInfoAnimal by remember {
                                mutableStateOf(
                                    (item as? Animal)?.shortInfoAnimal ?: ""
                                )
                            }

                            var editedLongInfoAnimal by remember {
                                mutableStateOf(
                                    (item as? Animal)?.longInfoAnimal ?: ""
                                )
                            }

                            var editedBreed by remember {
                                mutableStateOf(
                                    (item as? Animal)?.breedAnimal ?: ""
                                )
                            }

                            var editedTypeAnimal by remember {
                                mutableStateOf(
                                    (item as? Animal)?.typeAnimal ?: "" as TypeAnimal
                                )
                            }


                            var editedEntryDate by remember {
                                mutableStateOf(
                                    (item as? Animal)?.entryDate ?: ""
                                )
                            }

                            var editedPhotoAnimal by remember {
                                mutableStateOf(
                                    (item as? Animal)?.photoAnimal ?: ""
                                )
                            }

                            var editedInShelter by remember {
                                mutableStateOf(
                                    inShelter
                                )
                            }

                            var editedLost by remember {
                                mutableStateOf(
                                    inShelter
                                )
                            }

                            var idAnimal = (item as? Animal)?.id
                            val animalFormData = idAnimal?.let {
                                AnimalFormData(
                                    it, editedName,
                                    editedBirthDate,
                                    editedAnimalSex,
                                    editedWaitingAdoption,
                                    editedFosterCare,
                                    editedShortInfoAnimal,
                                    editedLongInfoAnimal,
                                    editedBreed,
                                    editedTypeAnimal.name,
                                    editedEntryDate,
                                    editedPhotoAnimal,
                                    editedInShelter,
                                    editedLost)
                            }

                            val animalFormState = rememberAnimalFormState()


                            val typeList by animalViewModel.getTypeAnimal().collectAsState(emptyList())

                            AnimalFormDialog(
                                showDialogAdd = showDialogEdit,
                                animalFormState = animalFormState,
                                typeList = typeList,
                                sectionType = if((item as? Animal)!!.lost == 1) SectionType.LOST else SectionType.IN_SHELTER,
                                animalFormData = animalFormData,
                                isEdit = true,
                            ) {

                            }


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
                } else if (item is News) {
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


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DiscouragedApi", "CoroutineCreationDuringComposition")
@Composable
fun NewsCard(
    item: Any,
    navController: NavController,
    animalViewModel: AnimalViewModel,
    userViewModel: UserViewModel,
    newsViewModel: NewsViewModel,
) {
    var isFavorite by remember { mutableStateOf(false) }
    val age = if (item is Animal) calculateAge(item.birthDate) else ""
    val user = userViewModel.getAuthenticatedUser()

    var showDialogDelete by remember { mutableStateOf(false) }
    var showDialogEdit = remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var shortInfoAnimal by remember { mutableStateOf("") }
    var longInfoAnimal by remember { mutableStateOf("") }

    var goodNews by remember { mutableStateOf(false) }



    var photoAnimal by remember { mutableStateOf("") }
    var photoNews by remember { mutableStateOf("") }

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

    if (item is News) {
        title = item.titleNews
        shortInfoAnimal = item.shortInfoNews
        longInfoAnimal = item.longInfoNews
        goodNews = intToBoolean(item.goodNews)

    }


    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.6f)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable {
                navigateToDetails(item, navController)
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
                val photoPath = when (item) {
                    is Animal -> {
                        item.photoAnimal
                    }
                    is News -> {
                        item.photoNews
                    }
                    else -> {
                        null
                    }
                }
                val firstImagePath = photoPath?.split(", ")?.get(0)?.trim()
                val resourceName = firstImagePath?.substringAfterLast("/")
                val defaultResourceName =
                    "default_image"

                val resourceId = context.resources.getIdentifier(
                    resourceName?.replace(".jpg", "") ?: defaultResourceName,
                    "drawable",
                    context.packageName
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
                        ShowDeleteDialog(
                            item = item,
                            animalViewModel = animalViewModel,
                            newsViewModel = newsViewModel,
                            showDialogDelete = showDialogDelete,
                            onDismiss = { showDialogDelete = false }
                        )
                    }
                } else {
                    Log.e("AnimalImage", "Resource not found $photoPath")
                }
                if (user != null) {
                    if (user!!.role != TypeUser.admin) {
                        if (isFavorite) {
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
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = Wine,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clickable {
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
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Black,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clickable {
                                    showDialogEdit.value = true
                                }
                        )
                        if (showDialogEdit.value) {


                            //Animal
                            var editedTitle by remember {
                                mutableStateOf(
                                    (item as? News)?.titleNews ?: ""
                                )
                            }

                            var editedShortInfo by remember {
                                mutableStateOf(
                                    (item as? News)?.shortInfoNews ?: ""
                                )
                            }

                            var editedLongInfo by remember {
                                mutableStateOf(
                                    (item as? News)?.longInfoNews ?: ""
                                )
                            }

                            var editedPublishedDate by remember {
                                mutableStateOf(
                                    (item as? News)?.publishedDate ?: ""
                                )
                            }

                            var editedCreatedAt by remember {
                                mutableStateOf(
                                    (item as? News)?.createdAt ?: ""
                                )
                            }

                            var editedUntilDate by remember {
                                mutableStateOf(
                                    (item as? News)?.untilDate ?: ""
                                )
                            }

                            var editedPhotoNews by remember {
                                mutableStateOf(
                                    (item as? News)?.photoNews ?: ""
                                )
                            }


                            var editedGoodNews by remember {
                                mutableStateOf(
                                    goodNews
                                )
                            }


                            var idNews = (item as? News)?.id
                            val newsFormData = idNews?.let {

                                NewsFormData(
                                    it,
                                    editedTitle,
                                    editedShortInfo,
                                    editedLongInfo,
                                    editedPublishedDate,
                                    editedCreatedAt,
                                    editedUntilDate,
                                    editedPhotoNews,
                                    editedGoodNews
                                )

                            }

                            val newsFormState = rememberNewsFormState()


                            NewsFormDialog(
                                showDialogAdd = showDialogEdit,
                                newsFormState = newsFormState,
                                sectionType = if((item as? News)!!.goodNews == 1) SectionType.GOOD_NEWS else SectionType.WHATS_NEW,
                                newsFormData = newsFormData,
                                isEdit = true,
                            ) {

                            }




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
                } else if (item is News) {
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

private fun navigateToDetails(item: Any, navController: NavController) {
    if (item is Animal) {
        navController.navigate(route = Routes.ANIMALINFO + "/" + item.id)
    }else if( item is News) {
        navController.navigate(route = Routes.NEWSINFO + "/" + item.id)

    }
}

@Composable
fun ShowDeleteDialog(
    item: Any,
    animalViewModel: AnimalViewModel,
    newsViewModel: NewsViewModel,
    showDialogDelete: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialogDelete) {
        var titleDialog = ""
        if (item is Animal) {
            titleDialog = "Eliminar ${item.nameAnimal}"
        } else if (item is News) {
            titleDialog = "Eliminar ${item.titleNews}"
        }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = titleDialog)
            },
            text = {
                Text(text = "¿Estás seguro de eliminarlo?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                        if (item is Animal) {
                            animalViewModel.viewModelScope.launch {
                                animalViewModel.deleteAnimal(animal = item)
                            }
                        } else if (item is News) {
                            newsViewModel.viewModelScope.launch {
                                newsViewModel.deleteNews(newNew = item)
                            }
                        }
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}