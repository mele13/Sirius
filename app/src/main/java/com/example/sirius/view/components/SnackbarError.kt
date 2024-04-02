package com.example.sirius.view.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.sirius.R

@Composable
fun CustomSnackbar(
    message: String,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Snackbar(
            content = {
                Text(
                    text = message,
                    style = TextStyle(
                        color = if (!isSystemInDarkTheme()) Color.White else Color.Black
                    ),
                )
            },
            action = {
                TextButton(
                    onClick = onDismiss,
                    content = {
                        Text(
                            stringResource(id = R.string.close),
                            color = if (!isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                )
            },
            modifier = Modifier
                .zIndex(99f)
                .offset(y = (-200).dp)
        )
    }
}