package com.adoptme.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.AnimalSize
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.Gender
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AnimalCard
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.SearchBar
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.label
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.CatalogViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onOpenAnimal: (String) -> Unit,
    onNews: () -> Unit,
    onChat: () -> Unit,
    onHelp: () -> Unit,
    onNotifications: () -> Unit
) {
    CatalogScreen(
        showShortcuts = true,
        onOpenAnimal = onOpenAnimal,
        onNews = onNews,
        onChat = onChat,
        onHelp = onHelp,
        onNotifications = onNotifications
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatalogScreen(
    showShortcuts: Boolean,
    onOpenAnimal: (String) -> Unit,
    onNews: () -> Unit = {},
    onChat: () -> Unit = {},
    onHelp: () -> Unit = {},
    onNotifications: () -> Unit = {}
) {
    val container = LocalAppContainer.current
    val vm: CatalogViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()

    SoftBackground {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val columns = if (maxWidth >= 700.dp) 3 else 2
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(Modifier.padding(top = 12.dp)) {
                        val greeting = state.userName.ifBlank { "друг" }
                        Text(
                            text = "Здравствуйте, $greeting",
                            style = MaterialTheme.typography.headlineSmall,
                            color = PurplePrimary
                        )
                        Text(
                            text = "Найдите животное, которое ждёт дом",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(12.dp))
                        SearchBar(query = state.query.text, onQueryChange = vm::setText)
                        if (showShortcuts) {
                            Spacer(Modifier.height(12.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CategoryChip("Новости", false, onClick = onNews)
                                CategoryChip("Чаты", false, onClick = onChat)
                                CategoryChip("Помощь", false, onClick = onHelp)
                                CategoryChip("Уведомления", false, onClick = onNotifications)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CategoryChip("Все", state.query.type == null) { vm.setType(null) }
                            AnimalType.entries.forEach { type ->
                                CategoryChip(type.label(), state.query.type == type) {
                                    vm.setType(if (state.query.type == type) null else type)
                                }
                            }
                            CategoryChip(
                                label = "Фильтры",
                                selected = state.filtersOpen,
                                onClick = vm::toggleFilters
                            )
                        }
                        if (state.filtersOpen) {
                            Spacer(Modifier.height(12.dp))
                            Text("Возраст", style = MaterialTheme.typography.titleSmall)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                CategoryChip("Любой", state.query.ageGroup == null) { vm.setAge(null) }
                                AgeGroup.entries.forEach { age ->
                                    CategoryChip(age.label(), state.query.ageGroup == age) {
                                        vm.setAge(if (state.query.ageGroup == age) null else age)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Размер", style = MaterialTheme.typography.titleSmall)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                CategoryChip("Любой", state.query.size == null) { vm.setSize(null) }
                                AnimalSize.entries.forEach { size ->
                                    CategoryChip(size.label(), state.query.size == size) {
                                        vm.setSize(if (state.query.size == size) null else size)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Пол", style = MaterialTheme.typography.titleSmall)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                CategoryChip("Любой", state.query.gender == null) { vm.setGender(null) }
                                Gender.entries.forEach { gender ->
                                    CategoryChip(gender.label(), state.query.gender == gender) {
                                        vm.setGender(if (state.query.gender == gender) null else gender)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Город", style = MaterialTheme.typography.titleSmall)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                CategoryChip("Все города", state.query.city == null) { vm.setCity(null) }
                                state.cities.forEach { city ->
                                    CategoryChip(city, state.query.city == city) {
                                        vm.setCity(if (state.query.city == city) null else city)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            CategoryChip("Сбросить фильтры", false, onClick = vm::resetFilters)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
                if (state.animals.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EmptyState(
                            title = "Никого не нашли",
                            subtitle = "Измените кличку или фильтры."
                        )
                    }
                } else {
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
