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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.label
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onApplications: () -> Unit,
    onFavorites: () -> Unit,
    onSettings: () -> Unit,
    onHelp: () -> Unit,
    onNews: () -> Unit,
    onAdmin: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ProfileViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val user by vm.user.collectAsState()
    val favorites by vm.favoriteCount.collectAsState()
    val applications by vm.applicationCount.collectAsState()
    val isAdmin by vm.isLocalAdmin.collectAsState()

    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            ScreenHeader("Профиль")
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                DemoBanner()
                AdoptMeCard {
                    Column(Modifier.padding(16.dp)) {
                        Text(user.name.ifBlank { "Гость" }, style = MaterialTheme.typography.headlineSmall)
                        Text(user.email.ifBlank { "Email не указан" }, color = TextSecondary)
                        Text(user.role.label(), color = TextSecondary)
                    }
                }
                MenuRow("Мои заявки", "$applications", onApplications)
                MenuRow("Избранное", "$favorites", onFavorites)
                MenuRow("Новости", "Истории и советы", onNews)
                MenuRow("Помощь", "Волонтёрство и контакты", onHelp)
                MenuRow("Настройки", "Уведомления и режим", onSettings)
                if (isAdmin) {
                    AdoptMeCard(modifier = Modifier.clickable(onClick = onAdmin)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Панель администратора", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Локальная роль на этом устройстве. Это не защищённая серверная авторизация.",
                                color = TextSecondary
                            )
                        }
                    }
                }
                GradientButton(text = "Выйти из аккаунта", onClick = { vm.logout(onLogout) })
            }
        }
    }
}

@Composable
private fun MenuRow(title: String, subtitle: String, onClick: () -> Unit) {
    AdoptMeCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = TextSecondary)
            }
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: ProfileViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val session by vm.session.collectAsState()

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Настройки", onBack = onBack)
            Column(Modifier.padding(16.dp)) {
                AdoptMeCard {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Напоминания", fontWeight = FontWeight.SemiBold)
                            Text("Переключатель хранится локально и не отправляет push.", color = TextSecondary)
                        }
                        Switch(
                            checked = session.notificationsEnabled,
                            onCheckedChange = vm::setNotifications
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Приложение работает в демонстрационном режиме. Заявки, избранное и переписка остаются в Room на устройстве. Firebase Authentication и Firestore можно подключить через интерфейсы в пакете remote.",
                    color = TextSecondary
                )
            }
        }
    }
}
