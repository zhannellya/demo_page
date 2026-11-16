package com.adoptme.app.ui.screens.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextMuted
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.toRussianDateTime
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: NotificationsViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val items by vm.items.collectAsState()
    val enabled by vm.enabled.collectAsState()

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Уведомления", onBack)
            if (!enabled) {
                Text(
                    "Показ внутренних уведомлений выключен в настройках. Записи ниже сохранены.",
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            if (items.isEmpty()) {
                EmptyState("Пока тихо", "Здесь появятся сообщения о заявках.")
            } else {
                LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                    items(items, key = { it.id }) { item ->
                        AdoptMeCard(
                            Modifier
                                .padding(bottom = 12.dp)
                                .clickable { vm.markRead(item.id) }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(item.title, style = MaterialTheme.typography.titleMedium, color = PurplePrimary)
                                Text(item.message, color = if (item.isRead) TextMuted else TextSecondary)
                                Spacer(Modifier.height(4.dp))
                                Text(item.createdAt.toRussianDateTime(), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
