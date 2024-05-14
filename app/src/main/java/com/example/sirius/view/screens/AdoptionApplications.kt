package com.example.sirius.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sirius.model.Event
import com.example.sirius.viewmodel.ChatViewModel
import com.example.sirius.viewmodel.EventViewModel
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun AdoptionApplications(userViewModel : UserViewModel, chatViewModel : ChatViewModel){

    val user by remember { mutableStateOf(userViewModel.getAuthenticatedUser()) }
    val eventViewModel: EventViewModel = viewModel(factory = EventViewModel.factory)
    val eventList = eventViewModel.getUnallocatedEvents().collectAsState(initial = emptyList()).value

    val mutableEventList = eventList.toMutableList()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                items(mutableEventList) {event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.fillMaxHeight()) {
                                Text(
                                    text = event.titleEvent,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight(400),
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(10.dp)
                                )

                                Text(
                                    text = event.descriptionEvent,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight(400),
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(10.dp)
                                )


                            }

                            Column(Modifier.fillMaxHeight()) {
                                Button(onClick = {
                                      user?.let {
                                          chatViewModel.addMessageAdoption(event.requestingUser, event)
                                      }

                                    eventViewModel.viewModelScope.launch {
                                        eventViewModel.updateEvent(
                                            Event(
                                                id = event.id,
                                                dateEvent = event.dateEvent,
                                                descriptionEvent =  event.descriptionEvent,
                                                titleEvent =  event.titleEvent,
                                                eventType =  event.eventType,
                                                userId = user?.id,
                                                requestingUser = event.requestingUser
                                            )
                                        )
                                    }



                                    mutableEventList.remove(event)

                                }) {

                                    Text(text = "Assign")

                                }

                                Button(onClick = {
                                    eventViewModel.viewModelScope.launch {
                                        eventViewModel.deleteEvent(event)
                                    }

                                }) {

                                    Text(text = "Decline")

                                }


                            }
                            

                            
                        }
                    }
                }
            }
        }
    }
}