package com.example.sirius.view.components

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sirius.model.Animal
import com.example.sirius.viewmodel.AnimalViewModel

@Composable
fun EditAnimalDialog(
    item : Animal
) {
    val animalViewModel: AnimalViewModel = viewModel(factory = AnimalViewModel.factory)



}