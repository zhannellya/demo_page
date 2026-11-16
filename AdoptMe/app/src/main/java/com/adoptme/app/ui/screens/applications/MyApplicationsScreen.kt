package com.adoptme.app.ui.screens.applications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.components.StatusChip
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.toRussianDateTime
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.MyApplicationsViewModel

@Composable
fun MyApplicationsScreen(onBack: (() -> Unit)? = null) {
    val container = LocalAppContainer.current
    val vm: MyApplicationsViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val applications by vm.applications.collectAsState()
    val message by vm.message.collectAsState()
    var pendingCancel by remember { mutableStateOf<String?>(null) }

    message?.let { text ->
        AlertDialog(
            onDismissRequest = vm::clearMessage,
            title = { Text("Заявки") },
            text = { Text(text) },
            confirmButton = { TextButton(onClick = vm::clearMessage) { Text("Хорошо") } }
        )
    }
    pendingCancel?.let { id ->
        AlertDialog(
            onDismissRequest = { pendingCancel = null },
            title = { Text("Отменить заявку?") },
            text = { Text("Статус станет «Отменено». Повторно отменить её будет нельзя.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.cancel(id)
                    pendingCancel = null
                }) { Text("Отменить заявку") }
            },
            dismissButton = {
                TextButton(onClick = { pendingCancel = null }) { Text("Назад") }
            }
        )
    }

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Мои заявки", onBack)
            if (applications.isEmpty()) {
                EmptyState(
                    title = "Заявок пока нет",
                    subtitle = "Выберите животное и отправьте заявку на усыновление."
                )
            } else {
                LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                    items(applications, key = { it.id }) { application ->
                        AdoptMeCard(Modifier.padding(bottom = 12.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        application.number,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = PurplePrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatusChip(application.status)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(application.animalName, style = MaterialTheme.typography.titleLarge)
                                Text(application.submittedAt.toRussianDateTime(), color = TextSecondary)
                                if (application.status == ApplicationStatus.PENDING) {
                                    TextButton(onClick = { pendingCancel = application.id }) {
                                        Text("Отменить заявку", color = PurplePrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
