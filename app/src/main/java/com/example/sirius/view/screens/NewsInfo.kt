package com.example.sirius.view.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.model.SectionType
import com.example.sirius.model.TypeUser
import com.example.sirius.navigation.Routes
import com.example.sirius.tools.buildAnAgeText
import com.example.sirius.tools.calculateAge
import com.example.sirius.tools.intToBoolean
import com.example.sirius.view.components.NewsFormData
import com.example.sirius.view.components.NewsFormDialog
import com.example.sirius.view.components.rememberNewsFormState
import com.example.sirius.viewmodel.NewsViewModel
import com.example.sirius.viewmodel.UserViewModel

@SuppressLint("DiscouragedApi", "CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsInfo(
    id: Int?,
    viewModel: NewsViewModel,
    userViewModel: UserViewModel,
    navController: NavController,
) {
    val user = userViewModel.getAuthenticatedUser()
    var editMode = remember { mutableStateOf(false) }

    val news by viewModel.getNewsById(id ?: 0).collectAsState(initial = null)
    val context = LocalContext.current
    val userId = userViewModel.getAuthenticatedUser()?.id

    var title by remember { mutableStateOf("") }
    var shortInfoAnimal by remember { mutableStateOf("") }
    var longInfoAnimal by remember { mutableStateOf("") }

    var goodNews by remember { mutableStateOf(false) }


    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {


        title = news?.titleNews.toString()
        shortInfoAnimal = news?.shortInfoNews.toString()
        longInfoAnimal = news?.longInfoNews.toString()
        goodNews = news?.let { intToBoolean(it.goodNews) } == true


        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            if(editMode.value){
                var editedTitle by remember {
                    mutableStateOf(
                        news?.titleNews ?: ""
                    )
                }

                var editedShortInfo by remember {
                    mutableStateOf(
                        news?.shortInfoNews ?: ""
                    )
                }

                var editedLongInfo by remember {
                    mutableStateOf(
                        news?.longInfoNews ?: ""
                    )
                }

                var editedPublishedDate by remember {
                    mutableStateOf(
                        news?.publishedDate ?: ""
                    )
                }

                var editedCreatedAt by remember {
                    mutableStateOf(
                        news?.createdAt ?: ""
                    )
                }

                var editedUntilDate by remember {
                    mutableStateOf(
                        news?.untilDate ?: ""
                    )
                }

                var editedPhotoNews by remember {
                    mutableStateOf(
                        news?.photoNews ?: ""
                    )
                }

                var editedGoodNews by remember {
                    mutableStateOf(
                        goodNews
                    )
                }


                var idNews = news?.id
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
                    showDialogAdd = editMode,
                    newsFormState = newsFormState,
                    sectionType = if(news!!.goodNews == 1) SectionType.GOOD_NEWS else SectionType.WHATS_NEW,
                    newsFormData = newsFormData,
                    isEdit = true,
                ) {

                }
            }
            LazyColumn(
                verticalArrangement = Arrangement.Bottom
            ) {
                if (news != null) {
                    val photoPaths = news!!.photoNews.split(", ").map { it.trim() }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.White)
                        ) {
                            CarouselSlider(photoPaths, news!!, context)
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
                            // Icono sponsor
                            if (user != null && user.role != TypeUser.admin) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate(
                                                route = Routes.SPONSORING + "/${id}-${
                                                    photoPaths[0].substringAfterLast(
                                                        '/'
                                                    )
                                                }-${news!!.titleNews}"
                                            )
                                        }
                                        .align(Alignment.BottomStart)
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
                    }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 20.dp)
                        ) {
                            //  if (!editMode) {
                            Text(
                                text = news!!.titleNews,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                            )

                            if (userId != null) {
                                if (user!!.role == TypeUser.admin || user.role == TypeUser.owner) {

                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .clickable { editMode.value = true }
                                            .size(15.dp)
                                    )

                                }
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
                            // if (!editMode) {
                            Text(
                                text = news!!.longInfoNews,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp)
                            )

                        }
                    }
                    val age = calculateAge(news!!.publishedDate)
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Published date: ${buildAnAgeText(age, news!!.publishedDate)}",
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

}



