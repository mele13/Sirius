package com.example.sirius.view.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sirius.model.Event
import com.example.sirius.model.TypeEvent.adoption
import com.example.sirius.model.TypeEvent.cite
import com.example.sirius.model.TypeEvent.medical
import com.example.sirius.model.TypeEvent.volunteer
import com.example.sirius.model.TypeEvent.worker
import com.example.sirius.model.TypeUser
import com.example.sirius.model.User
import com.example.sirius.ui.theme.Green3

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DiscouragedApi", "CoroutineCreationDuringComposition")
@Composable
fun EventCard(event: Event, user: User) {


    var showDialogEdit = remember { mutableStateOf(false) }
    var showDialogDelete = remember { mutableStateOf(false) }


    val permission = when (event.eventType) {
        volunteer -> user.role != TypeUser.volunteer || user.id == event.userId
        worker -> user.role == TypeUser.admin || user.role == TypeUser.owner ||
                (user.role == TypeUser.worker && user.id == event.userId)
        cite -> user.role == TypeUser.admin || user.role == TypeUser.owner || user.id == event.userId
        medical -> TODO()
        adoption -> user.role == TypeUser.admin || user.role == TypeUser.owner ||
                (user.role == TypeUser.worker && user.id == event.userId)
    }

    if (permission) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            border = BorderStroke(1.dp, Green3),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxSize()
                ) {
                    IconButton(onClick = { showDialogDelete.value = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    IconButton(onClick = { showDialogEdit.value = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(30.dp)

                        )
                    }
                }
                if (showDialogEdit.value) {

                    EditEventDialog(showDialogEdit = showDialogEdit, event = event)
                }

                if (showDialogDelete.value) {

                    DeleteDialog(onDismissRequest = { showDialogDelete.value = false }, titleDialog = "Delete event" , item = event)
                }
                Column {
                    Text(text = event.titleEvent, Modifier.padding(4.dp))
                    Text(text = event.descriptionEvent, Modifier.padding(4.dp))
                    Text(text = event.dateEvent, Modifier.padding(4.dp))
                }

            }
        }
    }
}

