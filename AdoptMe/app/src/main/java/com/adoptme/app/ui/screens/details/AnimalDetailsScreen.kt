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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.HealthBadge
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.LavenderMist
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.PurplePrimary
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
    val animal by vm.animal.collectAsState()

    SoftBackground {
        val pet = animal
        if (pet == null) {
            EmptyState(
                title = "Pet not found",
                subtitle = "This listing may have been adopted."
            )
        } else {
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
                                contentDescription = "Favorite",
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
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(LavenderMist)
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
                            Column {
                                Text(
                                    text = pet.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = PurplePrimary
                                )
                                Text(
                                    text = pet.breed + " · " + pet.age,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            HealthBadge(status = pet.healthStatus)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        InfoRow(
                            label = "Gender",
                            value = pet.gender.name.lowercase().replaceFirstChar { it.uppercase() }
                        )
                        InfoRow(label = "Size", value = pet.size)
                        InfoRow(label = "Location", value = pet.location)
                        InfoRow(label = "Vaccinated", value = if (pet.vaccinated) "Yes" else "No")
                        InfoRow(label = "Sterilized", value = if (pet.sterilized) "Yes" else "No")
                        InfoRow(label = "Good with kids", value = if (pet.goodWithKids) "Yes" else "No")
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "About", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = pet.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
                Box(modifier = Modifier.padding(20.dp)) {
                    GradientButton(
                        text = "Adopt " + pet.name,
                        onClick = { onAdopt(pet.id, pet.name) }
                    )
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
        Text(text = label, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.titleSmall)
    }
}
