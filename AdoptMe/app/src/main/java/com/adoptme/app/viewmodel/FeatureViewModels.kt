package com.adoptme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptme.app.data.local.SeedData
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalSize
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.AppNotification
import com.adoptme.app.data.model.Availability
import com.adoptme.app.data.model.ChatConversation
import com.adoptme.app.data.model.ChatMessage
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.model.NewsArticle
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.data.repository.AnimalRepository
import com.adoptme.app.data.repository.ApplicationRepository
import com.adoptme.app.data.repository.ChatRepository
import com.adoptme.app.data.repository.ContentRepository
import com.adoptme.app.utils.ClickLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ShelterViewModel(contentRepository: ContentRepository) : ViewModel() {
    private val city = MutableStateFlow<String?>(null)
    private val shelters = contentRepository.observeShelters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedCity: StateFlow<String?> = city.asStateFlow()
    val all: StateFlow<List<Shelter>> = shelters

    val visible: StateFlow<List<Shelter>> = kotlinx.coroutines.flow.combine(shelters, city) { list, selected ->
        if (selected.isNullOrBlank()) list else list.filter { it.city == selected }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectCity(value: String?) {
        city.value = value
    }
}

class NewsViewModel(contentRepository: ContentRepository) : ViewModel() {
    val articles: StateFlow<List<NewsArticle>> = contentRepository.observeNews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class NewsDetailViewModel(contentRepository: ContentRepository, articleId: String) : ViewModel() {
    val article: StateFlow<NewsArticle?> = contentRepository.observeArticle(articleId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

class ChatListViewModel(chatRepository: ChatRepository) : ViewModel() {
    val conversations: StateFlow<List<ChatConversation>> = chatRepository.observeConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class ChatThreadViewModel(
    private val chatRepository: ChatRepository,
    private val conversationId: String
) : ViewModel() {
    val messages: StateFlow<List<ChatMessage>> = chatRepository.observeMessages(conversationId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _draft = MutableStateFlow("")
    val draft: StateFlow<String> = _draft.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val lock = ClickLock()

    fun setDraft(value: String) {
        _draft.value = value
        _error.value = null
    }

    fun send() {
        if (!lock.tryAcquire()) return
        val text = _draft.value
        viewModelScope.launch {
            val result = chatRepository.send(conversationId, text)
            if (result.isSuccess) {
                _draft.value = ""
                _error.value = null
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            lock.release()
        }
    }
}

class NotificationsViewModel(private val contentRepository: ContentRepository) : ViewModel() {
    val items: StateFlow<List<AppNotification>> = contentRepository.observeNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val enabled: StateFlow<Boolean> = contentRepository.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun markRead(id: String) = viewModelScope.launch {
        contentRepository.markNotificationRead(id)
    }

    fun setEnabled(value: Boolean) = viewModelScope.launch {
        contentRepository.setNotificationsEnabled(value)
    }
}

data class AnimalEditorState(
    val id: String = "",
    val name: String = "",
    val type: AnimalType = AnimalType.DOG,
    val breed: String = "",
    val ageLabel: String = "",
    val ageMonths: String = "",
    val gender: Gender = Gender.MALE,
    val size: AnimalSize = AnimalSize.MEDIUM,
    val health: HealthStatus = HealthStatus.HEALTHY,
    val availability: Availability = Availability.AVAILABLE,
    val personality: String = "",
    val description: String = "",
    val story: String = "",
    val shelterId: String = "",
    val imageUrl: String = "",
    val vaccinated: Boolean = true,
    val sterilized: Boolean = false,
    val goodWithKids: Boolean = true,
    val loading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

class AdminViewModel(
    private val animalRepository: AnimalRepository,
    private val applicationRepository: ApplicationRepository
) : ViewModel() {
    val applications: StateFlow<List<AdoptionApplication>> = applicationRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val animals: StateFlow<List<Animal>> = animalRepository.observeAnimals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val shelters = SeedData.shelters

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()
    private val reviewLock = ClickLock()

    fun review(id: String, approve: Boolean) {
        if (!reviewLock.tryAcquire()) return
        viewModelScope.launch {
            val result = applicationRepository.review(id, approve)
            _message.value = result.fold(
                onSuccess = { application ->
                    val decision = if (approve) "одобрена" else "отклонена"
                    "Заявка ${application.number} $decision."
                },
                onFailure = { it.message }
            )
            reviewLock.release()
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}

class AnimalEditorViewModel(
    private val animalRepository: AnimalRepository,
    animalId: String?
) : ViewModel() {
    private val _state = MutableStateFlow(AnimalEditorState(id = animalId.orEmpty()))
    val state: StateFlow<AnimalEditorState> = _state.asStateFlow()
    private val lock = ClickLock()

    init {
        if (!animalId.isNullOrBlank() && animalId != "new") {
            viewModelScope.launch {
                animalRepository.observeAnimal(animalId).collect { animal ->
                    if (animal == null || _state.value.name.isNotBlank()) return@collect
                    _state.value = AnimalEditorState(
                        id = animal.id,
                        name = animal.name,
                        type = animal.type,
                        breed = animal.breed,
                        ageLabel = animal.ageLabel,
                        ageMonths = animal.ageMonths.toString(),
                        gender = animal.gender,
                        size = animal.size,
                        health = animal.healthStatus,
                        availability = animal.availability,
                        personality = animal.personality,
                        description = animal.description,
                        story = animal.story,
                        shelterId = animal.shelterId,
                        imageUrl = animal.imageUrl,
                        vaccinated = animal.vaccinated,
                        sterilized = animal.sterilized,
                        goodWithKids = animal.goodWithKids
                    )
                }
            }
        }
    }

    fun update(transform: (AnimalEditorState) -> AnimalEditorState) = _state.update(transform)

    fun save() {
        if (!lock.tryAcquire()) return
        val current = _state.value
        val shelter = SeedData.shelters.find { it.id == current.shelterId }
        val months = current.ageMonths.toIntOrNull()
        if (shelter == null || months == null) {
            _state.update {
                it.copy(error = if (shelter == null) "Выберите приют." else "Укажите возраст в месяцах.")
            }
            lock.release()
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val animalId = current.id.takeUnless { it.isBlank() || it == "new" } ?: UUID.randomUUID().toString()
            val animal = Animal(
                id = animalId,
                name = current.name.trim(),
                type = current.type,
                breed = current.breed.trim(),
                ageLabel = current.ageLabel.trim(),
                ageMonths = months,
                gender = current.gender,
                size = current.size,
                healthStatus = current.health,
                availability = current.availability,
                personality = current.personality.trim(),
                description = current.description.trim(),
                story = current.story.trim(),
                shelterId = shelter.id,
                shelterName = shelter.name,
                city = shelter.city,
                imageUrl = current.imageUrl.trim(),
                vaccinated = current.vaccinated,
                sterilized = current.sterilized,
                goodWithKids = current.goodWithKids
            )
            val result = animalRepository.save(animal)
            _state.update { editor ->
                result.fold(
                    onSuccess = { editor.copy(loading = false, saved = true, id = animal.id) },
                    onFailure = { error -> editor.copy(loading = false, error = error.message) }
                )
            }
            if (result.isFailure) lock.release()
        }
    }
}
