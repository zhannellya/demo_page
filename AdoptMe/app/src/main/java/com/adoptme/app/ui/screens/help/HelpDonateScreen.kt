package com.adoptme.app.ui.screens.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.formatKzt
import com.adoptme.app.data.model.titleRu
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.Blush
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ContentViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HelpDonateScreen(
    onBack: () -> Unit,
    onDonate: (shelterId: String, purpose: String, amount: Int?) -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ContentViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val shelters by vm.shelters.collectAsState()
    val history by vm.donationHistory.collectAsState()
    val defaultShelterId = shelters.firstOrNull()?.id.orEmpty()

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Помощь приютам", onBack = onBack)
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Text(
                        "Каждая помощь кормит, лечит и приближает питомца к дому.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Blush
                    )
                    Spacer(Modifier.height(8.dp))
                }
                items(vm.donations, key = { it.id }) { option ->
                    AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(option.title, style = MaterialTheme.typography.titleLarge, color = PurplePrimary)
                            Spacer(Modifier.height(4.dp))
                            Text(option.description, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(option.amountLabel, style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(12.dp))
                            GradientButton(
                                text = when (option.icon) {
                                    "volunteer" -> "Стать волонтёром"
                                    "custom" -> "Помочь своей суммой"
                                    else -> "Помочь на ${option.amountLabel}"
                                },
                                onClick = {
                                    if (option.icon == "volunteer") {
                                        scope.launch {
                                            snackbar.showSnackbar("Спасибо! Приют свяжется с вами по поводу смен.")
                                        }
                                    } else {
                                        onDonate(
                                            defaultShelterId.ifBlank { "s1" },
                                            option.purposeKey,
                                            option.amountKzt?.takeIf { it > 0 }
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                if (history.isNotEmpty()) {
                    item {
                        Text("История пожертвований", style = MaterialTheme.typography.titleLarge, color = Blush)
                    }
                    items(history, key = { it.id }) { donation ->
                        val shelterName = shelters.find { it.id == donation.shelterId }?.name ?: donation.shelterId
                        AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(donation.purpose, style = MaterialTheme.typography.titleMedium, color = PurplePrimary)
                                Text("$shelterName · ${formatKzt(donation.amountKzt)}", color = TextSecondary)
                                Text(
                                    SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("ru")).format(Date(donation.createdAt)),
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(donation.status.titleRu(), color = PurplePrimary)
                            }
                        }
                    }
                }
            }
            SnackbarHost(hostState = snackbar)
        }
    }
}
