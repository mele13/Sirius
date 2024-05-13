package com.example.sirius.view.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sirius.ui.theme.Orange

@Composable
fun FloatingButton(icon : ImageVector, modifier : Modifier, onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = { onClick() },
        containerColor = Orange,
        modifier = Modifier
            .padding(5.dp)
            .then(modifier),
          //  .background(Color.White),
        shape = CircleShape,

    ) {
        Icon(icon, "Small floating action button.", tint = Color.White)
    }
}