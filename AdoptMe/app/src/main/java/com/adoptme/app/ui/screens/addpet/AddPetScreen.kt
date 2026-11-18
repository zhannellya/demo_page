package com.adoptme.app.ui.screens.addpet

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.adoptme.app.R
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.model.KazakhstanCities
import com.adoptme.app.data.model.OwnerType
import com.adoptme.app.data.model.titleRu
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.Mauve
import com.adoptme.app.ui.theme.OnGradient
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.AddPetViewModel

@Composable
fun AddPetScreen(
    onBack: () -> Unit,
    onPublished: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: AddPetViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val form by vm.form.collectAsState()
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) vm.onPhotoPicked(uri)
    }

    LaunchedEffect(form.submitted) {
        if (form.submitted) onPublished()
    }

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Добавить питомца", onBack = onBack)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    "Заполните анкету — питомец появится в заявках, на главной и в поиске.",
                    color = OnGradient,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                AdoptMeCard {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Mauve.copy(alpha = 0.18f))
                                .clickable {
                                    photoPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (form.photoUri.isBlank()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.AddAPhoto, contentDescription = null, tint = Plum)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Добавить фотографию", color = Plum)
                                }
                            } else {
                                AsyncImage(
                                    model = form.photoUri,
                                    contentDescription = "Фото питомца",
                                    placeholder = painterResource(R.drawable.ic_paw),
                                    error = painterResource(R.drawable.ic_paw),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        FieldError(form.fieldErrors["photo"])

                        AdoptTextField(
                            value = form.name,
                            onValueChange = { vm.update { s -> s.copy(name = it, fieldErrors = s.fieldErrors - "name") } },
                            label = "Имя",
                            isError = form.fieldErrors.containsKey("name"),
                            supportingText = form.fieldErrors["name"]
                        )

                        Text("Тип животного", style = MaterialTheme.typography.titleSmall, color = Plum)
                        ChipRow {
                            CategoryChip("Кошка", form.type == AnimalType.CAT) {
                                vm.update { it.copy(type = AnimalType.CAT, fieldErrors = it.fieldErrors - "type") }
                            }
                            CategoryChip("Собака", form.type == AnimalType.DOG) {
                                vm.update { it.copy(type = AnimalType.DOG, fieldErrors = it.fieldErrors - "type") }
                            }
                        }
                        FieldError(form.fieldErrors["type"])

                        AdoptTextField(
                            value = form.breed,
                            onValueChange = { vm.update { s -> s.copy(breed = it) } },
                            label = "Порода"
                        )

                        Text("Пол", style = MaterialTheme.typography.titleSmall, color = Plum)
                        ChipRow {
                            CategoryChip("Мальчик", form.gender == Gender.MALE) {
                                vm.update { it.copy(gender = Gender.MALE) }
                            }
                            CategoryChip("Девочка", form.gender == Gender.FEMALE) {
                                vm.update { it.copy(gender = Gender.FEMALE) }
                            }
                        }

                        AdoptTextField(
                            value = form.age,
                            onValueChange = { vm.update { s -> s.copy(age = it, fieldErrors = s.fieldErrors - "age") } },
                            label = "Возраст",
                            isError = form.fieldErrors.containsKey("age"),
                            supportingText = form.fieldErrors["age"]
                        )

                        Text("Город", style = MaterialTheme.typography.titleSmall, color = Plum)
                        ChipRow {
                            KazakhstanCities.featured.forEach { city ->
                                CategoryChip(city, form.city == city) {
                                    vm.update { it.copy(city = city, fieldErrors = it.fieldErrors - "city") }
                                }
                            }
                        }
                        AdoptTextField(
                            value = form.city,
                            onValueChange = { vm.update { s -> s.copy(city = it, fieldErrors = s.fieldErrors - "city") } },
                            label = "Или введите город Казахстана",
                            isError = form.fieldErrors.containsKey("city"),
                            supportingText = form.fieldErrors["city"]
                        )

                        AdoptTextField(
                            value = form.description,
                            onValueChange = { vm.update { s -> s.copy(description = it, fieldErrors = s.fieldErrors - "description") } },
                            label = "Описание",
                            singleLine = false,
                            minLines = 4,
                            isError = form.fieldErrors.containsKey("description"),
                            supportingText = form.fieldErrors["description"]
                        )

                        Text("Состояние здоровья", style = MaterialTheme.typography.titleSmall, color = Plum)
                        ChipRow {
                            HealthStatus.entries.forEach { status ->
                                CategoryChip(status.titleRu(), form.healthStatus == status) {
                                    vm.update { it.copy(healthStatus = status) }
                                }
                            }
                        }

                        Text("Тип владельца", style = MaterialTheme.typography.titleSmall, color = Plum)
                        ChipRow {
                            CategoryChip("Частное лицо", form.ownerType == OwnerType.PRIVATE) {
                                vm.update { it.copy(ownerType = OwnerType.PRIVATE, fieldErrors = it.fieldErrors - "ownerType") }
                            }
                            CategoryChip("Приют", form.ownerType == OwnerType.SHELTER) {
                                vm.update { it.copy(ownerType = OwnerType.SHELTER, fieldErrors = it.fieldErrors - "ownerType") }
                            }
                        }
                        FieldError(form.fieldErrors["ownerType"])

                        if (form.ownerType == OwnerType.SHELTER) {
                            Text("Название приюта", style = MaterialTheme.typography.titleSmall, color = Plum)
                            ChipRow {
                                form.shelters.forEach { shelter ->
                                    CategoryChip(shelter.name, form.shelterId == shelter.id) {
                                        vm.update {
                                            it.copy(
                                                shelterId = shelter.id,
                                                city = it.city.ifBlank { shelter.city },
                                                fieldErrors = it.fieldErrors - "shelter"
                                            )
                                        }
                                    }
                                }
                            }
                            FieldError(form.fieldErrors["shelter"])
                        }

                        if (form.ownerType == OwnerType.PRIVATE) {
                            AdoptTextField(
                                value = form.ownerPhone,
                                onValueChange = { vm.update { s -> s.copy(ownerPhone = it, fieldErrors = s.fieldErrors - "ownerPhone") } },
                                label = "Контактный номер телефона",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Done
                                ),
                                isError = form.fieldErrors.containsKey("ownerPhone"),
                                supportingText = form.fieldErrors["ownerPhone"]
                            )
                        }

                        form.error?.let { Text(it, color = ErrorRose) }
                    }
                }
                Spacer(Modifier.height(20.dp))
                GradientButton(
                    text = "Опубликовать",
                    onClick = vm::publish,
                    loading = form.submitting || form.loading
                )
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun ChipRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

@Composable
private fun FieldError(message: String?) {
    if (!message.isNullOrBlank()) {
        Text(message, color = ErrorRose, style = MaterialTheme.typography.bodySmall)
    }
}
