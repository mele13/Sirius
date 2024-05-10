package com.example.sirius.view.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import com.example.sirius.model.News
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.booleanToInt
import com.example.sirius.tools.intToBoolean
import com.example.sirius.tools.parseDateStringToLong

@Composable
fun newsFormFields(
    state: Long,
    newsFormState: NewsFormState,
    newsFormData: NewsFormData?,
): NewsFormData {

    val formData = newsFormData ?: NewsFormData(
        id = newsFormData?.id ?: 0,
        title = newsFormState.title,
        shortInfo = newsFormState.shortInfo,
        longInfo = newsFormState.longInfo,
        publishedDate = newsFormState.publishedDate,
        createdAt = newsFormState.createdAt,
        untilDate = newsFormState.untilDate,
        photoNews = newsFormState.photoNews,
        goodNews = newsFormState.goodNews,
    )

    newsFormState.id = formData.id
    newsFormState.title = formData.title
    newsFormState.shortInfo = formData.shortInfo
    newsFormState.longInfo = formData.longInfo
    newsFormState.publishedDate = formData.publishedDate
    newsFormState.createdAt = formData.createdAt
    newsFormState.untilDate = formData.untilDate
    newsFormState.photoNews = formData.photoNews
    newsFormState.goodNews = formData.goodNews
    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        item {
            CustomTextField(
                value = newsFormState.title,
                onValueChange = {
                    newsFormState.title = it
                    formData.title = it
                                },
                label = "Title new"
            )
        }
        item {
            CustomTextField(
                value = newsFormState.shortInfo,
                onValueChange = { newsFormState.shortInfo = it
                    formData.shortInfo = it
                },
                label = "Short Info"
            )
        }
        item {
            CustomTextField(
                value = newsFormState.longInfo,
                onValueChange = { newsFormState.longInfo = it
                    formData.longInfo = it
                },
                label = "Long Info"
            )
        }
        item {
            DatePickerItem(
                state =  if( formData.publishedDate != "") parseDateStringToLong(formData.publishedDate) else state,
                onDateSelected = { date ->
                    newsFormState.publishedDate = date
                    formData.publishedDate = date

                },
                title = "Published Date"
            )
        }
        item {
            DatePickerItem(
                state =  if( formData.createdAt != "") parseDateStringToLong(formData.createdAt) else state,
                onDateSelected = { date ->
                    newsFormState.createdAt = date
                    formData.createdAt = date

                },
                title = "Create At"
            )
        }
        item {
            DatePickerItem(
                state =  if( formData.untilDate != "") parseDateStringToLong(formData.untilDate) else state,
                onDateSelected = { date ->
                    newsFormState.untilDate = date
                    formData.untilDate = date

                },
                title = "Until Date"
            )
        }
        item {
            PhotoPicker (
                selectedImage = formData.photoNews,
                onImageSelected = { imagePath ->
                    newsFormState.photoNews = imagePath
                    formData.photoNews = imagePath
                }
            )
        }
        item {
            StatusCheckbox(
                labelText = "Good News",
                checked = intToBoolean(newsFormState.goodNews),
                onCheckedChange = { isChecked ->
                    updateGoodNews(newsFormState, formData, isChecked)
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
                navController.navigate(route = Routes.NEWSINFO + "/" + news.id)
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
    var id: Int,
    var title: String,
    var shortInfo: String,
    var longInfo: String,
    var publishedDate: String,
    var createdAt: String,
    var untilDate: String,
    var photoNews: String,
    var goodNews: Int
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
        goodNews = 0
    }
    var id by mutableStateOf(0)
    var title by mutableStateOf("")
    var shortInfo by mutableStateOf("")
    var longInfo by mutableStateOf("")
    var publishedDate by mutableStateOf("")
    var createdAt by mutableStateOf("")
    var untilDate by mutableStateOf("")
    var photoNews by mutableStateOf("")
    var goodNews by mutableStateOf(0)
}

private fun updateGoodNews(newsFormData: NewsFormState, formData: NewsFormData, isChecked: Boolean) {
    newsFormData.goodNews = booleanToInt(isChecked)
    formData.goodNews = booleanToInt(isChecked)
}