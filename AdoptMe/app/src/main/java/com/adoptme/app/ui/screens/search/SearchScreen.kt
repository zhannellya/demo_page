package com.adoptme.app.ui.screens.search

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.Gender
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AnimalCard
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SearchBar
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.Blush
import com.adoptme.app.ui.theme.OnGradient
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.SearchViewModel

@Composable
fun SearchScreen(onOpenAnimal: (String) -> Unit) {
    val container = LocalAppContainer.current
    val vm: SearchViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(
                title = "Поиск",
                actions = {
                    TextButton(onClick = vm::clearFilters) {
                        Text("Сбросить", color = OnGradient, fontWeight = FontWeight.SemiBold)
                    }
                }
            )
            SearchBar(
                query = state.query,
                onQueryChange = vm::setQuery,
                modifier = Modifier.padding(horizontal = 20.dp),
                placeholder = "Кошки, собаки, порода, приют"
            )
            Spacer(Modifier.height(12.dp))
            FilterLabel("Кто")
            ChipRow {
                CategoryChip("Все", state.type == null) { vm.setType(null) }
                CategoryChip("Кошки", state.type == AnimalType.CAT, PinkNeon) { vm.setType(AnimalType.CAT) }
                CategoryChip("Собаки", state.type == AnimalType.DOG, Plum) { vm.setType(AnimalType.DOG) }
            }
            FilterLabel("Пол")
            ChipRow {
                CategoryChip("Любой", state.gender == null) { vm.setGender(null) }
                CategoryChip("Мальчик", state.gender == Gender.MALE) { vm.setGender(Gender.MALE) }
                CategoryChip("Девочка", state.gender == Gender.FEMALE) { vm.setGender(Gender.FEMALE) }
            }
            FilterLabel("Возраст")
            ChipRow {
                CategoryChip("Любой", state.ageGroup == null) { vm.setAgeGroup(null) }
                AgeGroup.entries.forEach { group ->
                    CategoryChip(group.title, state.ageGroup == group) { vm.setAgeGroup(group) }
                }
            }
            FilterLabel("Порода")
            ChipRow {
                CategoryChip("Все породы", state.breed == null) { vm.setBreed(null) }
                state.breeds.forEach { breed ->
                    CategoryChip(breed, state.breed == breed) { vm.setBreed(breed) }
                }
            }
            Text(
                text = if (state.loading) "Ищем животных…" else "Найдено: ${state.animals.size}",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                color = Blush,
                style = MaterialTheme.typography.titleSmall
            )
            when {
                state.loading -> CircularProgressIndicator(
                    color = White,
                    modifier = Modifier.padding(24.dp)
                )
                state.animals.isEmpty() -> EmptyState(
                    title = "Никого не нашли",
                    subtitle = "Сбросьте фильтры или измените запрос."
                )
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
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

@Composable
private fun FilterLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
        color = OnGradient,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun ChipRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}
