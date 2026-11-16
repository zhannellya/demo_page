package com.adoptme.app.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.adoptme.app.R
import com.adoptme.app.data.local.SeedData
import com.adoptme.app.data.model.Availability
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.HealthBadge
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.LavenderMist
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextMuted
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.label
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.DetailsViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimalDetailsScreen(
    animalId: String,
    onBack: () -> Unit,
    onAdopt: (String) -> Unit
) {
    val container = LocalAppContainer.current
    val vm: DetailsViewModel = viewModel(
        key = animalId,
        factory = AdoptMeViewModelFactory(container, animalId = animalId)
    )
    val animal by vm.animal.collectAsState()
    val pet = animal

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(
                title = pet?.name ?: "Питомец",
                onBack = onBack,
                actions = {
                    IconButton(onClick = vm::toggleFavorite, enabled = pet != null) {
                        Icon(
                            imageVector = if (pet?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Избранное",
                            tint = if (pet?.isFavorite == true) PinkNeon else TextMuted
                        )
                    }
                }
            )
            if (pet == null) {
                EmptyState("Карточка не найдена", "Это животное больше не в локальном каталоге.")
            } else {
                val shelter = SeedData.shelters.find { it.id == pet.shelterId }
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    AsyncImage(
                        model = pet.imageUrl.takeIf { it.isNotBlank() },
                        contentDescription = pet.name,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_paw),
                        error = painterResource(R.drawable.ic_paw),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(LavenderMist)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(pet.name, style = MaterialTheme.typography.headlineLarge, color = PurplePrimary)
                    Text("${pet.city} · ${pet.shelterName}", color = TextSecondary)
                    Spacer(Modifier.height(12.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CategoryChip(pet.type.label(), true) {}
                        CategoryChip(pet.gender.label(), false) {}
                        CategoryChip(pet.size.label(), false) {}
                        CategoryChip(pet.ageLabel, false) {}
                        CategoryChip(pet.availability.label(), pet.availability == Availability.AVAILABLE) {}
                    }
                    Spacer(Modifier.height(12.dp))
                    HealthBadge(pet.healthStatus)
                    Spacer(Modifier.height(16.dp))
                    DetailBlock("Характер", pet.personality)
                    DetailBlock("Описание", pet.description)
                    DetailBlock("История", pet.story)
                    DetailBlock(
                        "Здоровье",
                        listOf(
                            if (pet.vaccinated) "Прививки отмечены в карточке." else "Сведения о прививках не заполнены.",
                            if (pet.sterilized) "Стерилизация отмечена." else "Стерилизация не отмечена.",
                            if (pet.goodWithKids) "В карточке указано, что ладит с детьми." else "Лучше спокойный дом без маленьких детей."
                        ).joinToString(" ")
                    )
                    AdoptMeCard {
                        Column(Modifier.padding(16.dp)) {
                            Text("Приют", style = MaterialTheme.typography.titleMedium, color = PurplePrimary)
                            Spacer(Modifier.height(6.dp))
                            Text(pet.shelterName, style = MaterialTheme.typography.titleSmall)
                            Text(pet.city, color = TextSecondary)
                            Text(shelter?.hours ?: "График уточняется в чате.", color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(shelter?.description ?: "Сведения о приюте появятся после связи.", color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(shelter?.contactNote ?: SeedData.CONTACT_NOTE, color = TextSecondary)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    val canAdopt = pet.availability == Availability.AVAILABLE
                    if (!canAdopt) {
                        Text(
                            "Подать заявку нельзя: статус «${pet.availability.label()}».",
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    GradientButton(
                        text = "Подать заявку на усыновление",
                        onClick = { onAdopt(pet.id) },
                        enabled = canAdopt
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailBlock(title: String, body: String) {
    Column(Modifier.padding(bottom = 14.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = PurplePrimary)
        Spacer(Modifier.height(4.dp))
        Text(body, style = MaterialTheme.typography.bodyLarge)
    }
}
