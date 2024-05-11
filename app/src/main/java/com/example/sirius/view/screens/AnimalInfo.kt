package com.example.sirius.view.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.model.Animal
import com.example.sirius.model.News
import com.example.sirius.model.SectionType
import com.example.sirius.model.TypeAnimal
import com.example.sirius.model.TypeUser
import com.example.sirius.model.User
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.checkIfAnimalIsFavorite
import com.example.sirius.tools.buildAnAgeText
import com.example.sirius.tools.calculateAge
import com.example.sirius.ui.theme.Orange
import com.example.sirius.view.components.AdoptAnAnimal
import com.example.sirius.view.components.AnimalFormData
import com.example.sirius.view.components.AnimalFormDialog
import com.example.sirius.view.components.AnimalFormState
import com.example.sirius.view.components.FavoriteIcon
import com.example.sirius.view.components.rememberAnimalFormState
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.ChatViewModel
import com.example.sirius.viewmodel.ShelterViewModel
import com.example.sirius.viewmodel.UserViewModel

@SuppressLint("DiscouragedApi", "CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnimalInfo(
    id: Int?,
    viewModel: AnimalViewModel,
    userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    navController: NavController,
) {
    val user = userViewModel.getAuthenticatedUser()
    var showDialog by remember { mutableStateOf(false) }
    var editMode = remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedLongInfo by remember { mutableStateOf("") }

    var isFavorite by remember { mutableStateOf(false) }
    val animal by viewModel.getAnimalById(id ?: 0).collectAsState(initial = null)
    val context = LocalContext.current
    val userId = userViewModel.getAuthenticatedUser()?.id

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        editedName = animal?.nameAnimal.toString()
        editedLongInfo = animal?.longInfoAnimal.toString()

        isFavorite =
            animal?.let { checkIfAnimalIsFavorite(userId = userId, animal = it, userViewModel = userViewModel) } == true

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            DisplayAnimalFormDialogIfNeeded(editMode, animal)

            LazyColumn(
                verticalArrangement = Arrangement.Bottom
            ) {
                if (animal != null) {
                    val photoPaths = animal!!.photoAnimal.split(", ").map { it.trim() }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.White)
                        ) {
                            CarouselSlider(photoPaths, animal!!, context)
                            Image(
                                painter = painterResource(id = R.drawable.rectangle2),
                                contentDescription = "rectangle",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.32f)
                                    .align(Alignment.BottomStart),
                                colorFilter = ColorFilter.tint(color = colorScheme.background),
                            )
                            if (id != null) {
                                println("id animal")
                                println(animal!!.id)
                                DisplaySponsorButton(user = user, navController = navController, id = animal!!.id, modifier = Modifier.align(Alignment.BottomStart))
                            }

                            Button(
                                onClick = { showDialog = true },
                                modifier = Modifier
                                    .width(200.dp)
                                    .align(Alignment.BottomCenter)
                                    .offset(y = (-25).dp),
                                colors = ButtonDefaults.buttonColors(Orange)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.adopt_me),
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight(400),
                                        color = Color(0xFFFFFFFF),
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 20.dp)
                        ) {
                            Text(
                                text = animal!!.nameAnimal,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                            )
                            HandleUserActions(
                                userId,
                                user,
                                isFavorite,
                                animal,
                                editMode,
                                navController,
                            ) { newValue ->
                                isFavorite = newValue
                            }
                        }
                    }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 20.dp)
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = animal!!.longInfoAnimal,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp)
                            )

                        }
                    }
                    val age = calculateAge(animal!!.birthDate)
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Age: ${buildAnAgeText(age, animal!!.birthDate)}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp)
                        )
                    }
                }
            }
        }
    }
    HandleAdoptionDialog(showDialog, animal, chatViewModel, userViewModel)
}



@SuppressLint("DiscouragedApi")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselSlider(photoPaths: List<String>, item: Any, context: Context) {
    val pagerState = rememberPagerState()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            pageCount = photoPaths.size,
            state = pagerState,
            key = { photoPaths[it] }
        ) { index ->
            val resourceName = photoPaths[index].substringAfterLast("/").replace(".jpg", "")
            val resourceId = context.resources.getIdentifier(
                resourceName, "drawable", context.packageName
            )

            if(item is Animal){
                if(resourceId != 0) {
                    GetImage(painter = resourceId, description = item.shortInfoAnimal)
                } else {
                    GetImage(painter = R.drawable.image_not_found, description = item.shortInfoAnimal)
                }
            } else if (item is News){
                if(resourceId != 0) {
                    GetImage(painter = resourceId, description = item.shortInfoNews)
                } else {
                    GetImage(painter = R.drawable.image_not_found, description = item.shortInfoNews)
                }
            }


        }
    }
}

@Composable
fun GetImage(painter: Int, description: String) {
    Image(
        painter = painterResource(id = painter),
        contentDescription = description,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
    )
}

private fun createAnimalFormDataFromAnimal(animal: Animal?): AnimalFormData? {
    return animal?.let {
        AnimalFormData(
            it.id,
            it.nameAnimal,
            it.birthDate,
            it.sexAnimal,
            it.waitingAdoption,
            it.fosterCare,
            it.shortInfoAnimal,
            it.longInfoAnimal,
            it.breedAnimal,
            it.typeAnimal.name,
            it.entryDate,
            it.photoAnimal,
            it.inShelter,
            it.lost
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ShowAnimalFormDialog(
    editMode: MutableState<Boolean>,
    animalFormState: AnimalFormState,
    animalFormData: AnimalFormData?,
    animal: Animal?
) {
    var sectionType = if (animal?.lost == 1) SectionType.LOST else SectionType.IN_SHELTER

    AnimalFormDialog(
        showDialogAdd = editMode,
        animalFormState = animalFormState,
        typeList = TypeAnimal.getAllDisplayNames(),
        sectionType = sectionType,
        animalFormData = animalFormData,
        isEdit = true
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayAnimalFormDialogIfNeeded(
    editMode: MutableState<Boolean>,
    animal: Animal?
) {
    if (editMode.value) {
        val animalFormData = createAnimalFormDataFromAnimal(animal)
        val animalFormState = rememberAnimalFormState()
        ShowAnimalFormDialog(editMode, animalFormState, animalFormData, animal)
    }
}

@Composable
fun HandleAdoptionDialog(showDialog: Boolean, animal: Animal?, chatViewModel: ChatViewModel, userViewModel: UserViewModel) {
    var showDialogMutable by remember { mutableStateOf(showDialog) }

    if (showDialogMutable) {
        val shelterViewModel: ShelterViewModel = viewModel(factory = ShelterViewModel.factory)

        val shelter by shelterViewModel.getShelterById(1).collectAsState(initial = null)

        animal?.let { animal ->
            shelter?.let {
                AdoptAnAnimal(animal, chatViewModel, userViewModel) {
                    showDialogMutable = false
                }
            }
        }
        LaunchedEffect(showDialogMutable) {
            showDialogMutable = showDialog
        }
    }

}

@Composable
fun HandleUserActions(
    userId: Int?,
    user: User?,
    isFavorite: Boolean,
    animal: Animal?,
    editMode: MutableState<Boolean>,
    navController: NavController,
    onFavoriteChanged: (Boolean) -> Unit
) {
    if (userId != null) {
        if (user?.role != TypeUser.admin && user?.role != TypeUser.owner) {
            Box {
                if (user != null) {
                    FavoriteIcon(
                        isFavorite = isFavorite,
                        item = animal!!,
                        user = user,
                        modifier = Modifier.align(Alignment.TopEnd),
                        onFavoriteChanged = onFavoriteChanged
                    )
                }
            }
        } else {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .clickable { editMode.value = true }
                    .size(15.dp)
            )
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .clickable { navController.navigate(route = Routes.CLINICALRECORD + "/" + animal?.id) }
                    .size(15.dp)
            )
        }
    }
}

@Composable
fun DisplaySponsorButton(
    user: User?,
    navController: NavController,
    id: Int,
    modifier: Modifier
) {
    if (user != null && user.role != TypeUser.admin) {
        Box(
            modifier = modifier
                .clickable {
                    navController.navigate(
                        route = Routes.SPONSORING + "/${id}"
                    )
                }
                .size(65.dp)
                .padding(start = 30.dp, bottom = 25.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.sponsor_icon),
                contentDescription = stringResource(id = R.string.sponsor)
            )
        }
    }
}

