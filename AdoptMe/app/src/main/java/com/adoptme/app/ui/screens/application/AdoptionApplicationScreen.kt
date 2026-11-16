package com.adoptme.app.ui.screens.application

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ApplicationViewModel

@Composable
fun AdoptionScreen(
    animalId: String,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ApplicationViewModel = viewModel(factory = AdoptMeViewModelFactory(container, animalId))
    val form by vm.form.collectAsState()
    LaunchedEffect(form.submittedNumber) {
        if (form.submittedNumber != null) onSubmitted()
    }

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Заявка на усыновление", onBack = onBack)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(Modifier.widthIn(max = 560.dp).fillMaxWidth()) {
                    AdoptMeCard {
                        Column(Modifier.padding(16.dp)) {
                            Text("Животное", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                            Text(
                                form.animalName.ifBlank { "Выбранная анкета" },
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Анкета подставлена автоматически.", color = TextSecondary)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    AdoptTextField(form.fullName, { value -> vm.update { it.copy(fullName = value) } }, "Имя заявителя")
                    Spacer(Modifier.height(10.dp))
                    AdoptTextField(
                        form.phone,
                        { value -> vm.update { it.copy(phone = value) } },
                        "Телефон",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(Modifier.height(10.dp))
                    AdoptTextField(
                        form.email,
                        { value -> vm.update { it.copy(email = value) } },
                        "Email",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(Modifier.height(10.dp))
                    AdoptTextField(form.address, { value -> vm.update { it.copy(address = value) } }, "Адрес проживания")
                    Spacer(Modifier.height(10.dp))
                    AdoptTextField(
                        form.experience,
                        { value -> vm.update { it.copy(experience = value) } },
                        "Опыт содержания животных",
                        singleLine = false
                    )
                    Spacer(Modifier.height(10.dp))
                    AdoptTextField(
                        form.reason,
                        { value -> vm.update { it.copy(reason = value) } },
                        "Причина усыновления",
                        singleLine = false
                    )
                    form.error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = ErrorRose)
                    }
                    Spacer(Modifier.height(16.dp))
                    GradientButton(
                        text = "Отправить заявку",
                        onClick = vm::submit,
                        enabled = !form.loading && form.submittedNumber == null && !form.unavailable,
                        loading = form.loading
                    )
                    if (form.unavailable) {
                        Spacer(Modifier.height(8.dp))
                        Text("Это животное уже нашло дом.", color = TextSecondary)
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}
