package com.example.sirius.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sirius.tools.formatDate
import com.example.sirius.ui.theme.Green1
import com.example.sirius.ui.theme.Green4
import com.example.sirius.view.screens.getDrawableResourceId

@Composable
fun SexCheckbox(animalFormState: AnimalFormState, animalFormData: AnimalFormData) {
    val bothEmpty = animalFormState.sex.isEmpty()
    val textColor = if (bothEmpty) Color.Red else LocalContentColor.current
    Text("Select Sex")
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        SexCheckboxOption("M", animalFormState, animalFormData, textColor)
        SexCheckboxOption("F", animalFormState, animalFormData, textColor)
    }
}

@Composable
fun SexCheckboxOption(
    sex: String,
    animalFormState: AnimalFormState,
    animalFormData: AnimalFormData,
    textColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(sex, color = textColor)
        Checkbox(
            checked = animalFormState.sex == sex,
            onCheckedChange = { isChecked ->
                animalFormState.sex = if (isChecked) sex else ""
                animalFormData.sex = if (isChecked) sex else ""
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerItem(
    state: Long,
    title: String,
    onDateSelected: @Composable (String) -> Unit
) {
    val datePickerState = rememberDatePickerState(state)

    DatePicker(
        state = datePickerState,
        showModeToggle = true,
        modifier = Modifier.fillMaxWidth(),
        dateFormatter = DatePickerFormatter(),
        dateValidator = {
            true
        },
        title = {
            Text(title, fontWeight = FontWeight.Bold)
        },
    )

    onDateSelected(formatDate(datePickerState.selectedDateMillis!!))
}


@Composable
fun PhotoPicker(
    selectedImage: String,
    onImageSelected: (String) -> Unit,
) {
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
                    val isSelected = selectedImage == imagePath
                    Image(
                        painter = painterResource(id = getDrawableResourceId(imagePath = imagePath)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(4.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .clickable {
                                onImageSelected(imagePath)
                            }
                            .border(
                                width = 2.dp,
                                color = if (isSelected) Green1 else Color.Transparent,
                                shape = MaterialTheme.shapes.extraSmall
                            ),
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val textColor = if (value.isEmpty()) Color.Red else LocalContentColor.current
    val textStyle = TextStyle(color = textColor)

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = textStyle) },
        modifier = modifier.padding(bottom = 8.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        singleLine = true,
    )
}

@Composable
fun StatusCheckbox(
    labelText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(labelText)
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SquareImage(
    painter: Painter,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .padding(4.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .border(2.dp, Green4)
            .run {
                if (onClick != null) clickable { onClick.invoke() } else this
            }
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.extraSmall)
        )
    }
}