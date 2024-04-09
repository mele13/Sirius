package com.example.sirius.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sirius.ui.theme.Black
import com.example.sirius.ui.theme.Wine
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.UserViewModel

@Composable
fun FavoriteIcon(
    isFavorite: Boolean,
    onFavoriteClicked: () -> Unit,
) {
    if (isFavorite) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Wine,
            modifier = Modifier
                //.align(Alignment.TopEnd)
                .clickable { onFavoriteClicked() }
        )
    } else {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = Wine,
            modifier = Modifier
                //.align(Alignment.TopEnd)
                .clickable { onFavoriteClicked() }
        )
    }
}

@Composable
fun FavoriteEditSection(
    isFavorite: Boolean,
    showDialogEdit: Boolean,
    onFavoriteClicked: () -> Unit,
    onEditClicked: () -> Unit,
    item: Any,
    userViewModel: UserViewModel,
    animalViewModel: AnimalViewModel,
) {
    if (showDialogEdit) {
        // Lógica de edición
        val editedName by remember { mutableStateOf("") }
        val editedShortInfo by remember { mutableStateOf("") }
        // Otros estados editados necesarios para la edición
    }

    if (userViewModel.getAuthenticatedUser()?.role?.trim() != "admin") {
        FavoriteIcon(
            isFavorite = isFavorite,
            onFavoriteClicked = onFavoriteClicked,
        )
    } else {
        EditIcon(
            showDialogEdit = showDialogEdit,
            onEditClicked = onEditClicked,
        )
    }
}

@Composable
fun EditIcon(
    showDialogEdit: Boolean,
    onEditClicked: () -> Unit,
) {
    Icon(
        imageVector = Icons.Default.Edit,
        contentDescription = null,
        tint = Black,
        modifier = Modifier
            //.align(Alignment.TopEnd)
            .clickable { onEditClicked() }
    )
    if (showDialogEdit) {

    }
}