package com.adoptme.app.ui.screens.application

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ApplicationViewModel

@Composable
fun AdoptionApplicationScreen(
    animalId: String,
    animalName: String,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ApplicationViewModel = viewModel(
        key = "apply_$animalId",
        factory = AdoptMeViewModelFactory(container, animalId = animalId, animalName = animalName)
    )
    val form by vm.form.collectAsState()

    LaunchedEffect(form.submitted) {
        if (form.submitted) onSubmitted()
    }

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Заявка на усыновление", onBack = onBack)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Text("Заявка на $animalName", style = MaterialTheme.typography.titleLarge, color = PurplePrimary)
                Spacer(Modifier.height(6.dp))
                Text("Заполните анкету. Приют увидит её в истории ваших заявок, данные сохраняются на устройстве.", color = TextSecondary)
                Spacer(Modifier.height(16.dp))
                AdoptMeCard {
                    Column(modifier = Modifier.padding(20.dp)) {
                        AdoptTextField(
                            value = form.fullName,
                            onValueChange = { vm.update { s -> s.copy(fullName = it, error = null) } },
                            label = "Имя"
                        )
                        Spacer(Modifier.height(10.dp))
                        AdoptTextField(
                            value = form.email,
                            onValueChange = { vm.update { s -> s.copy(email = it, error = null) } },
                            label = "Email",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                        )
                        Spacer(Modifier.height(10.dp))
                        AdoptTextField(
                            value = form.phone,
                            onValueChange = { vm.update { s -> s.copy(phone = it, error = null) } },
                            label = "Телефон",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                        )
                        Spacer(Modifier.height(10.dp))
                        AdoptTextField(
                            value = form.address,
                            onValueChange = { vm.update { s -> s.copy(address = it, error = null) } },
                            label = "Адрес"
                        )
                        Spacer(Modifier.height(14.dp))
                        Text("Жильё", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Квартира", "Дом", "Общее жильё").forEach { type ->
                                CategoryChip(
                                    label = type,
                                    selected = form.housingType == type,
                                    onClick = { vm.update { s -> s.copy(housingType = type) } }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = form.hasOtherPets,
                                onCheckedChange = { checked -> vm.update { s -> s.copy(hasOtherPets = checked) } },
                                colors = CheckboxDefaults.colors(checkedColor = PurplePrimary)
                            )
                            Text("У меня уже есть животные")
                        }
                        AdoptTextField(
                            value = form.experience,
                            onValueChange = { vm.update { s -> s.copy(experience = it) } },
                            label = "Опыт содержания",
                            singleLine = false,
                            minLines = 3
                        )
                        Spacer(Modifier.height(10.dp))
                        AdoptTextField(
                            value = form.reason,
                            onValueChange = { vm.update { s -> s.copy(reason = it, error = null) } },
                            label = "Почему хотите усыновить?",
                            singleLine = false,
                            minLines = 3
                        )
                        form.error?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(it, color = ErrorRose)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                GradientButton("Отправить заявку", onClick = vm::submit, loading = form.loading)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
