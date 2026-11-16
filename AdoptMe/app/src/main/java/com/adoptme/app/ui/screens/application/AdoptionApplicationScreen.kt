package com.adoptme.app.ui.screens.application

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.AdoptionFormViewModel

@Composable
fun AdoptionApplicationScreen(
    animalId: String,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: AdoptionFormViewModel = viewModel(
        key = animalId,
        factory = AdoptMeViewModelFactory(container, animalId = animalId)
    )
    val form by vm.form.collectAsState()

    if (form.submittedNumber != null) {
        AlertDialog(
            onDismissRequest = onSubmitted,
            title = { Text("Заявка отправлена") },
            text = {
                Text(
                    "Номер заявки ${form.submittedNumber}. Статус: На рассмотрении. Заявка сохранена на устройстве и появится в разделе «Мои заявки»."
                )
            },
            confirmButton = {
                TextButton(onClick = onSubmitted) { Text("К моим заявкам") }
            }
        )
    }

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Заявка на усыновление", onBack)
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = form.animal?.name ?: "Животное",
                    style = MaterialTheme.typography.headlineSmall,
                    color = PurplePrimary
                )
                Text(
                    text = form.animal?.let { "${it.breed}, ${it.city}" } ?: "Карточка загружается",
                    color = TextSecondary
                )
                Spacer(Modifier.height(12.dp))
                DemoBanner("Животное выбрано автоматически. Повторное нажатие не создаст вторую заявку, пока первая на рассмотрении.")
                Spacer(Modifier.height(16.dp))
                AdoptTextField(
                    value = form.fullName,
                    onValueChange = { vm.update { state -> state.copy(fullName = it, error = null) } },
                    label = "ФИО",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))
                AdoptTextField(
                    value = form.phone,
                    onValueChange = { vm.update { state -> state.copy(phone = it, error = null) } },
                    label = "Телефон",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))
                AdoptTextField(
                    value = form.email,
                    onValueChange = { vm.update { state -> state.copy(email = it, error = null) } },
                    label = "Электронная почта",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))
                AdoptTextField(
                    value = form.address,
                    onValueChange = { vm.update { state -> state.copy(address = it, error = null) } },
                    label = "Адрес проживания",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))
                AdoptTextField(
                    value = form.experience,
                    onValueChange = { vm.update { state -> state.copy(experience = it, error = null) } },
                    label = "Опыт содержания животных",
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))
                AdoptTextField(
                    value = form.reason,
                    onValueChange = { vm.update { state -> state.copy(reason = it, error = null) } },
                    label = "Причина усыновления",
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                form.error?.let { message ->
                    Spacer(Modifier.height(8.dp))
                    Text(message, color = ErrorRose)
                }
                Spacer(Modifier.height(20.dp))
                GradientButton(
                    text = "Отправить заявку",
                    onClick = vm::submit,
                    enabled = form.submittedNumber == null,
                    loading = form.loading
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
