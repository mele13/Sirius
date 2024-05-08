package com.example.sirius.view.screens

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sirius.R
import com.example.sirius.model.Management
import com.example.sirius.ui.theme.Green3
import com.example.sirius.ui.theme.Green4
import com.example.sirius.view.components.BarSearch
import com.example.sirius.viewmodel.ManagementViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HandlingScreen(id: Int?) {

    val managementViewModel: ManagementViewModel = viewModel(factory = ManagementViewModel.factory)
    val lastMovements = id?.let { managementViewModel.getLastMovements(it).collectAsState(initial = emptyList()) }
    val movements = id?.let { managementViewModel.getMovements(it).collectAsState(initial = emptyList()).value }

    val textState = remember { mutableStateOf(TextFieldValue("")) }

    var selectedButton = remember { mutableStateOf("Expenses") }

    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        DownloadMovements(context, managementViewModel)
        Text(
            text = "Last movement",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(15.dp)
        )

        if (lastMovements != null) {
           Movement(movements = managementViewModel.getMovementsForCurrentMonth(movements))

        }
        BarSearch(state = textState, placeHolder = "Search here...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp))
        val searchedText = textState.value.text

        Buttons(selectedButton)
        if (movements != null) {
            PaymentHistory(movements, selectedButton, searchedText)
            Summary(movements, managementViewModel)

        }

    }
}

@Composable
fun DownloadMovements(context : Context, managementViewModel: ManagementViewModel){
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Download movements",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(15.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.download),
            contentDescription = "Download",
            modifier = Modifier
                .size(24.dp)
                .clickable {

                    managementViewModel.viewModelScope.launch {
                        managementViewModel.exportTableAText(
                            context = context,
                            "movements.txt"
                        )
                        Toast
                            .makeText(
                                context,
                                "The file has been exported successfully.",
                                Toast.LENGTH_LONG
                            )
                            .show()
                    }
                }
        )
    }
}

@Composable
fun Buttons(selectedButton: MutableState<String>){
    Row(modifier = Modifier.fillMaxWidth() ,verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.Center
    ) {


        Button(onClick = { selectedButton.value = "Expenses" },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (selectedButton.value == "Expenses") Green3 else Color.White,
                contentColor = if (selectedButton.value == "Expenses") Color.Black else Color.Black.copy(alpha = 0.5f)
            ),
            border = BorderStroke(0.dp, Color.Transparent),
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                text = "Expenses",
                style = TextStyle(
                    fontSize = 20.sp,
                ),

                )
        }
        Button(onClick = { selectedButton.value = "Incomes" },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (selectedButton.value == "Incomes") Green3 else Color.White,
                contentColor = if (selectedButton.value == "Incomes") Color.Black else Color.Black.copy(alpha = 0.5f)
            ),
            border = BorderStroke(0.dp, Color.Transparent),
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                text = "Income",
                style = TextStyle(
                    fontSize = 20.sp,
                ),

                )
        }
    }
}
@Composable
fun PaymentHistory(movements: List<Management?>, selectedButton : MutableState<String>, searchedText : String){
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp), contentAlignment = Alignment.Center) {
        LazyColumn(modifier = Modifier
            .width(400.dp)
            .height(200.dp)) {
            if (movements != null) {
                items(
                    items = movements.filter {
                        if (selectedButton.value == "Expenses") {
                            it?.value?.startsWith("-") ?: false &&
                                    it?.description?.contains(searchedText, ignoreCase = true)?: false
                        } else {
                            !it?.value?.startsWith("-")!! &&
                                    it.description.contains(searchedText, ignoreCase = true)

                        }
                    },
                    key = { it?.id ?: 0 }
                ) { item ->
                    MovementItem(movement = item)
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Summary(movements: List<Management?>, managementViewModel: ManagementViewModel){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 20.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Balance",
                style = TextStyle(
                    fontSize = 20.sp,
                ),
            )
            val balance = managementViewModel.calculateBalance(movements)
            Text(
                text = "$balance$",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = if (balance < 0) Color.Red else Green4
                ),
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total",
                style = TextStyle(
                    fontSize = 20.sp,
                ),
            )
            val total = managementViewModel.calculateTotal(movements)
            Text(
                text = "$total$",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = if (total < 0) Color.Red else Green4
                ),
            )
        }
    }
}
@Composable
fun MovementItem(movement: Management?) {
    movement?.let {
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = it.description,
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                )

                Text(
                    text = it.value,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight(400),
                        color = if (it.value.startsWith("-")) Color.Red else Green3,
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun Movement(movements: List<Management?>) {
    LazyColumn(Modifier.height(100.dp)) {
        items(movements) { movement ->
            MovementItem(movement = movement)
        }
    }
}