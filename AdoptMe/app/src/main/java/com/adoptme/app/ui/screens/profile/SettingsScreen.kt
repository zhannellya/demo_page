package com.adoptme.app.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.NotificationsViewModel

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: NotificationsViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val enabled by vm.enabled.collectAsState()

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Настройки", onBack)
            Column(Modifier.padding(horizontal = 16.dp)) {
                AdoptMeCard {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Внутренние уведомления", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Предпочтение сохраняется на устройстве. Системные push-уведомления не отправляются.",
                                color = TextSecondary
                            )
                        }
                        Switch(checked = enabled, onCheckedChange = vm::setEnabled)
                    }
                }
                Spacer(Modifier.height(16.dp))
                DemoBanner(
                    "ADOPTME хранит анкеты, избранное и переписку в локальной базе Room. Пароли лежат только в виде PBKDF2-хеша. " +
                        "Сервер, оплата и настоящая карта приютов не подключены."
                )
                Spacer(Modifier.height(16.dp))
                Text("Версия 1.0.0", color = PurplePrimary, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
