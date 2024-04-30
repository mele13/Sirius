package com.example.sirius.view.components

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sirius.model.News
import com.example.sirius.model.SectionType
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.booleanToInt
import com.example.sirius.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

@Composable
fun NewsFormDialog(
    showDialogAdd: MutableState<Boolean>,
    newsFormState: NewsFormState,
    dateState: Long,
    newsViewmodel: NewsViewModel,
    sectionType: SectionType,
    newsFormData: NewsFormData? =  null,
    onAddClick: () -> Unit,
) {
    var formData = NewsFormData("", "", "", "", "", "", "", false)

    if (sectionType ==  SectionType.GOOD_NEWS) {
        newsFormState.goodNews = true
    } else if (sectionType ==  SectionType.WHATS_NEW){
        newsFormState.goodNews = false
    }

    AlertDialog(
        onDismissRequest = { showDialogAdd.value = false },
        title = { Text("Add New") },
        text = {
            formData = newsFormFields(state = dateState, newsFormState = newsFormState)
        },
        confirmButton = {
            Button(
                onClick = {
                    if (formData.photoNews.isEmpty()){
                        formData.photoNews = "res/drawable/user_image2.jpg"
                    }
                    onAddClick()
                    showDialogAdd.value = false
                    newsViewmodel.viewModelScope.launch {
                        newsViewmodel.insertNews(News(0, formData.title, formData.shortInfo, formData.longInfo, formData.publishedDate, formData.createdAt, formData.untilDate, formData.photoNews, booleanToInt(formData.goodNews) ))
                    }
                    newsFormState.clear()
                },
                enabled = newsFormState.title.isNotEmpty() &&
                        newsFormState.shortInfo.isNotEmpty() &&
                        newsFormState.longInfo.isNotEmpty()
            ) {
                Text("Add")

            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showDialogAdd.value = false
                    newsFormState.clear()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun newsFormFields(
    state: Long,
    newsFormState: NewsFormState,
): NewsFormData {

    val formData = NewsFormData(
        title = newsFormState.title,
        shortInfo = newsFormState.shortInfo,
        longInfo = newsFormState.longInfo,
        publishedDate = newsFormState.publishedDate,
        createdAt = newsFormState.createdAt,
        untilDate = newsFormState.untilDate,
        photoNews = newsFormState.photoNews,
        goodNews = newsFormState.goodNews,
    )

    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        item {
            CustomTextField(
                value = newsFormState.title,
                onValueChange = { newsFormState.title = it },
                label = "Title new"
            )
        }
        item {
            CustomTextField(
                value = newsFormState.shortInfo,
                onValueChange = { newsFormState.shortInfo = it },
                label = "Short Info"
            )
        }
        item {
            CustomTextField(
                value = newsFormState.longInfo,
                onValueChange = { newsFormState.longInfo = it },
                label = "Long Info"
            )
        }
        item {
            DatePickerItem(
                state = state,
                onDateSelected = { date ->
                    newsFormState.publishedDate = date
                },
                title = "Published Date"
            )
        }
        item {
            DatePickerItem(
                state = state,
                onDateSelected = { date ->
                    newsFormState.createdAt = date
                },
                title = "Create At"
            )
        }
        item {
            DatePickerItem(
                state = state,
                onDateSelected = { date ->
                    newsFormState.untilDate = date
                },
                title = "Until Date"
            )
        }
        item {
            PhotoPicker (
                selectedImage = newsFormState.photoNews,
                onImageSelected = { imagePath ->
                    newsFormState.photoNews = imagePath
                }
            )
        }
    }
    return formData
}

@SuppressLint("DiscouragedApi")
@Composable
fun NewsItem(news: News, navController: NavController) {
    val context = LocalContext.current
    val resourceName = news.photoNews.substringAfterLast("/")
    val resourceId = context.resources.getIdentifier(
        resourceName.replace(".jpg", ""), "drawable", context.packageName
    )

    if (resourceId != 0) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val painter = painterResource(id = resourceId)
            SquareImage(painter = painter, onClick = {
               // navController.navigate(route = Routes.NEWSINFO + "/" + news.id)
            })
            Text(
                text = news.titleNews,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                softWrap = true,
                modifier = Modifier.width(100.dp)
            )
        }
    } else {
        Log.e("NewsItem", "Resource not found for ${news.photoNews}")
    }
}

@Composable
fun rememberNewsFormState(): NewsFormState {
    return remember { NewsFormState() }
}

data class NewsFormData(
    var title: String,
    var shortInfo: String,
    var longInfo: String,
    var publishedDate: String,
    var createdAt: String,
    var untilDate: String,
    var photoNews: String,
    var goodNews: Boolean
)

@Stable
class NewsFormState {
    fun clear() {
        title = ""
        shortInfo = ""
        longInfo = ""
        publishedDate = ""
        createdAt = ""
        untilDate = ""
        photoNews = ""
        goodNews = false
    }

    var title by mutableStateOf("")
    var shortInfo by mutableStateOf("")
    var longInfo by mutableStateOf("")
    var publishedDate by mutableStateOf("")
    var createdAt by mutableStateOf("")
    var untilDate by mutableStateOf("")
    var photoNews by mutableStateOf("")
    var goodNews by mutableStateOf(false)
}