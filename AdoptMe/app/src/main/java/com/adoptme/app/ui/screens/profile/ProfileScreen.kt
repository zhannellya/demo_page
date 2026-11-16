package com.adoptme.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SecondaryButton
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.screens.applications.statusColor
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.PurpleGradient
import com.adoptme.app.ui.theme.SuccessGreen
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ProfileViewModel
import com.adoptme.app.data.model.titleRu

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onApplications: () -> Unit,
    onNotifications: () -> Unit,
    onHelp: () -> Unit,
    onLostFound: () -> Unit,
    onVeterinary: () -> Unit,
    onMap: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ProfileViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val user by vm.user.collectAsState()
    val applications by vm.applications.collectAsState()
    val edit by vm.edit.collectAsState()
    var confirmLogout by remember { mutableStateOf(false) }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text("Выйти из аккаунта?") },
            text = { Text("Избранное и заявки останутся на этом устройстве.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogout = false
                    vm.logout(onLogout)
                }) { Text("Выйти") }
            },
            dismissButton = {
                TextButton(onClick = { confirmLogout = false }) { Text("Отмена") }
            }
        )
    }

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Профиль")
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(14.dp)
            ) {
                item {
                    AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.foundation.layout.Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(PurpleGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.name.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                                        color = White,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                                Spacer(Modifier.size(16.dp))
                                Column {
                                    Text(user.name.ifBlank { "Гость" }, style = MaterialTheme.typography.headlineSmall, color = Plum)
                                    Text(user.email.ifBlank { "Нет email" }, color = TextSecondary)
                                    Text(
                                        text = user.phone.ifBlank { "Телефон не указан" },
                                        color = TextSecondary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = user.city.ifBlank { "Город не указан" },
                                        color = TextSecondary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            if (edit.editing) {
                                AdoptTextField(
                                    value = edit.name,
                                    onValueChange = { value -> vm.updateEdit { it.copy(name = value, error = null, message = null) } },
                                    label = "Имя"
                                )
                                Spacer(Modifier.height(8.dp))
                                AdoptTextField(
                                    value = edit.phone,
                                    onValueChange = { value -> vm.updateEdit { it.copy(phone = value, error = null) } },
                                    label = "Телефон"
                                )
                                Spacer(Modifier.height(8.dp))
                                AdoptTextField(
                                    value = edit.city,
                                    onValueChange = { value -> vm.updateEdit { it.copy(city = value, error = null) } },
                                    label = "Город"
                                )
                                edit.error?.let {
                                    Spacer(Modifier.height(8.dp))
                                    Text(it, color = ErrorRose)
                                }
                                Spacer(Modifier.height(12.dp))
                                GradientButton(text = "Сохранить", onClick = vm::save, loading = edit.saving)
                                Spacer(Modifier.height(8.dp))
                                SecondaryButton(text = "Отмена", onClick = vm::cancelEdit)
                            } else {
                                edit.message?.let {
                                    Text(it, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(8.dp))
                                }
                                GradientButton(text = "Редактировать профиль", onClick = vm::startEdit)
                            }
                        }
                    }
                }
                item {
                    AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            ProfileLink("Мои заявки (${applications.items.size})", onApplications)
                            ProfileLink("Уведомления", onNotifications)
                            ProfileLink("Приюты", onMap)
                            ProfileLink("Потеряшки", onLostFound)
                            ProfileLink("Помощь приютам", onHelp)
                            ProfileLink("Ветеринар", onVeterinary)
                        }
                    }
                }
                if (applications.items.isNotEmpty()) {
                    val latest = applications.items.first()
                    item {
                        AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text("Последняя заявка", style = MaterialTheme.typography.titleMedium, color = Plum)
                                Spacer(Modifier.height(6.dp))
                                Text(latest.animalName, style = MaterialTheme.typography.titleLarge)
                                Text(
                                    latest.status.titleRu(),
                                    color = statusColor(latest.status),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                item {
                    GradientButton(text = "Выйти", onClick = { confirmLogout = true })
                }
            }
        }
    }
}

@Composable
private fun ProfileLink(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(label, color = Plum, modifier = Modifier.fillMaxWidth())
    }
}
