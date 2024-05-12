package com.example.sirius.view.components

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sirius.model.Animal
import com.example.sirius.model.News
import com.example.sirius.model.SectionType
import com.example.sirius.model.TypeAnimal
import com.example.sirius.model.TypeUser
import com.example.sirius.model.User
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.buildAnAgeText
import com.example.sirius.tools.calculateAge
import com.example.sirius.tools.checkIfAnimalIsFavorite
import com.example.sirius.tools.getAdoptionText
import com.example.sirius.tools.intToBoolean
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
    item: Animal,
    navController: NavController,
    userViewModel: UserViewModel,
) {
    var isFavorite by remember { mutableStateOf(false) }
    var age by remember { mutableStateOf(calculateAge(item)) }

    LaunchedEffect(item, item.birthDate) {
        age = calculateAge(item)
    }

    val user = userViewModel.getAuthenticatedUser()

    var showDialogDelete by remember { mutableStateOf(false) }
    var showDialogEdit = remember { mutableStateOf(false) }
    var nameAnimal by remember { mutableStateOf("") }
    var shortInfoAnimal by remember { mutableStateOf("") }
    var waitingAdoptionAnimal by remember { mutableStateOf(false) }
    var fosterCareAnimal by remember { mutableStateOf(false) }
    var inShelter by remember { mutableStateOf(false) }
    var lost by remember { mutableStateOf(false) }

    nameAnimal = item.nameAnimal
    shortInfoAnimal = item.shortInfoAnimal
    waitingAdoptionAnimal = intToBoolean(item.waitingAdoption)
    fosterCareAnimal = intToBoolean(item.fosterCare)
    inShelter = intToBoolean(item.inShelter)
    lost = intToBoolean(item.lost)

    isFavorite = user?.let { checkIfAnimalIsFavorite(userId = it.id, animal = item, userViewModel = userViewModel) } ?: false

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
                val photoPath = getPhotoPath(item)
                val firstImagePath = photoPath?.split(", ")?.get(0)?.trim()
                val resourceName = firstImagePath?.substringAfterLast("/")
                val defaultResourceName =
                    "default_image"

                val resourceId = context.resources.getIdentifier(
                    resourceName?.replace(".jpg", "") ?: defaultResourceName,
                    "drawable",
                    context.packageName
                )

                CustomImage(
                    resourceId = resourceId,
                    photoPath = photoPath,
                    item = item,
                    user = user,
                    showDialogDelete = showDialogDelete,
                    onShowDialogDeleteChanged = { showDialogDelete = it },
                    modifier = Modifier.fillMaxSize()
                )

                DisplayFavoriteOrEdit(
                    item = item,
                    isFavorite = isFavorite,
                    user = user,
                    showDialogEdit = showDialogEdit,
                    modifier = Modifier.align(Alignment.TopEnd),
                    onFavoriteChanged = { newValue -> isFavorite = newValue },
                )
            }
            Spacer(Modifier.padding(4.dp))
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .offset(y = (-15).dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                val adoptionText = getAdoptionText(item.waitingAdoption)

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
                    FosterCareText(item)

                }
                var title = "${item.nameAnimal}, ${buildAnAgeText(age, item.birthDate, true)}"

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterHorizontally)
                )
                var description = item.shortInfoAnimal
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
    item: News,
    navController: NavController,
    userViewModel: UserViewModel,
) {
    val user = userViewModel.getAuthenticatedUser()

    var showDialogDelete by remember { mutableStateOf(false) }
    var showDialogEdit = remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var shortInfoAnimal by remember { mutableStateOf("") }
    var longInfoAnimal by remember { mutableStateOf("") }

    var goodNews by remember { mutableStateOf(false) }

    title = item.titleNews
    shortInfoAnimal = item.shortInfoNews
    longInfoAnimal = item.longInfoNews
    goodNews = intToBoolean(item.goodNews)

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
                val photoPath = item.photoNews
                val firstImagePath = photoPath.split(", ")[0].trim()
                val resourceName = firstImagePath.substringAfterLast("/")

                val resourceId = context.resources.getIdentifier(
                    resourceName.replace(".jpg", ""),
                    "drawable",
                    context.packageName
                )
                CustomImage(
                    resourceId = resourceId,
                    photoPath = photoPath,
                    item = item,
                    user = user,
                    showDialogDelete = showDialogDelete,
                    onShowDialogDeleteChanged = { showDialogDelete = it },
                    modifier = Modifier.fillMaxSize()
                )
                DisplayFavoriteOrEdit(
                    item = item,
                    user = user,
                    showDialogEdit = showDialogEdit,
                    modifier = Modifier.align(Alignment.TopEnd),
                )
            }
            Spacer(Modifier.padding(4.dp))
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .offset(y = (-15).dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                val title = item.titleNews
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterHorizontally)
                )
                var description = item.shortInfoNews
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
fun CustomImage(
    resourceId: Int,
    photoPath: String?,
    item: Any,
    user: User?,
    showDialogDelete: Boolean,
    onShowDialogDeleteChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (resourceId != 0) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            val painter = painterResource(id = resourceId)
            Image(
                painter = painter,
                contentDescription = getContentDescription(item),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
            )
            if (shouldShowDeleteIcon(user)) {
                DeleteIcon(onShowDialogDeleteChanged, Modifier.align(Alignment.Center))
            }
            if (showDialogDelete) {
                ShowDeleteDialog(item, onShowDialogDeleteChanged)
            }
        }
    } else {
        Log.e("AnimalImage", "Resource not found $photoPath")
    }
}

private fun getContentDescription(item: Any): String? {
    return when (item) {
        is Animal -> item.shortInfoAnimal
        is News -> item.shortInfoNews
        else -> null
    }
}

private fun shouldShowDeleteIcon(user: User?): Boolean {
    return user != null && (user.role == TypeUser.admin || user.role == TypeUser.owner)
}

@Composable
private fun DeleteIcon(onShowDialogDeleteChanged: (Boolean) -> Unit, modifier: Modifier) {
    Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = null,
        tint = Color.Red,
        modifier = modifier
            .size(50.dp)
            .alpha(0.5f)
            .pointerInput(Unit) {
                detectTapGestures {
                    onShowDialogDeleteChanged(true)
                }
            }
    )
}

@Composable
private fun ShowDeleteDialog(
    item: Any,
    onShowDialogDeleteChanged: (Boolean) -> Unit
) {
    val titleDialog = when (item) {
        is Animal -> "Delete ${item.nameAnimal}"
        is News -> "Delete ${item.titleNews}"
        else -> ""
    }
    DeleteDialog(
        onDismissRequest = { onShowDialogDeleteChanged(false) },
        titleDialog = titleDialog,
        animalViewModel = viewModel(factory = AnimalViewModel.factory),
        newsViewModel = viewModel(factory = NewsViewModel.factory),
        item = item
    )
}

@Composable
fun FavoriteIcon(
    isFavorite: Boolean,
    item: Animal,
    user: User,
    modifier: Modifier,
    onFavoriteChanged: (Boolean) -> Unit
) {
    val animalViewModel: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)
    val userViewModel: UserViewModel = viewModel(factory = UserViewModel.factory)

    var currentFavorite by rememberSaveable { mutableStateOf(isFavorite) }

    LaunchedEffect(isFavorite) {
        currentFavorite = isFavorite
    }

    val onClickAction: () -> Unit = {
        val newFavorite = !currentFavorite
        currentFavorite = newFavorite
        onFavoriteChanged(newFavorite)

        if (newFavorite) {
            userViewModel.viewModelScope.launch {
                animalViewModel.insertLikedAnimal(
                    animalId = item.id,
                    userId = user.id
                )
            }
        } else {
            userViewModel.viewModelScope.launch {
                animalViewModel.removeLikedAnimal(
                    animalId = item.id,
                    userId = user.id
                )
            }
        }
    }

    val icon = if (currentFavorite) {
        Icons.Default.Favorite
    } else {
        Icons.Default.FavoriteBorder
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Wine,
        modifier = modifier
            .clickable(onClick = onClickAction)
    )
}

private fun getPhotoPath(item: Any): String? {
    return when (item) {
        is Animal -> item.photoAnimal
        is News -> item.photoNews
        else -> null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DisplayEditDialogAnimal(
    item: Animal,
    showDialogEdit: MutableState<Boolean>,
) {
    if (showDialogEdit.value) {
        var editedName by remember { mutableStateOf((item as? Animal)?.nameAnimal ?: "") }
        var editedBirthDate by remember { mutableStateOf((item as? Animal)?.birthDate ?: "") }
        var editedAnimalSex by remember { mutableStateOf((item as? Animal)?.sexAnimal ?: "") }
        var editedWaitingAdoption by remember { mutableStateOf(item.waitingAdoption) }
        var editedFosterCare by remember { mutableStateOf(item.fosterCare) }
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
        var editedBreed by remember { mutableStateOf((item as? Animal)?.breedAnimal ?: "") }
        var editedTypeAnimal by remember {
            mutableStateOf(
                (item as? Animal)?.typeAnimal ?: "" as TypeAnimal
            )
        }
        var editedEntryDate by remember { mutableStateOf((item as? Animal)?.entryDate ?: "") }
        var editedPhotoAnimal by remember { mutableStateOf((item as? Animal)?.photoAnimal ?: "") }
        var editedInShelter by remember { mutableStateOf(item.inShelter) }
        var editedLost by remember { mutableStateOf(item.lost) }
        var editedShelterid by remember { mutableStateOf(item.shelter_id) }

        val idAnimal = (item as? Animal)?.id
        val animalFormData = idAnimal?.let {
            AnimalFormData(
                it,
                editedName,
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
                editedLost,
                editedShelterid
            )
        }

        val animalFormState = rememberAnimalFormState()
        val animalViewModel: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)

        val typeList by animalViewModel.getTypeAnimal().collectAsState(emptyList())

        val sectionType =
            if (item.lost == 1) SectionType.LOST else SectionType.IN_SHELTER

        AnimalFormDialog(
            showDialogAdd = showDialogEdit,
            animalFormState = animalFormState,
            typeList = typeList,
            sectionType = sectionType,
            animalFormData = animalFormData,
            isEdit = true
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DisplayEditDialogNews(
    item: News,
    showDialogEdit: MutableState<Boolean>,
    goodNews: Int
) {
    if (showDialogEdit.value) {
        var editedTitle by remember { mutableStateOf((item as? News)?.titleNews ?: "") }

        var editedShortInfo by remember { mutableStateOf((item as? News)?.shortInfoNews ?: "") }

        var editedLongInfo by remember { mutableStateOf((item as? News)?.longInfoNews ?: "") }

        var editedPublishedDate by remember { mutableStateOf((item as? News)?.publishedDate ?: "") }

        var editedCreatedAt by remember { mutableStateOf((item as? News)?.createdAt ?: "") }

        var editedUntilDate by remember { mutableStateOf((item as? News)?.untilDate ?: "") }

        var editedPhotoNews by remember { mutableStateOf((item as? News)?.photoNews ?: "") }


        var editedGoodNews by remember { mutableStateOf(goodNews) }

        var editedShelterid by remember { mutableStateOf((item as? News)?.shelter_id ?: 0) }

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
                editedGoodNews,
                editedShelterid
            )

        }

        val newsFormState = rememberNewsFormState()

        val sectionType =
            if ((item as? News)!!.goodNews == 1) SectionType.GOOD_NEWS else SectionType.WHATS_NEW

        NewsFormDialog(
            showDialogAdd = showDialogEdit,
            newsFormState = newsFormState,
            sectionType = sectionType,
            newsFormData = newsFormData,
            isEdit = true,
        )
    }
}

@Composable
fun FosterCareText(item: Animal) {
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayFavoriteOrEdit(
    item: Any,
    isFavorite: Boolean? = null,
    user: User?,
    showDialogEdit: MutableState<Boolean>,
    modifier: Modifier,
    onFavoriteChanged: ((Boolean) -> Unit)? = null,
) {
    if (user != null){
        if (onFavoriteChanged != null && isFavorite != null && user.role == TypeUser.user) {
            DisplayFavoriteIcon(
                item = item as Animal,
                isFavorite = isFavorite,
                user = user,
                modifier = modifier,
                onFavoriteChanged = onFavoriteChanged
            )
        }  else {
            DisplayEditIcon(
                showDialogEdit = showDialogEdit,
                modifier = modifier
            )
            if (showDialogEdit.value) {
                if (item is Animal){
                    DisplayEditDialogAnimal(
                        item = item,
                        showDialogEdit = showDialogEdit,
                    )
                } else if (item is News) {
                    DisplayEditDialogNews(
                        item = item,
                        showDialogEdit = showDialogEdit,
                        goodNews = item.goodNews
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayFavoriteIcon(
    item: Animal,
    isFavorite: Boolean,
    user: User,
    modifier: Modifier,
    onFavoriteChanged: (Boolean) -> Unit
) {
    FavoriteIcon(
        isFavorite = isFavorite,
        item = item,
        user = user,
        modifier = modifier,
        onFavoriteChanged = onFavoriteChanged
    )
}

@Composable
fun DisplayEditIcon(
    showDialogEdit: MutableState<Boolean>,
    modifier: Modifier
) {
    OutlinedIcon(
        icon = Icons.Default.Edit,
        modifier = modifier,
        onClick = { showDialogEdit.value = true }
    )
}