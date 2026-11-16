package com.adoptme.app.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AnimalCard
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.SearchBar
import com.adoptme.app.ui.components.SectionTitle
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.Blush
import com.adoptme.app.ui.theme.OnGradient
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onOpenAnimal: (String) -> Unit,
    onSeeAll: () -> Unit,
    onNotifications: () -> Unit,
    onLostFound: () -> Unit,
    onHelp: () -> Unit,
    onVeterinary: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: HomeViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()

    SoftBackground {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Здравствуйте, ${state.userName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Blush
                        )
                        Text(
                            text = "Найдите друга",
                            style = MaterialTheme.typography.headlineLarge,
                            color = OnGradient,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onNotifications) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Уведомления", tint = OnGradient)
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                SearchBar(query = state.query, onQueryChange = vm::setQuery)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryChip("Все", state.selectedCategory == null) { vm.setCategory(null) }
                    CategoryChip("Кошки", state.selectedCategory == AnimalType.CAT, PinkNeon) { vm.setCategory(AnimalType.CAT) }
                    CategoryChip("Собаки", state.selectedCategory == AnimalType.DOG, Plum) { vm.setCategory(AnimalType.DOG) }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle("Быстрые действия", horizontalPadding = 0.dp)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    QuickActionCard("Потеряшки", "Объявления рядом", Modifier.weight(1f), onLostFound)
                    QuickActionCard("Помощь", "Корм и лечение", Modifier.weight(1f), onHelp)
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    QuickActionCard("Ветеринар", "Клиники и советы", Modifier.weight(1f), onVeterinary)
                    QuickActionCard("Поиск", "Фильтры и породы", Modifier.weight(1f), onSeeAll)
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle("Животные", "Все", onSeeAll, horizontalPadding = 0.dp)
            }
            when {
                state.loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    CircularProgressIndicator(color = White, modifier = Modifier.padding(24.dp))
                }
                state.animals.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState("Никого не нашли", "Измените запрос или откройте поиск.")
                }
                else -> items(state.animals, key = { it.id }) { animal ->
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

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AdoptMeCard(modifier = modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Plum)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Plum.copy(alpha = 0.75f))
        }
    }
}
