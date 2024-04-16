package com.example.sirius.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.*
import com.example.sirius.ui.theme.Green3
import com.example.sirius.ui.theme.Green4

@Composable
fun SingleMessage(message: String, isCurrentUser: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = if (isCurrentUser) Green4 else Green3 ,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = message,
            textAlign = if (isCurrentUser) TextAlign.End else TextAlign.Start,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}