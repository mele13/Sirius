package com.example.sirius.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.example.sirius.model.TypeUser
import com.example.sirius.model.User
import com.example.sirius.ui.theme.Green1
import com.example.sirius.view.screens.getDrawableResourceId
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun ListEmployed(viewModel: UserViewModel) {
    var employers by remember { mutableStateOf(emptyList<User>()) }
    val user = viewModel.getAuthenticatedUser()
    LaunchedEffect(key1 = Unit) {
        if (user != null) {
            employers = user.shelterId?.let { viewModel.getAllEmployers(it) }!!
        }
    }

    val dropdownItems = listOf(TypeUser.worker, TypeUser.volunteer)
    val textState = remember { mutableStateOf(TextFieldValue(""))}

    Column(Modifier.fillMaxSize()) {
        BarSearch(state= textState, placeHolder= "Search here...", modifier = Modifier)
        val searchedText = textState.value.text
        val filteredWorkers = employers.filter {
            it.username.contains(searchedText, ignoreCase = true)
        }
        if (filteredWorkers.isEmpty()) {
            Text(
                text = "No matching results found",
                modifier = Modifier.padding(10.dp)
            )
        } else {
            var searchedText = textState.value.text

            if (filteredWorkers.isEmpty() && searchedText.isNotEmpty()) {
                Text(
                    text = searchedText,
                    modifier = Modifier.padding(10.dp)
                )
            } else {
                Column {
                    filteredWorkers.forEach { user ->
                        Row(
                            modifier = Modifier
                                .background(Green1)
                                .padding(8.dp)
                                .clickable {
                                    textState.value = TextFieldValue(user.username)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = getDrawableResourceId(imagePath = user.photoUser)),
                                contentDescription = null,
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .size(55.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = user.username,
                                modifier = Modifier.weight(1f),
                                fontSize = 14.sp
                            )
                            val expanded = remember { mutableStateOf(false) }
                            Row(
                                modifier = Modifier
                                    .border(1.dp, Color.Black)
                                    .clickable(onClick = { expanded.value = !expanded.value })
                                    .width(90.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = user.role.toString(),
                                    color = Color.Black,
                                )

                                IconButton(
                                    onClick = { expanded.value = !expanded.value },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded.value,
                                    onDismissRequest = { expanded.value = false },
                                ) {
                                    dropdownItems.forEach { item ->
                                        DropdownMenuItem(
                                            {
                                                Text(text = item.toString())
                                            },
                                            onClick = {
                                                expanded.value = false
                                                viewModel.viewModelScope.launch {
                                                    user.let { viewModel.updateRole(it, item) }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}