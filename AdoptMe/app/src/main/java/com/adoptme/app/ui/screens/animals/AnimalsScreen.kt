package com.adoptme.app.ui.screens.animals

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AnimalCard
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SearchBar
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.CyanNeon
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.AnimalsViewModel

@Composable
fun AnimalsScreen(onOpenAnimal: (String) -> Unit) {
    val container = LocalAppContainer.current
    val vm: AnimalsViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val animals by vm.animals.collectAsState()
    val query by vm.queryState.collectAsState()
    val category by vm.categoryState.collectAsState()

    SoftBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp)
        ) {
            ScreenHeader(title = "Animals")
            SearchBar(
                query = query,
                onQueryChange = vm::setQuery,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryChip(
                    label = "All",
                    selected = category == null,
                    onClick = { vm.setCategory(null) }
                )
                CategoryChip(
                    label = "Cats",
                    selected = category == AnimalType.CAT,
                    onClick = { vm.setCategory(AnimalType.CAT) },
                    accent = PinkNeon
                )
                CategoryChip(
                    label = "Dogs",
                    selected = category == AnimalType.DOG,
                    onClick = { vm.setCategory(AnimalType.DOG) },
                    accent = CyanNeon
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (animals.isEmpty()) {
                EmptyState(
                    title = "No pets found",
                    subtitle = "Try another search or category."
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(animals, key = { animal -> animal.id }) { animal ->
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
