@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.adoptme.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptme.app.data.local.PetPhotoStore
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.ChatMessage
import com.adoptme.app.data.model.ChatParticipantType
import com.adoptme.app.data.model.Donation
import com.adoptme.app.data.model.DonationRequest
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.model.OwnerType
import com.adoptme.app.data.model.PaymentMethod
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.data.repository.AnimalRepository
import com.adoptme.app.data.repository.AuthRepository
import com.adoptme.app.data.repository.ChatRepository
import com.adoptme.app.data.repository.DonationRepository
import com.adoptme.app.data.repository.NewPetDraft
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddPetFormState(
    val photoUri: String = "",
    val name: String = "",
    val type: AnimalType? = null,
    val breed: String = "",
    val gender: Gender? = null,
    val age: String = "",
    val city: String = "",
    val description: String = "",
    val healthStatus: HealthStatus = HealthStatus.HEALTHY,
    val ownerType: OwnerType? = null,
    val shelterId: String? = null,
    val ownerPhone: String = "",
    val fieldErrors: Map<String, String> = emptyMap(),
    val loading: Boolean = false,
    val submitting: Boolean = false,
    val submitted: Boolean = false,
    val error: String? = null,
    val shelters: List<Shelter> = emptyList()
)

class AddPetViewModel(
    private val animalRepository: AnimalRepository,
    private val authRepository: AuthRepository,
    private val petPhotoStore: PetPhotoStore
) : ViewModel() {
    private val _form = MutableStateFlow(AddPetFormState())
    val form: StateFlow<AddPetFormState> = _form.asStateFlow()

    init {
        viewModelScope.launch {
            combine(animalRepository.observeShelters(), authRepository.currentUser) { shelters, user ->
                shelters to user
            }.collect { (shelters, user) ->
                _form.update { current ->
                    current.copy(
                        shelters = shelters,
                        city = current.city.ifBlank { user.city },
                        ownerPhone = current.ownerPhone.ifBlank { user.phone }
                    )
                }
            }
        }
    }

    fun update(transform: (AddPetFormState) -> AddPetFormState) = _form.update(transform)

    fun onPhotoPicked(uri: Uri) = viewModelScope.launch {
        _form.update { it.copy(loading = true, error = null, fieldErrors = it.fieldErrors - "photo") }
        runCatching { petPhotoStore.persist(uri) }
            .onSuccess { stored ->
                _form.update { it.copy(photoUri = stored, loading = false) }
            }
            .onFailure { error ->
                _form.update {
                    it.copy(
                        loading = false,
                        fieldErrors = it.fieldErrors + ("photo" to (error.message ?: "Не удалось сохранить фото."))
                    )
                }
            }
    }

    fun publish() = viewModelScope.launch {
        val state = _form.value
        if (state.submitting) return@launch
        val errors = linkedMapOf<String, String>()
        if (state.photoUri.isBlank()) errors["photo"] = "Добавьте фотографию животного."
        if (state.name.isBlank()) errors["name"] = "Введите имя."
        if (state.type == null) errors["type"] = "Выберите тип животного."
        if (state.age.isBlank()) errors["age"] = "Укажите возраст."
        if (state.city.isBlank()) errors["city"] = "Укажите город."
        if (state.description.isBlank()) errors["description"] = "Добавьте описание."
        if (state.ownerType == null) errors["ownerType"] = "Выберите тип владельца."
        when (state.ownerType) {
            OwnerType.SHELTER -> if (state.shelterId.isNullOrBlank()) {
                errors["shelter"] = "Выберите приют."
            }
            OwnerType.PRIVATE -> if (state.ownerPhone.isBlank()) {
                errors["ownerPhone"] = "Укажите контактный номер телефона."
            }
            null -> Unit
        }
        if (errors.isNotEmpty()) {
            _form.update { it.copy(fieldErrors = errors, error = "Заполните обязательные поля.") }
            return@launch
        }
        val user = authRepository.currentUser.first()
        _form.update { it.copy(submitting = true, error = null, fieldErrors = emptyMap()) }
        val result = animalRepository.publishPet(
            NewPetDraft(
                name = state.name,
                type = checkNotNull(state.type),
                breed = state.breed,
                gender = state.gender ?: Gender.FEMALE,
                ageLabel = state.age,
                city = state.city,
                description = state.description,
                healthStatus = state.healthStatus,
                imageUri = state.photoUri,
                ownerType = checkNotNull(state.ownerType),
                ownerName = user.name,
                ownerPhone = state.ownerPhone,
                shelterId = state.shelterId
            ),
            applicant = user
        )
        _form.update { current ->
            result.fold(
                onSuccess = { current.copy(submitting = false, submitted = true) },
                onFailure = { error -> current.copy(submitting = false, error = error.message) }
            )
        }
    }
}

data class ChatUiState(
    val loading: Boolean = true,
    val title: String = "",
    val conversationId: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val draft: String = "",
    val sending: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val participantId: String,
    private val participantType: ChatParticipantType,
    initialName: String
) : ViewModel() {
    private val conversationId = MutableStateFlow<String?>(null)
    private val draft = MutableStateFlow("")
    private val sending = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)
    private val title = MutableStateFlow(initialName)

    private val messages = conversationId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else chatRepository.observeMessages(id)
    }

    val ui: StateFlow<ChatUiState> = combine(
        combine(conversationId, messages, draft) { id, items, text -> Triple(id, items, text) },
        combine(sending, error, title) { isSending, err, name -> Triple(isSending, err, name) }
    ) { conv, meta ->
        val (id, items, text) = conv
        val (isSending, err, name) = meta
        ChatUiState(
            loading = id == null,
            title = name,
            conversationId = id.orEmpty(),
            messages = items,
            draft = text,
            sending = isSending,
            error = err
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ChatUiState())

    init {
        viewModelScope.launch {
            val name = initialName.ifBlank {
                chatRepository.resolveParticipantName(participantId, participantType)
            }
            title.value = name
            val conversation = chatRepository.getOrCreateConversation(participantId, participantType, name)
            conversationId.value = conversation.id
        }
    }

    fun setDraft(value: String) {
        draft.value = value
        error.value = null
    }

    fun send() = viewModelScope.launch {
        val id = conversationId.value ?: return@launch
        if (sending.value) return@launch
        val text = draft.value
        if (text.trim().isEmpty()) {
            error.value = "Пустое сообщение отправить нельзя."
            return@launch
        }
        sending.value = true
        val result = chatRepository.sendMessage(id, text)
        sending.value = false
        result.fold(
            onSuccess = {
                draft.value = ""
                viewModelScope.launch {
                    delay(650)
                    chatRepository.addLocalReply(id, participantType)
                }
            },
            onFailure = { failure -> error.value = failure.message }
        )
    }
}

data class DonationPaymentUiState(
    val loading: Boolean = false,
    val shelter: Shelter? = null,
    val shelters: List<Shelter> = emptyList(),
    val purposeKey: String = "custom",
    val purposeTitle: String = "Произвольная сумма",
    val amountInput: String = "",
    val method: PaymentMethod = PaymentMethod.BANK_CARD,
    val paying: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val receiptAmount: Int? = null,
    val history: List<Donation> = emptyList()
)

class DonationPaymentViewModel(
    animalRepository: AnimalRepository,
    private val donationRepository: DonationRepository,
    initialShelterId: String,
    initialPurpose: String,
    initialAmount: Int?
) : ViewModel() {
    private val shelterId = MutableStateFlow(initialShelterId)
    private val purposeKey = MutableStateFlow(initialPurpose.ifBlank { "custom" })
    private val amountInput = MutableStateFlow(initialAmount?.takeIf { it > 0 }?.toString().orEmpty())
    private val method = MutableStateFlow(PaymentMethod.BANK_CARD)
    private val paying = MutableStateFlow(false)
    private val success = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)
    private val receiptAmount = MutableStateFlow<Int?>(null)

    val ui: StateFlow<DonationPaymentUiState> = combine(
        combine(animalRepository.observeShelters(), donationRepository.observeHistory(), shelterId, purposeKey, amountInput) { shelters, history, selectedId, purpose, amount ->
            DonationFormSlice(shelters, history, selectedId, purpose, amount)
        },
        combine(method, paying, success, error, receiptAmount) { selectedMethod, isPaying, isSuccess, err, receipt ->
            DonationStatusSlice(selectedMethod, isPaying, isSuccess, err, receipt)
        }
    ) { form, status ->
        DonationPaymentUiState(
            shelter = form.shelters.find { it.id == form.selectedId } ?: form.shelters.firstOrNull(),
            shelters = form.shelters,
            purposeKey = form.purpose,
            purposeTitle = purposeTitle(form.purpose),
            amountInput = form.amount,
            method = status.method,
            paying = status.paying,
            success = status.success,
            error = status.error,
            receiptAmount = status.receiptAmount,
            history = form.history
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DonationPaymentUiState())

    fun selectShelter(id: String) {
        shelterId.value = id
        error.value = null
    }

    fun setAmount(value: String) {
        amountInput.value = value.filter { it.isDigit() }
        error.value = null
    }

    fun setMethod(value: PaymentMethod) {
        method.value = value
    }

    fun pay() = viewModelScope.launch {
        if (paying.value || success.value) return@launch
        val amount = amountInput.value.toIntOrNull() ?: 0
        val shelter = shelterId.value.ifBlank { ui.value.shelter?.id }.orEmpty()
        if (shelter.isBlank()) {
            error.value = "Выберите приют."
            return@launch
        }
        if (amount <= 0) {
            error.value = "Укажите сумму в тенге."
            return@launch
        }
        paying.value = true
        error.value = null
        val result = donationRepository.donate(
            DonationRequest(
                shelterId = shelter,
                purpose = purposeTitle(purposeKey.value),
                amountKzt = amount,
                method = method.value
            )
        )
        paying.value = false
        result.fold(
            onSuccess = { receipt ->
                success.value = true
                receiptAmount.value = receipt.amountKzt
            },
            onFailure = { failure -> error.value = failure.message }
        )
    }
}

private data class DonationFormSlice(
    val shelters: List<Shelter>,
    val history: List<Donation>,
    val selectedId: String,
    val purpose: String,
    val amount: String
)

private data class DonationStatusSlice(
    val method: PaymentMethod,
    val paying: Boolean,
    val success: Boolean,
    val error: String?,
    val receiptAmount: Int?
)

internal fun purposeTitle(key: String): String = when (key) {
    "food" -> "Корм на неделю"
    "medical" -> "Лечение"
    "shelter", "shelter_support" -> "Поддержка приюта"
    else -> "Произвольная сумма"
}

data class ShelterDetailUiState(
    val loading: Boolean = true,
    val shelter: Shelter? = null,
    val animals: List<Animal> = emptyList()
)

class ShelterDetailViewModel(
    animalRepository: AnimalRepository,
    shelterId: String
) : ViewModel() {
    val ui: StateFlow<ShelterDetailUiState> = combine(
        animalRepository.observeShelter(shelterId),
        animalRepository.observeAnimalsByShelter(shelterId)
    ) { shelter, animals ->
        ShelterDetailUiState(loading = false, shelter = shelter, animals = animals)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ShelterDetailUiState())
}

class ShelterAnimalsViewModel(
    private val animalRepository: AnimalRepository,
    shelterId: String
) : ViewModel() {
    val ui: StateFlow<ShelterDetailUiState> = combine(
        animalRepository.observeShelter(shelterId),
        animalRepository.observeAnimalsByShelter(shelterId)
    ) { shelter, animals ->
        ShelterDetailUiState(loading = false, shelter = shelter, animals = animals)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ShelterDetailUiState())

    fun toggleFavorite(id: String) = viewModelScope.launch { animalRepository.toggleFavorite(id) }
}
