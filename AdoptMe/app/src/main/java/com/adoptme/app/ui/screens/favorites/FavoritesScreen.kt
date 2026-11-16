package com.adoptme.app.ui.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AnimalCard
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(onOpenAnimal: (String) -> Unit, onBrowse: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: FavoritesViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val animals by vm.animals.collectAsState()

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Избранное")
            if (animals.isEmpty()) {
                EmptyState(
                    title = "Пока пусто",
                    subtitle = "Отметьте сердечко на карточке — список сохранится после перезапуска.",
                    actionLabel = "В каталог",
                    onAction = onBrowse
                )
            } else {
                BoxWithConstraints(Modifier.weight(1f).fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(if (maxWidth > 700.dp) 220.dp else 160.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(animals, key = { it.id }) { animal ->
                            AnimalCard(
                                animal = animal,
                                onClick = { onOpenAnimal(animal.id) },
                                onFavorite = { vm.toggleFavorite(animal.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
