package com.adoptme.app.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.local.DemoAccounts
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.DividerSoft
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onApplications: () -> Unit,
    onFavorites: () -> Unit,
    onSettings: () -> Unit,
    onNews: () -> Unit,
    onHelp: () -> Unit,
    onChats: () -> Unit,
    onNotifications: () -> Unit,
    onLostFound: () -> Unit,
    onVeterinary: () -> Unit,
    onAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ProfileViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val user by vm.user.collectAsState()
    var confirmLogout by remember { mutableStateOf(false) }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text("Выйти из аккаунта?") },
            text = { Text("Заявки и избранное останутся на устройстве. Войти можно снова с той же почтой и паролем.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogout = false
                    vm.logout()
                    onLogout()
                }) { Text("Выйти") }
            },
            dismissButton = {
                TextButton(onClick = { confirmLogout = false }) { Text("Остаться") }
            }
        )
    }

    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader("Профиль")
            Column(Modifier.padding(horizontal = 16.dp)) {
                AdoptMeCard {
                    Column(Modifier.padding(16.dp)) {
                        Text(user.name.ifBlank { "Гость" }, style = MaterialTheme.typography.headlineSmall, color = PurplePrimary)
                        Text(user.email.ifBlank { "Почта не указана" }, color = TextSecondary)
                        if (user.isAdmin) {
                            Spacer(Modifier.height(8.dp))
                            DemoBanner(DemoAccounts.NOTICE)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                AdoptMeCard {
                    Column {
                        MenuRow("Мои заявки", onApplications)
                        MenuRow("Избранное", onFavorites)
                        MenuRow("Чаты", onChats)
                        MenuRow("Новости", onNews)
                        MenuRow("Помощь", onHelp)
                        MenuRow("Уведомления", onNotifications)
                        MenuRow("Настройки", onSettings)
                        MenuRow("Потерянные и найденные", onLostFound)
                        MenuRow("Ветеринарная помощь", onVeterinary)
                        if (user.isAdmin) {
                            MenuRow("Администрирование", onAdmin)
                        }
                        MenuRow("Выйти", { confirmLogout = true })
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MenuRow(title: String, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text("›", color = TextSecondary)
        }
    }
    HorizontalDivider(color = DividerSoft)
}
