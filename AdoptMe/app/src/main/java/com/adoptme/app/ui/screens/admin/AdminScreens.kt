package com.adoptme.app.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.local.DemoAccounts
import com.adoptme.app.data.local.SeedData
import com.adoptme.app.data.model.AnimalSize
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Availability
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.components.StatusChip
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.label
import com.adoptme.app.utils.toRussianDateTime
import com.adoptme.app.viewmodel.AdminViewModel
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.AnimalEditorState
import com.adoptme.app.viewmodel.AnimalEditorViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminScreen(
    onBack: () -> Unit,
    onOpenApplication: (String) -> Unit,
    onEditAnimal: (String) -> Unit,
    onCreateAnimal: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: AdminViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val applications by vm.applications.collectAsState()
    val animals by vm.animals.collectAsState()
    val message by vm.message.collectAsState()
    var tab by remember { mutableIntStateOf(0) }

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Администрирование", onBack)
            LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                item {
                    DemoBanner(DemoAccounts.NOTICE)
                    Spacer(Modifier.height(12.dp))
                    message?.let { text ->
                        Text(text, color = PurplePrimary, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CategoryChip("Заявки", tab == 0) { tab = 0 }
                        CategoryChip("Животные", tab == 1) { tab = 1 }
                    }
                    Spacer(Modifier.height(12.dp))
                    if (tab == 1) {
                        GradientButton(text = "Добавить животное", onClick = onCreateAnimal)
                        Spacer(Modifier.height(12.dp))
                    }
                }
                if (tab == 0) {
                    items(applications, key = { it.id }) { application ->
                        AdoptMeCard(Modifier.padding(bottom = 12.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text(application.number, color = PurplePrimary, style = MaterialTheme.typography.titleMedium)
                                Text("${application.animalName} · ${application.applicantName}")
                                Text(application.submittedAt.toRussianDateTime(), color = TextSecondary)
                                Spacer(Modifier.height(8.dp))
                                StatusChip(application.status)
                                Spacer(Modifier.height(8.dp))
                                GradientButton(text = "Открыть", onClick = { onOpenApplication(application.id) })
                            }
                        }
                    }
                } else {
                    items(animals, key = { it.id }) { animal ->
                        AdoptMeCard(Modifier.padding(bottom = 12.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text(animal.name, style = MaterialTheme.typography.titleLarge)
                                Text("${animal.city} · ${animal.availability.label()}", color = TextSecondary)
                                Spacer(Modifier.height(8.dp))
                                GradientButton(text = "Изменить", onClick = { onEditAnimal(animal.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminApplicationScreen(applicationId: String, onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: AdminViewModel = viewModel(
        key = "admin-$applicationId",
        factory = AdoptMeViewModelFactory(container)
    )
    val applications by vm.applications.collectAsState()
    val application = applications.find { it.id == applicationId }

    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            ScreenHeader("Заявка", onBack)
            Column(Modifier.padding(horizontal = 16.dp)) {
                if (application == null) {
                    Text("Заявка не найдена.", color = TextSecondary)
                } else {
                    Text(application.number, style = MaterialTheme.typography.headlineSmall, color = PurplePrimary)
                    Spacer(Modifier.height(8.dp))
                    StatusChip(application.status)
                    Spacer(Modifier.height(12.dp))
                    Info("Животное", application.animalName)
                    Info("Заявитель", application.applicantName)
                    Info("Телефон", application.phone)
                    Info("Почта", application.email)
                    Info("Адрес", application.address)
                    Info("Опыт", application.experience)
                    Info("Причина", application.reason)
                    Info("Дата", application.submittedAt.toRussianDateTime())
                    if (application.status == ApplicationStatus.PENDING) {
                        Spacer(Modifier.height(8.dp))
                        GradientButton(text = "Одобрить", onClick = { vm.review(application.id, true) })
                        Spacer(Modifier.height(8.dp))
                        GradientButton(text = "Отклонить", onClick = { vm.review(application.id, false) })
                    } else {
                        Spacer(Modifier.height(8.dp))
                        Text("Решение уже сохранено.", color = TextSecondary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimalEditorScreen(animalId: String, onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: AnimalEditorViewModel = viewModel(
        key = "editor-$animalId",
        factory = AdoptMeViewModelFactory(container, animalId = animalId)
    )
    val state by vm.state.collectAsState()

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            ScreenHeader(if (animalId == "new") "Новое животное" else "Карточка животного", onBack)
            Column(Modifier.padding(horizontal = 16.dp)) {
                EditorField(state.name, "Кличка") { value -> vm.update { it.copy(name = value, error = null) } }
                ChoiceRow("Вид", AnimalType.entries, state.type, { it.label() }) { value ->
                    vm.update { it.copy(type = value) }
                }
                EditorField(state.breed, "Порода") { value -> vm.update { it.copy(breed = value, error = null) } }
                EditorField(state.ageLabel, "Возраст текстом") { value -> vm.update { it.copy(ageLabel = value) } }
                EditorField(
                    value = state.ageMonths,
                    label = "Возраст в месяцах",
                    keyboardType = KeyboardType.Number
                ) { value -> vm.update { it.copy(ageMonths = value.filter { char -> char.isDigit() }) } }
                ChoiceRow("Пол", Gender.entries, state.gender, { it.label() }) { value -> vm.update { it.copy(gender = value) } }
                ChoiceRow("Размер", AnimalSize.entries, state.size, { it.label() }) { value -> vm.update { it.copy(size = value) } }
                ChoiceRow("Здоровье", HealthStatus.entries, state.health, { it.label() }) { value -> vm.update { it.copy(health = value) } }
                ChoiceRow("Статус", Availability.entries, state.availability, { it.label() }) { value ->
                    vm.update { it.copy(availability = value) }
                }
                Text("Приют", color = PurplePrimary, style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SeedData.shelters.forEach { shelter ->
                        CategoryChip(shelter.city, state.shelterId == shelter.id) {
                            vm.update { it.copy(shelterId = shelter.id) }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                EditorField(state.personality, "Характер", singleLine = false) { value -> vm.update { it.copy(personality = value) } }
                EditorField(state.description, "Описание", singleLine = false) { value -> vm.update { it.copy(description = value) } }
                EditorField(state.story, "История", singleLine = false) { value -> vm.update { it.copy(story = value) } }
                EditorField(state.imageUrl, "Ссылка на фото") { value -> vm.update { it.copy(imageUrl = value) } }
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip("Привит", state.vaccinated) { vm.update { it.copy(vaccinated = !it.vaccinated) } }
                    CategoryChip("Стерилизация", state.sterilized) { vm.update { it.copy(sterilized = !it.sterilized) } }
                    CategoryChip("Ладит с детьми", state.goodWithKids) { vm.update { it.copy(goodWithKids = !it.goodWithKids) } }
                }
                state.error?.let { message ->
                    Spacer(Modifier.height(8.dp))
                    Text(message, color = ErrorRose)
                }
                Spacer(Modifier.height(16.dp))
                GradientButton(text = "Сохранить", onClick = vm::save, loading = state.loading)
            }
        }
    }
}

@Composable
private fun EditorField(
    value: String,
    label: String,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValue: (String) -> Unit
) {
    AdoptTextField(
        value = value,
        onValueChange = onValue,
        label = label,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
    Spacer(Modifier.height(8.dp))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> ChoiceRow(
    title: String,
    values: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit
) {
    Text(title, color = PurplePrimary, style = MaterialTheme.typography.titleSmall)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        values.forEach { value ->
            CategoryChip(label(value), value == selected) { onSelect(value) }
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun Info(title: String, value: String) {
    Text(title, color = PurplePrimary, style = MaterialTheme.typography.labelMedium)
    Text(value, modifier = Modifier.padding(bottom = 8.dp))
}
