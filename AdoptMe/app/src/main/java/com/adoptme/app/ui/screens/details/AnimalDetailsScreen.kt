package com.adoptme.app.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.adoptme.app.R
import com.adoptme.app.data.model.titleRu
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.HealthBadge
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.Mauve
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.TextMuted
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.DetailsViewModel

@Composable
fun AnimalDetailsScreen(
    animalId: String,
    onBack: () -> Unit,
    onAdopt: (String, String) -> Unit
) {
    val container = LocalAppContainer.current
    val vm: DetailsViewModel = viewModel(
        key = "details_$animalId",
        factory = AdoptMeViewModelFactory(container, animalId = animalId)
    )
    val state by vm.ui.collectAsState()

    SoftBackground {
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = White)
            }
            state.animal == null -> Column(Modifier.fillMaxSize()) {
                ScreenHeader(title = "Анкета", onBack = onBack)
                EmptyState(
                    title = "Питомец не найден",
                    subtitle = "Эта анкета больше недоступна."
                )
            }
            else -> {
                val pet = state.animal!!
                Column(modifier = Modifier.fillMaxSize()) {
                    ScreenHeader(
                        title = pet.name,
                        onBack = onBack,
                        actions = {
                            IconButton(
                                onClick = vm::toggleFavorite,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(White, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (pet.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Избранное",
                                    tint = if (pet.isFavorite) PinkNeon else TextMuted
                                )
                            }
                        }
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp)
                    ) {
                        AsyncImage(
                            model = pet.imageUrl,
                            contentDescription = pet.name,
                            placeholder = painterResource(R.drawable.ic_paw),
                            error = painterResource(R.drawable.ic_paw),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(Mauve)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(10.dp, RoundedCornerShape(28.dp))
                                .clip(RoundedCornerShape(28.dp))
                                .background(LilacCard)
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(pet.name, style = MaterialTheme.typography.headlineMedium, color = Plum)
                                    Text("${pet.breed} · ${pet.age}", style = MaterialTheme.typography.bodyMedium)
                                    Text(pet.type.titleRu(), style = MaterialTheme.typography.labelMedium)
                                }
                                HealthBadge(status = pet.healthStatus)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow("Пол", pet.gender.titleRu())
                            InfoRow("Размер", pet.size)
                            InfoRow("Приют", pet.location)
                            InfoRow("Привит", if (pet.vaccinated) "Да" else "Нет")
                            InfoRow("Стерилизован", if (pet.sterilized) "Да" else "Нет")
                            InfoRow("Ладит с детьми", if (pet.goodWithKids) "Да" else "Нет")
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("О питомце", style = MaterialTheme.typography.titleMedium, color = Plum)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(pet.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    Box(modifier = Modifier.padding(20.dp)) {
                        GradientButton(
                            text = "Хочу усыновить",
                            onClick = { onAdopt(pet.id, pet.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        )
    }
}
