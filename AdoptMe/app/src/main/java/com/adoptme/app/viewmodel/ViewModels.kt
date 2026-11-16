package com.adoptme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalQuery
import com.adoptme.app.data.model.AnimalStatus
import com.adoptme.app.data.model.ApplicationDraft
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.ChatMessage
import com.adoptme.app.data.model.ChatThread
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.NewsArticle
import com.adoptme.app.data.model.PetSize
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.data.model.Species
import com.adoptme.app.data.model.UserProfile
import com.adoptme.app.data.model.UserRole
import com.adoptme.app.data.model.ageGroupOf
import com.adoptme.app.di.AppContainer
import com.adoptme.app.utils.AnimalFilter
import com.adoptme.app.utils.ShelterFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val Share = SharingStarted.WhileSubscribed(5_000)

data class AuthFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AuthViewModel(private val container: AppContainer) : ViewModel() {
    private val _ui = MutableStateFlow(AuthFormState())
    val ui: StateFlow<AuthFormState> = _ui.asStateFlow()

    fun update(transform: (AuthFormState) -> AuthFormState) {
        _ui.update { transform(it).copy(error = null, success = false) }
    }

    fun register() = viewModelScope.launch {
        val state = _ui.value
        if (state.loading || state.success) return@launch
        _ui.update { it.copy(loading = true, error = null) }
        val result = container.authRepository.register(
            state.name,
            state.email,
            state.password,
            state.confirmPassword
        )
        _ui.update { current ->
            result.fold(
                onSuccess = { current.copy(loading = false, success = true) },
                onFailure = { error -> current.copy(loading = false, error = error.message) }
            )
        }
    }

    fun login() = viewModelScope.launch {
        val state = _ui.value
        if (state.loading || state.success) return@launch
        _ui.update { it.copy(loading = true, error = null) }
        val result = container.authRepository.login(state.email, state.password)
        _ui.update { current ->
            result.fold(
                onSuccess = { current.copy(loading = false, success = true) },
                onFailure = { error -> current.copy(loading = false, error = error.message) }
            )
        }
    }
}

data class HomeUiState(
    val query: AnimalQuery = AnimalQuery(),
    val animals: List<Animal> = emptyList(),
    val cities: List<String> = emptyList(),
    val userName: String = ""
)

class HomeViewModel(private val container: AppContainer) : ViewModel() {
    private val query = MutableStateFlow(AnimalQuery())
    val ui: StateFlow<HomeUiState> = combine(
        query,
        container.catalogRepository.animals,
        container.authRepository.currentUser
    ) { q, animals, user ->
        HomeUiState(
            query = q,
            animals = AnimalFilter.apply(animals, q),
            cities = animals.map { it.city }.distinct().sorted(),
            userName = user.name
        )
    }.stateIn(viewModelScope, Share, HomeUiState())

    fun setName(value: String) = query.update { it.copy(name = value) }
    fun setSpecies(value: Species?) = query.update { it.copy(species = value) }
    fun setAge(value: AgeGroup?) = query.update { it.copy(ageGroup = value) }
    fun setSize(value: PetSize?) = query.update { it.copy(size = value) }
    fun setGender(value: Gender?) = query.update { it.copy(gender = value) }
    fun setCity(value: String?) = query.update { it.copy(city = value) }
    fun clearFilters() { query.value = AnimalQuery(name = query.value.name) }

    fun toggleFavorite(id: String) = viewModelScope.launch {
        container.catalogRepository.toggleFavorite(id)
    }
}

class FavoritesViewModel(private val container: AppContainer) : ViewModel() {
    val animals: StateFlow<List<Animal>> = container.catalogRepository.animals
        .combine(MutableStateFlow(Unit)) { list, _ -> list.filter { it.isFavorite } }
        .stateIn(viewModelScope, Share, emptyList())

    fun toggleFavorite(id: String) = viewModelScope.launch {
        container.catalogRepository.toggleFavorite(id)
    }
}

class DetailsViewModel(
    private val container: AppContainer,
    private val animalId: String
) : ViewModel() {
    val animal: StateFlow<Animal?> = container.catalogRepository.observeAnimal(animalId)
        .stateIn(viewModelScope, Share, null)

    fun toggleFavorite() = viewModelScope.launch {
        container.catalogRepository.toggleFavorite(animalId)
    }
}

data class ApplicationFormState(
    val animalName: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val experience: String = "",
    val reason: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val submittedNumber: String? = null,
    val unavailable: Boolean = false
)

class ApplicationViewModel(
    private val container: AppContainer,
    private val animalId: String
) : ViewModel() {
    private val _form = MutableStateFlow(ApplicationFormState())
    val form: StateFlow<ApplicationFormState> = _form.asStateFlow()
    private var animalStatus: AnimalStatus = AnimalStatus.AVAILABLE
    private var animalName: String = ""
    private var prefilled = false

    init {
        viewModelScope.launch {
            container.catalogRepository.observeAnimal(animalId).collect { animal ->
                if (animal == null) return@collect
                animalStatus = animal.status
                animalName = animal.name
                _form.update {
                    it.copy(
                        animalName = animal.name,
                        unavailable = animal.status == AnimalStatus.ADOPTED
                    )
                }
            }
        }
        viewModelScope.launch {
            container.authRepository.currentUser.collect { user ->
                if (prefilled) return@collect
                if (user.id.isBlank()) return@collect
                prefilled = true
                _form.update {
                    it.copy(
                        fullName = it.fullName.ifBlank { user.name },
                        email = it.email.ifBlank { user.email },
                        phone = it.phone.ifBlank { user.phone }
                    )
                }
            }
        }
    }

    fun update(transform: (ApplicationFormState) -> ApplicationFormState) = _form.update(transform)

    fun submit() {
        val current = _form.value
        if (current.loading || current.submittedNumber != null) return
        viewModelScope.launch {
            _form.update { it.copy(loading = true, error = null) }
            val user = container.sessionStore.snapshot()
            val result = container.catalogRepository.submitApplication(
                ApplicationDraft(
                    userId = user.userId,
                    animalId = animalId,
                    animalName = animalName.ifBlank { current.animalName },
                    animalStatus = animalStatus,
                    applicantName = current.fullName,
                    email = current.email,
                    phone = current.phone,
                    address = current.address,
                    experience = current.experience,
                    reason = current.reason
                )
            )
            _form.update { state ->
                result.fold(
                    onSuccess = { saved -> state.copy(loading = false, submittedNumber = saved.number) },
                    onFailure = { error -> state.copy(loading = false, error = error.message) }
                )
            }
        }
    }
}

class MyApplicationsViewModel(private val container: AppContainer) : ViewModel() {
    val items: StateFlow<List<AdoptionApplication>> = container.catalogRepository.myApplications
        .stateIn(viewModelScope, Share, emptyList())
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun cancel(id: String) = viewModelScope.launch {
        val result = container.catalogRepository.cancelApplication(id)
        _message.value = result.fold(
            onSuccess = { "Заявка отменена." },
            onFailure = { it.message }
        )
    }

    fun clearMessage() { _message.value = null }
}

data class ShelterUiState(
    val city: String? = null,
    val query: String = "",
    val cities: List<String> = emptyList(),
    val shelters: List<Shelter> = emptyList()
)

class ShelterViewModel(container: AppContainer) : ViewModel() {
    private val city = MutableStateFlow<String?>(null)
    private val query = MutableStateFlow("")
    val ui: StateFlow<ShelterUiState> = combine(city, query, container.catalogRepository.shelters) { selected, text, shelters ->
        ShelterUiState(
            city = selected,
            query = text,
            cities = shelters.map { it.city }.distinct().sorted(),
            shelters = ShelterFilter.apply(shelters, selected, text)
        )
    }.stateIn(viewModelScope, Share, ShelterUiState())

    fun setCity(value: String?) { city.value = value }
    fun setQuery(value: String) { query.value = value }
}

class ShelterDetailViewModel(container: AppContainer, shelterId: String) : ViewModel() {
    val shelter: StateFlow<Shelter?> = container.catalogRepository.observeShelter(shelterId)
        .stateIn(viewModelScope, Share, null)
}

class ProfileViewModel(private val container: AppContainer) : ViewModel() {
    val user: StateFlow<UserProfile> = container.authRepository.currentUser
        .stateIn(viewModelScope, Share, UserProfile())
    val session = container.sessionStore.session.stateIn(
        viewModelScope,
        Share,
        com.adoptme.app.data.local.SessionSnapshot()
    )
    val favoriteCount: StateFlow<Int> = container.catalogRepository.animals
        .combine(MutableStateFlow(Unit)) { list, _ -> list.count { it.isFavorite } }
        .stateIn(viewModelScope, Share, 0)
    val applicationCount: StateFlow<Int> = container.catalogRepository.myApplications
        .combine(MutableStateFlow(Unit)) { list, _ -> list.size }
        .stateIn(viewModelScope, Share, 0)

    fun logout(onDone: () -> Unit) = viewModelScope.launch {
        container.authRepository.logout()
        onDone()
    }

    fun setNotifications(enabled: Boolean) = viewModelScope.launch {
        container.authRepository.setNotificationsEnabled(enabled)
    }

    val isLocalAdmin: StateFlow<Boolean> = user
        .combine(MutableStateFlow(Unit)) { profile, _ -> profile.role == UserRole.LOCAL_ADMIN }
        .stateIn(viewModelScope, Share, false)
}

class NewsListViewModel(container: AppContainer) : ViewModel() {
    val articles: StateFlow<List<NewsArticle>> = container.newsRepository.articles
        .stateIn(viewModelScope, Share, emptyList())
}

class NewsDetailViewModel(container: AppContainer, id: String) : ViewModel() {
    val article: StateFlow<NewsArticle?> = container.newsRepository.article(id)
        .stateIn(viewModelScope, Share, null)
}

class ChatListViewModel(private val container: AppContainer) : ViewModel() {
    val threads: StateFlow<List<ChatThread>> = container.chatRepository.threads
        .stateIn(viewModelScope, Share, emptyList())

    init {
        viewModelScope.launch { container.chatRepository.ensureInbox() }
    }
}

class ChatThreadViewModel(
    private val container: AppContainer,
    private val threadId: String
) : ViewModel() {
    val messages: StateFlow<List<ChatMessage>> = container.chatRepository.messages(threadId)
        .stateIn(viewModelScope, Share, emptyList())
    private val _draft = MutableStateFlow("")
    val draft: StateFlow<String> = _draft.asStateFlow()
    private val _sending = MutableStateFlow(false)
    val sending: StateFlow<Boolean> = _sending.asStateFlow()

    fun setDraft(value: String) { _draft.value = value }

    fun send() {
        val text = _draft.value
        if (_sending.value || text.isBlank()) return
        viewModelScope.launch {
            _sending.value = true
            _draft.value = ""
            container.chatRepository.send(threadId, text)
            _sending.value = false
        }
    }
}

class OpenShelterChatViewModel(private val container: AppContainer) : ViewModel() {
    fun open(shelterId: String, shelterName: String, onReady: (String) -> Unit) {
        viewModelScope.launch {
            val id = container.chatRepository.openShelterChat(shelterId, shelterName)
            onReady(id)
        }
    }
}

data class AdminUiState(
    val applications: List<AdoptionApplication> = emptyList(),
    val animals: List<Animal> = emptyList()
)

class AdminViewModel(private val container: AppContainer) : ViewModel() {
    val ui: StateFlow<AdminUiState> = combine(
        container.catalogRepository.allApplications,
        container.catalogRepository.animals
    ) { applications, animals ->
        AdminUiState(applications, animals)
    }.stateIn(viewModelScope, Share, AdminUiState())

    val shelters: StateFlow<List<Shelter>> = container.catalogRepository.shelters
        .stateIn(viewModelScope, Share, emptyList())

    fun setApplicationStatus(id: String, status: ApplicationStatus) = viewModelScope.launch {
        container.catalogRepository.setApplicationStatus(id, status)
    }

    fun setAnimalStatus(id: String, status: AnimalStatus) = viewModelScope.launch {
        container.catalogRepository.setAnimalStatus(id, status)
    }
}

data class AnimalEditorState(
    val id: String = "",
    val name: String = "",
    val species: Species = Species.DOG,
    val breed: String = "",
    val ageMonths: String = "12",
    val ageLabel: String = "",
    val gender: Gender = Gender.MALE,
    val size: PetSize = PetSize.MEDIUM,
    val temperament: String = "",
    val story: String = "",
    val shelterId: String = "",
    val city: String = "",
    val imageKey: String = "animal_placeholder",
    val status: AnimalStatus = AnimalStatus.AVAILABLE,
    val error: String? = null,
    val saved: Boolean = false
)

class AnimalEditorViewModel(
    private val container: AppContainer,
    private val animalId: String
) : ViewModel() {
    private val _state = MutableStateFlow(AnimalEditorState(id = if (animalId == "new") "" else animalId))
    val state: StateFlow<AnimalEditorState> = _state.asStateFlow()
    val shelters: StateFlow<List<Shelter>> = container.catalogRepository.shelters
        .stateIn(viewModelScope, Share, emptyList())

    init {
        if (animalId != "new") {
            viewModelScope.launch {
                container.catalogRepository.observeAnimal(animalId).collect { animal ->
                    if (animal == null || _state.value.saved) return@collect
                    _state.value = AnimalEditorState(
                        id = animal.id,
                        name = animal.name,
                        species = animal.species,
                        breed = animal.breed,
                        ageMonths = animal.ageMonths.toString(),
                        ageLabel = animal.ageLabel,
                        gender = animal.gender,
                        size = animal.size,
                        temperament = animal.temperament,
                        story = animal.story,
                        shelterId = animal.shelterId,
                        city = animal.city,
                        imageKey = animal.imageKey,
                        status = animal.status
                    )
                }
            }
        }
    }

    fun update(transform: (AnimalEditorState) -> AnimalEditorState) = _state.update(transform)

    fun save() = viewModelScope.launch {
        val form = _state.value
        val months = form.ageMonths.toIntOrNull()
        val shelter = shelters.value.find { it.id == form.shelterId }
        val error = when {
            form.name.trim().length < 2 -> "Укажите кличку."
            form.breed.isBlank() -> "Укажите породу."
            months == null || months <= 0 -> "Укажите возраст в месяцах."
            form.ageLabel.isBlank() -> "Укажите возраст словами."
            form.temperament.isBlank() -> "Опишите характер."
            form.story.length < 10 -> "Добавьте историю животного."
            shelter == null -> "Выберите приют."
            else -> null
        }
        if (error != null) {
            _state.update { it.copy(error = error) }
            return@launch
        }
        val animal = Animal(
            id = form.id.ifBlank { java.util.UUID.randomUUID().toString() },
            name = form.name.trim(),
            species = form.species,
            breed = form.breed.trim(),
            ageMonths = months!!,
            ageLabel = form.ageLabel.trim(),
            ageGroup = ageGroupOf(months),
            gender = form.gender,
            size = form.size,
            temperament = form.temperament.trim(),
            story = form.story.trim(),
            shelterId = shelter!!.id,
            shelterName = shelter.name,
            city = shelter.city,
            imageKey = form.imageKey.ifBlank { "animal_placeholder" },
            status = form.status
        )
        container.catalogRepository.saveAnimal(animal)
        _state.update { it.copy(error = null, saved = true, id = animal.id) }
    }
}

class ApplicationDetailViewModel(container: AppContainer, id: String) : ViewModel() {
    val application: StateFlow<AdoptionApplication?> = container.catalogRepository.observeApplication(id)
        .stateIn(viewModelScope, Share, null)
}

@Suppress("UNCHECKED_CAST")
class AdoptMeViewModelFactory(
    private val container: AppContainer,
    private val recordId: String = ""
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val model = when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(container)
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(container)
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> FavoritesViewModel(container)
            modelClass.isAssignableFrom(DetailsViewModel::class.java) -> DetailsViewModel(container, recordId)
            modelClass.isAssignableFrom(ApplicationViewModel::class.java) -> ApplicationViewModel(container, recordId)
            modelClass.isAssignableFrom(MyApplicationsViewModel::class.java) -> MyApplicationsViewModel(container)
            modelClass.isAssignableFrom(ShelterViewModel::class.java) -> ShelterViewModel(container)
            modelClass.isAssignableFrom(ShelterDetailViewModel::class.java) -> ShelterDetailViewModel(container, recordId)
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(container)
            modelClass.isAssignableFrom(NewsListViewModel::class.java) -> NewsListViewModel(container)
            modelClass.isAssignableFrom(NewsDetailViewModel::class.java) -> NewsDetailViewModel(container, recordId)
            modelClass.isAssignableFrom(ChatListViewModel::class.java) -> ChatListViewModel(container)
            modelClass.isAssignableFrom(ChatThreadViewModel::class.java) -> ChatThreadViewModel(container, recordId)
            modelClass.isAssignableFrom(OpenShelterChatViewModel::class.java) -> OpenShelterChatViewModel(container)
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> AdminViewModel(container)
            modelClass.isAssignableFrom(AnimalEditorViewModel::class.java) -> AnimalEditorViewModel(container, recordId)
            modelClass.isAssignableFrom(ApplicationDetailViewModel::class.java) -> ApplicationDetailViewModel(container, recordId)
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
        return model as T
    }
}
