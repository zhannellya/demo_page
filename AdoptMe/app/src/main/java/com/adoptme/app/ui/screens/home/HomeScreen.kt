package com.adoptme.app.ui.screens.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.PetSize
import com.adoptme.app.data.model.Species
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AnimalCard
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.SearchBar
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.label
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.HomeViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onOpenAnimal: (String) -> Unit,
    onNews: () -> Unit,
    onHelp: () -> Unit,
    onApplications: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: HomeViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()
    var filtersOpen by remember { mutableStateOf(false) }
    val greeting = state.userName.ifBlank { "друг" }

    SoftBackground {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val cell = if (maxWidth > 700.dp) 220.dp else 160.dp
            LazyVerticalGrid(
                columns = GridCells.Adaptive(cell),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text("Привет, $greeting", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Кого хотите забрать домой?", color = TextSecondary)
                        Spacer(Modifier.height(10.dp))
                        DemoBanner()
                        Spacer(Modifier.height(10.dp))
                        SearchBar(query = state.query.name, onQueryChange = vm::setName)
                        Spacer(Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CategoryChip("Все", state.query.species == null) { vm.setSpecies(null) }
                            Species.entries.forEach { species ->
                                CategoryChip(species.label(), state.query.species == species) {
                                    vm.setSpecies(if (state.query.species == species) null else species)
                                }
                            }
                        }
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            TextButton(onClick = { filtersOpen = !filtersOpen }) {
                                Text(if (filtersOpen) "Скрыть фильтры" else "Фильтры", color = PurplePrimary)
                            }
                            TextButton(onClick = onApplications) { Text("Мои заявки", color = PurplePrimary) }
                            TextButton(onClick = onNews) { Text("Новости", color = PurplePrimary) }
                            TextButton(onClick = onHelp) { Text("Помощь", color = PurplePrimary) }
                        }
                        if (filtersOpen) {
                            Text("Возраст", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
                            ChipRow {
                                AgeGroup.entries.forEach { group ->
                                    CategoryChip(group.label(), state.query.ageGroup == group) {
                                        vm.setAge(if (state.query.ageGroup == group) null else group)
                                    }
                                }
                            }
                            Text("Размер", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
                            ChipRow {
                                PetSize.entries.forEach { size ->
                                    CategoryChip(size.label(), state.query.size == size) {
                                        vm.setSize(if (state.query.size == size) null else size)
                                    }
                                }
                            }
                            Text("Пол", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
                            ChipRow {
                                Gender.entries.forEach { gender ->
                                    CategoryChip(gender.label(), state.query.gender == gender) {
                                        vm.setGender(if (state.query.gender == gender) null else gender)
                                    }
                                }
                            }
                            Text("Город", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
                            ChipRow {
                                state.cities.forEach { city ->
                                    CategoryChip(city, state.query.city == city) {
                                        vm.setCity(if (state.query.city == city) null else city)
                                    }
                                }
                            }
                            TextButton(onClick = vm::clearFilters) { Text("Сбросить фильтры", color = PurplePrimary) }
                        }
                    }
                }
                if (state.animals.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EmptyState(
                            title = "Никого не нашли",
                            subtitle = "Измените кличку или снимите часть фильтров."
                        )
                    }
                } else {
                    items(state.animals, key = { it.id }) { animal ->
                        AnimalCard(
                            animal = animal,
                            onClick = { onOpenAnimal(animal.id) },
                            onFavorite = { vm.toggleFavorite(animal.id) },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipRow(content: @Composable () -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = { content() }
    )
}
