package com.adoptme.app.ui.screens.shelter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.adoptme.app.R
import com.adoptme.app.data.model.ChatParticipantType
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SecondaryButton
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.CyanNeon
import com.adoptme.app.ui.theme.Mauve
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import com.adoptme.app.ui.util.dialPhone
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ShelterDetailViewModel

@Composable
fun ShelterDetailScreen(
    shelterId: String,
    onBack: () -> Unit,
    onChat: (participantId: String, type: ChatParticipantType, name: String) -> Unit,
    onHelp: (shelterId: String) -> Unit,
    onShelterAnimals: (shelterId: String) -> Unit,
    onOpenAnimal: (String) -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ShelterDetailViewModel = viewModel(
        key = "shelter_$shelterId",
        factory = AdoptMeViewModelFactory(container, shelterId = shelterId)
    )
    val state by vm.ui.collectAsState()
    val context = LocalContext.current

    SoftBackground {
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = White)
            }
            state.shelter == null -> Column(Modifier.fillMaxSize()) {
                ScreenHeader(title = "Приют", onBack = onBack)
                EmptyState("Приют не найден", "Эта страница больше недоступна.")
            }
            else -> {
                val shelter = state.shelter!!
                Column(modifier = Modifier.fillMaxSize()) {
                    ScreenHeader(title = shelter.name, onBack = onBack)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp)
                    ) {
                        AsyncImage(
                            model = shelter.logoUri,
                            contentDescription = shelter.name,
                            placeholder = painterResource(R.drawable.ic_paw),
                            error = painterResource(R.drawable.ic_paw),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(Mauve)
                        )
                        Spacer(Modifier.height(16.dp))
                        AdoptMeCard {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(shelter.name, style = MaterialTheme.typography.headlineSmall, color = Plum)
                                InfoLine("Город", shelter.city)
                                InfoLine("Адрес", shelter.address)
                                InfoLine("Телефон", shelter.phone)
                                InfoLine("Режим работы", shelter.workingHours)
                                Spacer(Modifier.height(4.dp))
                                Text("О приюте", style = MaterialTheme.typography.titleMedium, color = Plum)
                                Text(shelter.description, color = TextSecondary)
                                if (shelter.bankDetails.isNotBlank()) {
                                    Spacer(Modifier.height(8.dp))
                                    Text("Банковские реквизиты", style = MaterialTheme.typography.titleMedium, color = Plum)
                                    Text(shelter.bankDetails, color = TextSecondary)
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        GradientButton("Позвонить", onClick = { context.dialPhone(shelter.phone) })
                        Spacer(Modifier.height(10.dp))
                        SecondaryButton(
                            text = "Написать",
                            onClick = { onChat(shelter.id, ChatParticipantType.SHELTER, shelter.name) }
                        )
                        Spacer(Modifier.height(10.dp))
                        GradientButton(text = "Помочь", onClick = { onHelp(shelter.id) })
                        Spacer(Modifier.height(14.dp))
                        AdoptMeCard(
                            modifier = Modifier.clickable { onShelterAnimals(shelter.id) }
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Питомцы приюта", style = MaterialTheme.typography.titleLarge, color = Plum)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Сейчас в приюте: ${state.animals.size}",
                                    color = CyanNeon,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(Modifier.height(6.dp))
                                Text("Откройте список животных только этого приюта.", color = TextSecondary)
                            }
                        }
                        if (state.animals.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            state.animals.take(3).forEach { animal ->
                                AdoptMeCard(
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                        .clickable { onOpenAnimal(animal.id) }
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(animal.name, style = MaterialTheme.typography.titleMedium, color = Plum)
                                        Text("${animal.breed} · ${animal.age}", color = TextSecondary)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary)
        Text(value, color = Plum, modifier = Modifier.padding(start = 12.dp))
    }
}
