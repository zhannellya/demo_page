package com.adoptme.app.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalStatus
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AnimalArtwork
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.components.StatusBadge
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.TextMuted
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.label
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.DetailsViewModel

@Composable
fun AnimalDetailsScreen(
    animalId: String,
    onBack: () -> Unit,
    onAdopt: (String) -> Unit
) {
    val container = LocalAppContainer.current
    val vm: DetailsViewModel = viewModel(
        factory = AdoptMeViewModelFactory(container, animalId)
    )
    val animal by vm.animal.collectAsState()

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(title = animal?.name ?: "Анкета", onBack = onBack) {
                IconButton(onClick = vm::toggleFavorite) {
                    Icon(
                        imageVector = if (animal?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Избранное",
                        tint = if (animal?.isFavorite == true) PinkNeon else TextMuted
                    )
                }
            }
            val pet = animal
            if (pet == null) {
                Text("Анкета загружается…", modifier = Modifier.padding(24.dp), color = TextSecondary)
            } else {
                AnimalDetailsContent(pet, onAdopt)
            }
        }
    }
}

@Composable
private fun AnimalDetailsContent(animal: Animal, onAdopt: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(Modifier.widthIn(max = 640.dp).fillMaxWidth()) {
            AnimalArtwork(
                imageKey = animal.imageKey,
                contentDescription = animal.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(28.dp))
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(animal.name, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
                StatusBadge(animal.status)
            }
            Text("${animal.breed} · ${animal.city}", color = TextSecondary)
            Spacer(Modifier.height(12.dp))
            AdoptMeCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Fact("Возраст", animal.ageLabel)
                    Fact("Порода", animal.breed)
                    Fact("Размер", animal.size.label())
                    Fact("Пол", animal.gender.label())
                    Fact("Характер", animal.temperament)
                    Fact("Вид", animal.species.label())
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("История", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            Text(animal.story, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(12.dp))
            Text("Приют", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            AdoptMeCard {
                Column(Modifier.padding(16.dp)) {
                    Text(animal.shelterName, fontWeight = FontWeight.SemiBold)
                    Text(animal.city, color = TextSecondary)
                    Text(
                        "Точный адрес не показан: у демонстрационных приютов нет проверенных координат.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            val canApply = animal.status != AnimalStatus.ADOPTED
            GradientButton(
                text = if (canApply) "Подать заявку на усыновление" else "Уже нашёл дом",
                enabled = canApply,
                onClick = { onAdopt(animal.id) }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Fact(title: String, value: String) {
    Column {
        Text(title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
