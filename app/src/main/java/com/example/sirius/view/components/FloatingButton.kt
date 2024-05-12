package com.example.sirius.view.components

import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.sirius.ui.theme.Green4

@Composable
fun FloatingButton(icon : ImageVector, onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = { onClick() },
        containerColor = Green4
    ) {
        Icon(icon, "Small floating action button.")
    }
}