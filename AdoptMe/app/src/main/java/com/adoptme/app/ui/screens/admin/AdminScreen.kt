package com.adoptme.app.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.AnimalStatus
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.PetSize
import com.adoptme.app.data.model.Species
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.formatDateTime
import com.adoptme.app.utils.label
import com.adoptme.app.viewmodel.AdminViewModel
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.AnimalEditorViewModel
import com.adoptme.app.viewmodel.ApplicationDetailViewModel

@Composable
fun AdminScreen(
    onBack: () -> Unit,
    onOpenApplication: (String) -> Unit,
    onEditAnimal: (String) -> Unit,
    onCreateAnimal: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: AdminViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()

    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            ScreenHeader("Администратор", onBack = onBack)
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DemoBanner("Локальная роль. Одобрение заявки меняет запись в Room на этом устройстве и не проходит серверную проверку.")
                Text("Заявки", style = MaterialTheme.typography.titleLarge)
                if (state.applications.isEmpty()) {
                    Text("Заявок нет.", color = TextSecondary)
                }
                state.applications.forEach { application ->
                    AdoptMeCard(modifier = Modifier.clickable { onOpenApplication(application.id) }) {
                        Column(Modifier.padding(16.dp)) {
                            Text("№ ${application.number}", fontWeight = FontWeight.Bold)
                            Text("${application.animalName} · ${application.applicantName}")
                            Text(application.status.label(), color = TextSecondary)
                            Text(formatDateTime(application.submittedAt), color = TextSecondary)
                        }
                    }
                }
                Text("Животные", style = MaterialTheme.typography.titleLarge)
                GradientButton(text = "Добавить животное", onClick = onCreateAnimal)
                state.animals.forEach { animal ->
                    AdoptMeCard(modifier = Modifier.clickable { onEditAnimal(animal.id) }) {
                        Column(Modifier.padding(16.dp)) {
                            Text(animal.name, style = MaterialTheme.typography.titleMedium)
                            Text("${animal.city} · ${animal.status.label()}", color = TextSecondary)
                            FlowRowStatuses(animal.id, animal.status) { status ->
                                vm.setAnimalStatus(animal.id, status)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowStatuses(id: String, current: AnimalStatus, onSelect: (AnimalStatus) -> Unit) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        AnimalStatus.entries.forEach { status ->
            CategoryChip(status.label(), current == status) { onSelect(status) }
        }
    }
}

@Composable
fun ApplicationReviewScreen(applicationId: String, onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val details: ApplicationDetailViewModel = viewModel(factory = AdoptMeViewModelFactory(container, applicationId))
    val admin: AdminViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val application by details.application.collectAsState()
    val item = application

    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader("Заявка", onBack = onBack)
            if (item == null) {
                Text("Заявка не найдена.", modifier = Modifier.padding(16.dp))
            } else {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("№ ${item.number}", style = MaterialTheme.typography.headlineSmall)
                    Text("Статус: ${item.status.label()}", fontWeight = FontWeight.SemiBold)
                    Info("Животное", item.animalName)
                    Info("Заявитель", item.applicantName)
                    Info("Телефон", item.phone)
                    Info("Email", item.email)
                    Info("Адрес", item.address)
                    Info("Опыт", item.experience)
                    Info("Причина", item.reason)
                    Info("Дата", formatDateTime(item.submittedAt))
                    if (item.status == ApplicationStatus.PENDING) {
                        Spacer(Modifier.height(8.dp))
                        GradientButton(
                            text = "Одобрить",
                            onClick = { admin.setApplicationStatus(item.id, ApplicationStatus.APPROVED) }
                        )
                        Spacer(Modifier.height(8.dp))
                        GradientButton(
                            text = "Отклонить",
                            onClick = { admin.setApplicationStatus(item.id, ApplicationStatus.REJECTED) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Info(title: String, value: String) {
    Column {
        Text(title, color = TextSecondary, style = MaterialTheme.typography.labelMedium)
        Text(value)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimalEditorScreen(animalId: String, onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: AnimalEditorViewModel = viewModel(factory = AdoptMeViewModelFactory(container, animalId))
    val state by vm.state.collectAsState()
    val shelters by vm.shelters.collectAsState()
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 16.dp)
        ) {
            ScreenHeader(if (animalId == "new") "Новое животное" else "Редактирование", onBack = onBack)
            AdoptTextField(state.name, { value -> vm.update { it.copy(name = value) } }, "Кличка")
            Spacer(Modifier.height(8.dp))
            Text("Вид", color = TextSecondary)
            androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Species.entries.forEach { species ->
                    CategoryChip(species.label(), state.species == species) { vm.update { it.copy(species = species) } }
                }
            }
            Spacer(Modifier.height(8.dp))
            AdoptTextField(state.breed, { value -> vm.update { it.copy(breed = value) } }, "Порода")
            Spacer(Modifier.height(8.dp))
            AdoptTextField(state.ageMonths, { value -> vm.update { it.copy(ageMonths = value) } }, "Возраст, месяцы")
            Spacer(Modifier.height(8.dp))
            AdoptTextField(state.ageLabel, { value -> vm.update { it.copy(ageLabel = value) } }, "Возраст словами")
            Spacer(Modifier.height(8.dp))
            Text("Пол", color = TextSecondary)
            androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Gender.entries.forEach { gender ->
                    CategoryChip(gender.label(), state.gender == gender) { vm.update { it.copy(gender = gender) } }
                }
            }
            Text("Размер", color = TextSecondary)
            androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PetSize.entries.forEach { size ->
                    CategoryChip(size.label(), state.size == size) { vm.update { it.copy(size = size) } }
                }
            }
            Text("Статус", color = TextSecondary)
            androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnimalStatus.entries.forEach { status ->
                    CategoryChip(status.label(), state.status == status) { vm.update { it.copy(status = status) } }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Приют", color = TextSecondary)
            androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                shelters.forEach { shelter ->
                    CategoryChip("${shelter.name}, ${shelter.city}", state.shelterId == shelter.id) {
                        vm.update { it.copy(shelterId = shelter.id, city = shelter.city) }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            AdoptTextField(state.temperament, { value -> vm.update { it.copy(temperament = value) } }, "Характер", singleLine = false)
            Spacer(Modifier.height(8.dp))
            AdoptTextField(state.story, { value -> vm.update { it.copy(story = value) } }, "История", singleLine = false)
            Spacer(Modifier.height(8.dp))
            AdoptTextField(state.imageKey, { value -> vm.update { it.copy(imageKey = value) } }, "Имя изображения в drawable")
            state.error?.let { Text(it, color = ErrorRose, modifier = Modifier.padding(top = 8.dp)) }
            Spacer(Modifier.height(16.dp))
            GradientButton(text = "Сохранить", onClick = vm::save, enabled = !state.saved)
            Spacer(Modifier.height(24.dp))
        }
    }
}
