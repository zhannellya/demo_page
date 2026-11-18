package com.adoptme.app.ui.screens.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AnimalCard
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SecondaryButton
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.OnGradient
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.MatchQuizViewModel

@Composable
fun MatchQuizScreen(
    onBack: () -> Unit,
    onOpenAnimal: (String) -> Unit
) {
    val container = LocalAppContainer.current
    val vm: MatchQuizViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Подобрать питомца", onBack = onBack)
            when {
                state.finished -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            "Вам могут подойти",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnGradient
                        )
                    }
                    if (state.matches.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EmptyState("Никого не нашли", "Посмотрите общий поиск.")
                        }
                    } else {
                        items(state.matches, key = { it.id }) { animal ->
                            AnimalCard(
                                animal = animal,
                                onClick = { onOpenAnimal(animal.id) },
                                onFavorite = { vm.toggleFavorite(animal.id) },
                                imageHeight = 130.dp
                            )
                        }
                    }
                }
                else -> Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        when (state.step) {
                            0 -> "Кого вы хотите найти?"
                            1 -> "Какой ритм жизни вам ближе?"
                            else -> "Дома есть дети?"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnGradient
                    )
                    Spacer(Modifier.height(16.dp))
                    when (state.step) {
                        0 -> {
                            GradientButton("Кошку", onClick = { vm.chooseType(AnimalType.CAT) })
                            Spacer(Modifier.height(10.dp))
                            GradientButton("Собаку", onClick = { vm.chooseType(AnimalType.DOG) })
                            Spacer(Modifier.height(10.dp))
                            SecondaryButton("Не важно", onClick = { vm.chooseType(null) })
                        }
                        1 -> {
                            GradientButton("Спокойный", onClick = { vm.chooseEnergy(false) })
                            Spacer(Modifier.height(10.dp))
                            GradientButton("Активный", onClick = { vm.chooseEnergy(true) })
                        }
                        else -> {
                            GradientButton("Да", onClick = { vm.chooseKids(true) })
                            Spacer(Modifier.height(10.dp))
                            GradientButton("Нет", onClick = { vm.chooseKids(false) })
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Ответы никуда не отправляются — только подбор из анкет ADOPTME.", color = Plum.copy(0.85f))
                }
            }
        }
    }
}
