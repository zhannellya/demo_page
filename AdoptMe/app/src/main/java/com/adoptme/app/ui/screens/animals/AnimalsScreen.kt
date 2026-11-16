package com.adoptme.app.ui.screens.animals

import androidx.compose.runtime.Composable
import com.adoptme.app.ui.screens.home.CatalogScreen

@Composable
fun AnimalsScreen(onOpenAnimal: (String) -> Unit) {
    CatalogScreen(showShortcuts = false, onOpenAnimal = onOpenAnimal)
}
