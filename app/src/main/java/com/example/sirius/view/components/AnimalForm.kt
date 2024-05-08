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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import com.example.sirius.model.Animal
import com.example.sirius.model.SectionType
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.parseDateStringToLong
import com.example.sirius.view.screens.DropdownFiltersHome
import com.example.sirius.viewmodel.AnimalViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun animalFormFields(
    animalFormState: AnimalFormState,
    dateState: Long,
    animalViewmodel: AnimalViewModel,
    typeList: List<String>,
    sectionType: SectionType,
    animalFormData: AnimalFormData?,
    isEdit: Boolean
): AnimalFormData {

    val formData = animalFormData ?: createDefaultFormData(animalFormState)

    updateFormStateFromData(animalFormState, formData)

    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        item {
            CustomTextField(
                value = animalFormState.name,
                onValueChange = { updateName(animalFormState, formData, it) },
                label = "Name animal"
            )
        }
        item {
            animalFormData?.let { selectedBirthday(it, dateState) }?.let { state ->
                DatePickerItem(
                    state = state,
                    onDateSelected = { updateBirthDate(animalFormState, formData, it) },
                    title = "Birth Date"
                )
            }
        }
        item {
            SexCheckbox(animalFormState, formData)
        }
        item {
            StatusCheckbox(
                labelText = "Waiting Adoption",
                checked = animalFormState.waitingAdoption,
                onCheckedChange = { isChecked ->
                    updateWaitingAdoption(animalFormState, formData, isChecked)
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
                state = selectedEntryDate(formData, dateState),
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

private fun updateName(animalFormState: AnimalFormState, formData: AnimalFormData, newName: String) {
    animalFormState.name = newName
    formData.name = newName
}

private fun updateBirthDate(animalFormState: AnimalFormState, formData: AnimalFormData, newDate: String) {
    animalFormState.birthDate = newDate
    formData.birthDate = newDate
}

private fun updateWaitingAdoption(animalFormState: AnimalFormState, formData: AnimalFormData, isChecked: Boolean) {
    animalFormState.waitingAdoption = isChecked
    formData.waitingAdoption = isChecked
}

private fun selectedBirthday(formData: AnimalFormData, dateState: Long) : Long{
    return if(formData.birthDate != "") parseDateStringToLong(formData.birthDate) else dateState

}


private fun selectedEntryDate(formData: AnimalFormData, dateState: Long) : Long{
    return if(formData.entryDate != "") parseDateStringToLong(formData.entryDate) else dateState

}
private fun createDefaultFormData(animalFormState: AnimalFormState): AnimalFormData {
    return AnimalFormData(
        id = animalFormState.id,
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
}

private fun updateFormStateFromData(animalFormState: AnimalFormState, formData: AnimalFormData) {
    animalFormState.apply {
        id = formData.id
        name = formData.name
        birthDate = formData.birthDate
        sex = formData.sex
        waitingAdoption = formData.waitingAdoption
        fosterCare = formData.fosterCare
        shortInfo = formData.shortInfo
        longInfo = formData.longInfo
        breed = formData.breed
        typeAnimal = formData.type
        entryDate = formData.entryDate
        photoAnimal = formData.photoAnimal
        inShelter = formData.inShelter
        lost = formData.lost
    }
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