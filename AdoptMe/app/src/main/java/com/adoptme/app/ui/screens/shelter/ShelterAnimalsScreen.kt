package com.adoptme.app.ui.screens.shelter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AnimalCard
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ShelterAnimalsViewModel

@Composable
fun ShelterAnimalsScreen(
    shelterId: String,
    onBack: () -> Unit,
    onOpenAnimal: (String) -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ShelterAnimalsViewModel = viewModel(
        key = "shelter_animals_$shelterId",
        factory = AdoptMeViewModelFactory(container, shelterId = shelterId)
    )
    val state by vm.ui.collectAsState()

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(
                title = state.shelter?.name ?: "Питомцы приюта",
                onBack = onBack
            )
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = White)
                }
                state.animals.isEmpty() -> EmptyState(
                    title = "Пока нет питомцев",
                    subtitle = "У этого приюта нет объявлений в общем списке."
                )
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.animals, key = { it.id }) { animal ->
                        AnimalCard(
                            animal = animal,
                            onClick = { onOpenAnimal(animal.id) },
                            onFavorite = { vm.toggleFavorite(animal.id) },
                            imageHeight = 130.dp
                        )
                    }
                }
            }
        }
    }
}
