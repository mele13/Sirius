package com.example.sirius.view.components

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sirius.model.Animal
import com.example.sirius.model.SectionType
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.booleanToInt
import com.example.sirius.tools.parseDateStringToLong
import com.example.sirius.tools.stringToEnumTypeAnimal
import com.example.sirius.view.screens.DropdownFiltersHome
import com.example.sirius.viewmodel.AnimalViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnimalFormDialog(
    showDialogAdd: MutableState<Boolean>,
    animalFormState: AnimalFormState,
    typeList: List<String>,
    sectionType: SectionType,
    animalFormData: AnimalFormData? =  null,
    isEdit : Boolean,
    onAddClick: () -> Unit,

) {
    val animalViewModel: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)

    val dateState = System.currentTimeMillis()


    var formData = animalFormData ?: AnimalFormData(
        0,
        "", "", "",
        waitingAdoption = false,
        fosterCare = false,
        shortInfo = "",
        longInfo = "",
        breed = "",
        type = "",
        entryDate = "",
        photoAnimal = "",
        inShelter = false,
        lost = false
    )

    if (animalFormData != null) {
        formData = animalFormData
    }

    if (sectionType ==  SectionType.LOST) {
        animalFormState.lost = true
    } else if (sectionType ==  SectionType.IN_SHELTER){
        animalFormState.inShelter = true
    }

    AlertDialog(
        onDismissRequest = { showDialogAdd.value = false },
        title = { Text("Add New") },
        text = {
            formData = animalFormFields(
                animalFormState = animalFormState,
                dateState = dateState,
                animalViewmodel = animalViewModel,
                typeList = typeList,
                sectionType = sectionType,
                animalFormData = formData,
                isEdit
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isEdit){
                        println("updateAnimal")
                        onAddClick()
                        showDialogAdd.value = false
                        animalViewModel.viewModelScope.launch {
                            stringToEnumTypeAnimal(formData.type)?.let {
                                Animal(formData.id, formData.name, formData.birthDate, formData.sex, booleanToInt(formData.waitingAdoption), booleanToInt(formData.fosterCare), formData.shortInfo, formData.longInfo, formData.breed,
                                    it, formData.entryDate, formData.photoAnimal, booleanToInt(formData.lost), booleanToInt(formData.inShelter)
                                )
                            }?.let { animalViewModel.updateAnimal(it) }
                            animalFormState.clear()
                        }
                    } else {
                        if (formData.photoAnimal.isEmpty()){
                            formData.photoAnimal = "res/drawable/user_image1.jpg"
                        }
                        onAddClick()
                        showDialogAdd.value = false
                        println(formData)
                        animalViewModel.viewModelScope.launch {
                            stringToEnumTypeAnimal(formData.type)?.let {
                                Animal(0, formData.name, formData.birthDate, formData.sex, booleanToInt(formData.waitingAdoption), booleanToInt(formData.fosterCare), formData.shortInfo, formData.longInfo, formData.breed,
                                    it, formData.entryDate, formData.photoAnimal, booleanToInt(formData.lost), booleanToInt(formData.inShelter)
                                )
                            }?.let { animalViewModel.insertAnimal(it) }
                            animalFormState.clear()
                        }

                        println("insertAnimal")
                    }

                },

                enabled = animalFormState.name.isNotEmpty()  &&
                        animalFormState.sex.isNotEmpty() &&
                        animalFormState.shortInfo.isNotEmpty() &&
                        animalFormState.longInfo.isNotEmpty() &&
                        animalFormState.breed.isNotEmpty() &&
                        animalFormState.typeAnimal.isNotBlank()


            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    println("formData")
                    println(formData)
                    println(animalFormData)
                    showDialogAdd.value = false
                    animalFormState.clear()
                }
            ) {
                Text("Cancel")
            }
        },
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun animalFormFields(
    animalFormState: AnimalFormState,
    dateState: Long,
    animalViewmodel: AnimalViewModel,
    typeList: List<String>,
    sectionType: SectionType,
    animalFormData: AnimalFormData?,
    isEdit: Boolean
): AnimalFormData {

    val formData = animalFormData ?: AnimalFormData(
        id = animalFormData?.id ?: 0,
        name = animalFormState.name,
        birthDate = animalFormState.birthDate,
        sex = animalFormState.sex,
        waitingAdoption = animalFormState.waitingAdoption,
        fosterCare = animalFormState.fosterCare,
        shortInfo = animalFormState.shortInfo,
        longInfo = animalFormState.longInfo,
        breed = animalFormState.breed,
        type = animalFormState.typeAnimal,
        entryDate = animalFormState.entryDate,
        photoAnimal = animalFormState.photoAnimal,
        inShelter = animalFormState.inShelter,
        lost = animalFormState.lost
    )

    animalFormState.id = formData.id
    animalFormState.name = formData.name
    animalFormState.birthDate = formData.birthDate
    animalFormState.sex = formData.sex
    animalFormState.waitingAdoption = formData.waitingAdoption
    animalFormState.fosterCare = formData.fosterCare
    animalFormState.shortInfo = formData.shortInfo
    animalFormState.longInfo = formData.longInfo
    animalFormState.breed = formData.breed
    animalFormState.typeAnimal = formData.type
    animalFormState.entryDate = formData.entryDate
    animalFormState.photoAnimal = formData.photoAnimal
    animalFormState.inShelter = formData.inShelter
    animalFormState.lost = formData.lost

    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        item {
            CustomTextField(
                value = animalFormState.name,
                onValueChange = {
                    animalFormState.name = it
                    formData.name = it},
                label = "Name animal"
            )
        }
        item {
            println(dateState)
            DatePickerItem(
                state = if( formData.birthDate != "") parseDateStringToLong(formData.birthDate) else dateState,
                onDateSelected = { date ->
                    animalFormState.birthDate = date
                    formData.birthDate = date
                },
                title = "Birth Date"
            )
        }
        item {
            SexCheckbox(animalFormState, formData)
        }
        item {
            StatusCheckbox(
                labelText = "Waiting Adoption",
                checked = animalFormState.waitingAdoption,
                onCheckedChange = { isChecked ->
                    animalFormState.waitingAdoption = isChecked
                    formData.waitingAdoption = isChecked
                }
            )
        }
        item {
            StatusCheckbox(
                labelText = "Foster Care",
                checked = animalFormState.fosterCare,
                onCheckedChange = { isChecked ->
                    animalFormState.fosterCare = isChecked
                    formData.fosterCare = isChecked

                }
            )
        }
        item {
            CustomTextField(
                value = animalFormState.shortInfo,
                onValueChange = { animalFormState.shortInfo = it
                    formData.shortInfo = it
                },
                label = "Short info"
            )
        }
        item {
            CustomTextField(
                value = animalFormState.longInfo,
                onValueChange = { animalFormState.longInfo = it
                    formData.longInfo = it
                },
                label = "Long info"
            )
        }
        item {
            val breedList by animalViewmodel.getBreed().collectAsState(emptyList())
            val textState = remember { mutableStateOf(TextFieldValue(formData.breed)) }
            BarSearch(state = textState, placeHolder = "Breed", modifier = Modifier)

            val searchedText = textState.value.text
            val filteredBreed = breedList.filter {
                it.contains(searchedText, ignoreCase = true)
            }

            if (filteredBreed.isEmpty() && searchedText.isNotEmpty()) {
                Text(
                    text = searchedText,
                    modifier = Modifier.padding(10.dp)
                )
                formData.breed = searchedText
                animalFormState.breed = searchedText

            } else {
                Column {
                    filteredBreed.forEach { breed ->
                        Text(
                            text = breed,
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    textState.value = TextFieldValue(breed)
                                    animalFormState.breed = breed
                                    formData.breed = breed

                                }
                        )
                    }
                }
            }
        }
        item {
          //  var selectedType by remember { mutableStateOf(formData.type) }
            var selectedType = formData.type
            Text("Type animal")
                DropdownFiltersHome(
                    typeList,
                    formData,
                    animalFormState,
                    onTypeSelected = { selectedType = it }
                )


        }
        item {
            DatePickerItem(
                state = if( formData.entryDate != "") parseDateStringToLong(formData.entryDate) else dateState,
                onDateSelected = { date ->
                    animalFormState.entryDate = date
                    formData.entryDate = date

                },
                title = "Entry Date"
            )
        }
        item {
            PhotoPicker(
                selectedImage = formData.photoAnimal,
                onImageSelected = { imagePath ->
                    animalFormState.photoAnimal = imagePath
                    formData.photoAnimal = imagePath

                }
            )
        }

        if (sectionType != SectionType.IN_SHELTER || isEdit){
            item {
                StatusCheckbox(
                    labelText = "In shelter",
                    checked = animalFormState.inShelter,
                    onCheckedChange = { isChecked ->
                        animalFormState.inShelter = isChecked
                        formData.inShelter = isChecked
                    }
                )
            }
        }
        if (sectionType != SectionType.LOST || isEdit) {
            item {
                StatusCheckbox(
                    labelText = "Lost",
                    checked = animalFormState.lost,
                    onCheckedChange = { isChecked ->
                        animalFormState.lost = isChecked
                        formData.lost = isChecked

                    }
                )
            }
        }
    }
    return formData
}

@SuppressLint("DiscouragedApi")
@Composable
fun AnimalItem(animal: Animal, navController: NavController) {
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
fun rememberAnimalFormState(): AnimalFormState {
    return remember { AnimalFormState() }
}

data class AnimalFormData(
    var id: Int = 0,
    var name: String,
    var birthDate: String,
    var sex: String,
    var waitingAdoption: Boolean,
    var fosterCare: Boolean,
    var shortInfo: String,
    var longInfo: String,
    var breed: String,
    var type: String,
    var entryDate: String,
    var photoAnimal: String,
    var inShelter: Boolean,
    var lost: Boolean
)

@Stable
class AnimalFormState {
    fun clear() {
        name = ""
        birthDate = ""
        sex = ""
        waitingAdoption = false
        fosterCare = false
        shortInfo = ""
        longInfo = ""
        breed = ""
        typeAnimal = ""
        entryDate = ""
        photoAnimal = ""
        inShelter = false
        lost = false
    }

    var id by mutableStateOf(0)
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