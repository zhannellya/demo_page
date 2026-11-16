package com.adoptme.app.ui.screens.applications

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.titleRu
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.CyanNeon
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.SuccessGreen
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.WarningAmber
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ApplicationsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ApplicationsScreen(
    onOpenAnimal: (String) -> Unit,
    onBrowse: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ApplicationsViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Мои заявки")
            when {
                state.loading -> CircularProgressIndicator(color = White, modifier = Modifier.height(48.dp))
                state.items.isEmpty() -> EmptyState(
                    title = "Заявок пока нет",
                    subtitle = "Откройте анкету животного и нажмите «Хочу усыновить».",
                    actionLabel = "Смотреть животных",
                    onAction = onBrowse
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items, key = { it.id }) { application ->
                        AdoptMeCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenAnimal(application.animalId) }
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(application.animalName, style = MaterialTheme.typography.titleLarge, color = Plum)
                                Spacer(Modifier.height(4.dp))
                                Text(formatSubmittedAt(application.submittedAt), style = MaterialTheme.typography.labelMedium)
                                Spacer(Modifier.height(6.dp))
                                Text(application.reason, color = TextSecondary, maxLines = 3)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = application.status.titleRu(),
                                    color = statusColor(application.status),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${application.housingType} · ${application.phone}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatSubmittedAt(millis: Long): String {
    val format = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("ru"))
    return format.format(Date(millis))
}

internal fun statusColor(status: ApplicationStatus) = when (status) {
    ApplicationStatus.PENDING -> WarningAmber
    ApplicationStatus.UNDER_REVIEW -> CyanNeon
    ApplicationStatus.APPROVED -> SuccessGreen
    ApplicationStatus.REJECTED -> ErrorRose
}
