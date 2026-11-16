package com.adoptme.app.ui.screens.application

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.formatDateTime
import com.adoptme.app.utils.label
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.MyApplicationsViewModel

@Composable
fun MyApplicationsScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: MyApplicationsViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val items by vm.items.collectAsState()
    val message by vm.message.collectAsState()

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Мои заявки", onBack = onBack)
            message?.let {
                Text(it, modifier = Modifier.padding(horizontal = 16.dp), color = TextSecondary)
            }
            if (items.isEmpty()) {
                EmptyState("Заявок пока нет", "Выберите животное в каталоге и заполните анкету.")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.id }) { application ->
                        AdoptMeCard {
                            Column(Modifier.padding(16.dp)) {
                                Text("№ ${application.number}", fontWeight = FontWeight.Bold, color = PurplePrimary)
                                Text(application.animalName, style = MaterialTheme.typography.titleLarge)
                                Text(formatDateTime(application.submittedAt), color = TextSecondary)
                                Spacer(Modifier.height(6.dp))
                                Text(application.status.label(), fontWeight = FontWeight.SemiBold)
                                if (application.status == ApplicationStatus.PENDING) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        TextButton(onClick = { vm.cancel(application.id) }) {
                                            Text("Отменить", color = PurplePrimary)
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
}
