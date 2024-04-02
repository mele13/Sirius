package com.example.sirius.view.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sirius.R
import com.example.sirius.model.News
import com.example.sirius.model.Animal
import com.example.sirius.navigation.Routes
import com.example.sirius.ui.theme.Green2
import com.example.sirius.ui.theme.Green3
import com.example.sirius.ui.theme.Green4
import com.example.sirius.viewmodel.UserViewModel

@SuppressLint("CoroutineCreationDuringComposition", "DiscouragedApi")
@Composable
fun HomeScreen(
    navController: NavController,
    animalList: List<Animal>,
    newsList: List<News>,
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
                // What's New
                Text(
                    text = stringResource(id = R.string.newsIntro),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(6.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(newsList) { news ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (!news.titleNews.contains("lost") && news.goodNews == 0) {
                                NewsItem(news, context)
                            }
                        }
                    }
                }
                // Our friends
                Text(
                    text = stringResource(id = R.string.animalsIntro),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(6.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(animalList) {animal ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                            AnimalItem(animal, context, navController)
                            Text(
                                text = animal.nameAnimal,
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center,
                                softWrap = true,
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    }
                }
                // Lost
                Text(
                    text = stringResource(id = R.string.lostIntro),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(6.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(newsList) {news ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (news.titleNews.contains("lost")) {
                                NewsItem(news, context)
                            }
                        }
                    }
                }
                // Good News
                Text(
                    text = stringResource(id = R.string.goodNewsIntro),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(6.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(newsList) {news ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (news.goodNews == 1) {
                                NewsItem(news, context)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun NewsItem(news: News, context: Context) {
    val resourceName = news.photoNews.substringAfterLast("/")
    val resourceId = context.resources.getIdentifier(
        resourceName.replace(".jpg", ""), "drawable", context.packageName
    )

    if (resourceId != 0) {
        // Si se encontró el recurso, cargar la imagen
        val painter = painterResource(id = resourceId)
        SquareImage(painter = painter)
    } else {
        Log.e("AnimalImage", "Recurso no encontrado para ${news.photoNews}")
    }
    Text(
        text = news.titleNews,
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
        softWrap = true,
        modifier = Modifier.width(100.dp)
    )
}

@Composable
private fun AnimalItem(animal: Animal, context: Context, navController: NavController) {
    val photoPath = animal.photoAnimal
    val firstImagePath = photoPath.split(", ")[0].trim()
    val resourceName = firstImagePath.substringAfterLast("/")
    val resourceId = context.resources.getIdentifier(
        resourceName.replace(".jpg", ""), "drawable", context.packageName
    )

    if (resourceId != 0) {
        var painter = painterResource(id = resourceId)
        SquareImage(painter = painter, onClick = {
            navController.navigate(route = Routes.ANIMALINFO + "/" + animal.id)
        })
    } else {
        Log.e("AnimalImage", "Recurso no encontrado para ${animal.photoAnimal}")
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
