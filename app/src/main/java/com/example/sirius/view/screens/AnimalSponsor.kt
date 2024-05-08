package com.example.sirius.view.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sirius.R
import com.example.sirius.ui.theme.Gold
import com.example.sirius.view.components.NotAvailableDialog

@Composable
fun AnimalSponsor(photo: String?, animalName: String?) {
    val photoUrl = "res/drawable/${photo}"
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        NotAvailableDialog(
            onDismiss = {
                showDialog = false
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        ) {
            item {
                GenerateText(
                    textId = R.string.sponsorTitle,
                    style = "title",
                )
            }
            item {
                GenerateText(
                    textId = R.string.sponsorText,
                    style = "body",
                )
            }
            item {
                GenerateText(
                    textId = R.string.sponsorMid,
                    style = "body",
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.sponsorAnimal) + " $animalName?",
                        style = MaterialTheme.typography.h5,
                        textAlign = TextAlign.Center,
                    )
                    AnimalImage(photoUrl = photoUrl)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            item {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(Gold)
                ) {
                    Text(
                        text = stringResource(id = R.string.sponsorEnd),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun AnimalImage(photoUrl: String) {
    val context = LocalContext.current
    val resourceName = photoUrl.substringAfterLast("/").replace(".jpg", "")
    val resourceId = context.resources.getIdentifier(
        resourceName, "drawable", context.packageName
    )
    if(resourceId != 0) {
        GenerateImage(painter = resourceId)
    } else {
        GenerateImage(painter = R.drawable.image_not_found)
    }
}

@Composable
fun GenerateImage(painter: Int) {
    Image(
        painter = painterResource(id = painter),
        contentDescription = stringResource(id = R.string.animal_photo),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp)
            .clip(androidx.compose.material3.MaterialTheme.shapes.extraSmall),
    )
}

@Composable
fun GenerateText(textId: Int, style: String) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(id = textId),
            style = if (style == "title") { MaterialTheme.typography.h5 } else { MaterialTheme.typography.body1 },
            textAlign = TextAlign.Center
        )
    }
}
