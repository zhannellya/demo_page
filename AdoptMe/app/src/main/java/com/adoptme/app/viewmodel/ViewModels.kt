package com.adoptme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalFilter
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.AppNotification
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Clinic
import com.adoptme.app.data.model.DonationOption
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.LostFoundPost
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.data.model.UserProfile
import com.adoptme.app.data.repository.AnimalRepository
import com.adoptme.app.data.repository.AuthRepository
import com.adoptme.app.data.repository.applyFilter
import com.adoptme.app.data.repository.breedsFor
import com.adoptme.app.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    fun update(transform: (AuthUiState) -> AuthUiState) = _ui.update(transform)

    fun signUp() = viewModelScope.launch {
        val state = _ui.value
        if (state.password != state.confirmPassword) {
            _ui.update { it.copy(error = "Пароли не совпадают.") }
            return@launch
        }
        _ui.update { it.copy(loading = true, error = null) }
        val result = authRepository.signUp(
            name = state.name,
            email = state.email,
            password = state.password,
            phone = state.phone,
            city = state.city
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
        _ui.update { it.copy(loading = true, error = null) }
        val result = authRepository.login(state.email, state.password)
        _ui.update { current ->
            result.fold(
                onSuccess = { current.copy(loading = false, success = true) },
                onFailure = { error -> current.copy(loading = false, error = error.message) }
            )
        }
    }
}

data class HomeUiState(
    val loading: Boolean = true,
    val query: String = "",
    val selectedCategory: AnimalType? = null,
    val animals: List<Animal> = emptyList(),
    val userName: String = ""
)

class HomeViewModel(
    private val animalRepository: AnimalRepository,
    authRepository: AuthRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val category = MutableStateFlow<AnimalType?>(null)

    val ui: StateFlow<HomeUiState> = combine(
        query,
        category,
        animalRepository.observeAnimals(),
        authRepository.currentUser
    ) { q, cat, animals, user ->
        HomeUiState(
            loading = false,
            query = q,
            selectedCategory = cat,
            animals = animals.applyFilter(AnimalFilter(query = q, type = cat)),
            userName = user.name.ifBlank { "друг" }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun setQuery(value: String) { query.value = value }
    fun setCategory(type: AnimalType?) { category.value = type }
    fun toggleFavorite(id: String) = viewModelScope.launch { animalRepository.toggleFavorite(id) }
}

data class SearchUiState(
    val loading: Boolean = true,
    val query: String = "",
    val type: AnimalType? = null,
    val gender: Gender? = null,
    val breed: String? = null,
    val ageGroup: AgeGroup? = null,
    val breeds: List<String> = emptyList(),
    val animals: List<Animal> = emptyList()
)

class SearchViewModel(private val animalRepository: AnimalRepository) : ViewModel() {
    private val query = MutableStateFlow("")
    private val type = MutableStateFlow<AnimalType?>(null)
    private val gender = MutableStateFlow<Gender?>(null)
    private val breed = MutableStateFlow<String?>(null)
    private val ageGroup = MutableStateFlow<AgeGroup?>(null)

    private val filters = combine(query, type, gender, breed, ageGroup) { q, selectedType, selectedGender, selectedBreed, selectedAge ->
        AnimalFilter(
            query = q,
            type = selectedType,
            gender = selectedGender,
            breed = selectedBreed,
            ageGroup = selectedAge
        )
    }

    val ui: StateFlow<SearchUiState> = combine(filters, animalRepository.observeAnimals()) { filter, animals ->
        val availableBreeds = breedsFor(animals, filter.type)
        val breedOrNull = filter.breed?.takeIf { it in availableBreeds }
        val active = filter.copy(breed = breedOrNull)
        SearchUiState(
            loading = false,
            query = active.query,
            type = active.type,
            gender = active.gender,
            breed = breedOrNull,
            ageGroup = active.ageGroup,
            breeds = availableBreeds,
            animals = animals.applyFilter(active)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    fun setQuery(value: String) { query.value = value }
    fun setType(value: AnimalType?) {
        type.value = value
        val current = breed.value
        if (current != null) breed.value = current
    }
    fun setGender(value: Gender?) { gender.value = value }
    fun setBreed(value: String?) { breed.value = value }
    fun setAgeGroup(value: AgeGroup?) { ageGroup.value = value }
    fun clearFilters() {
        query.value = ""
        type.value = null
        gender.value = null
        breed.value = null
        ageGroup.value = null
    }
    fun toggleFavorite(id: String) = viewModelScope.launch { animalRepository.toggleFavorite(id) }
}

data class FavoritesUiState(
    val loading: Boolean = true,
    val animals: List<Animal> = emptyList()
)

class FavoritesViewModel(private val animalRepository: AnimalRepository) : ViewModel() {
    val ui: StateFlow<FavoritesUiState> = animalRepository.observeAnimals()
        .map { list -> FavoritesUiState(loading = false, animals = list.filter { it.isFavorite }) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesUiState())

    fun toggleFavorite(id: String) = viewModelScope.launch { animalRepository.toggleFavorite(id) }
}

data class DetailsUiState(
    val loading: Boolean = true,
    val animal: Animal? = null,
    val shelter: Shelter? = null
)

class DetailsViewModel(
    private val animalRepository: AnimalRepository,
    private val animalId: String
) : ViewModel() {
    val ui: StateFlow<DetailsUiState> = combine(
        animalRepository.observeAnimals(),
        animalRepository.observeShelters()
    ) { animals, shelters ->
        val animal = animals.find { it.id == animalId }
        val shelter = animal?.shelterId?.let { id -> shelters.find { it.id == id } }
        DetailsUiState(loading = false, animal = animal, shelter = shelter)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailsUiState())

    fun toggleFavorite() = viewModelScope.launch { animalRepository.toggleFavorite(animalId) }
}

data class ApplicationFormState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val housingType: String = "Квартира",
    val hasOtherPets: Boolean = false,
    val experience: String = "",
    val reason: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val submitted: Boolean = false
)

class ApplicationViewModel(
    private val animalRepository: AnimalRepository,
    authRepository: AuthRepository,
    private val animalId: String,
    private val animalName: String
) : ViewModel() {
    private val _form = MutableStateFlow(ApplicationFormState())
    val form: StateFlow<ApplicationFormState> = _form.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _form.update {
                    it.copy(
                        fullName = it.fullName.ifBlank { user.name },
                        email = it.email.ifBlank { user.email },
                        phone = it.phone.ifBlank { user.phone },
                        address = it.address.ifBlank { user.city }
                    )
                }
            }
        }
    }

    fun update(transform: (ApplicationFormState) -> ApplicationFormState) = _form.update(transform)

    fun submit() = viewModelScope.launch {
        val form = _form.value
        when {
            form.fullName.isBlank() -> _form.update { it.copy(error = "Введите имя.") }
            form.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(form.email).matches() ->
                _form.update { it.copy(error = "Введите корректный email.") }
            form.phone.isBlank() -> _form.update { it.copy(error = "Введите телефон.") }
            form.address.isBlank() -> _form.update { it.copy(error = "Введите адрес.") }
            form.reason.isBlank() -> _form.update { it.copy(error = "Расскажите, почему хотите усыновить питомца.") }
            else -> {
                _form.update { it.copy(loading = true, error = null) }
                val result = animalRepository.submitApplication(
                    AdoptionApplication(
                        id = "",
                        animalId = animalId,
                        animalName = animalName,
                        applicantName = form.fullName.trim(),
                        email = form.email.trim(),
                        phone = form.phone.trim(),
                        address = form.address.trim(),
                        housingType = form.housingType,
                        hasOtherPets = form.hasOtherPets,
                        experience = form.experience.trim(),
                        reason = form.reason.trim(),
                        status = ApplicationStatus.PENDING
                    )
                )
                _form.update { current ->
                    result.fold(
                        onSuccess = { current.copy(loading = false, submitted = true) },
                        onFailure = { error -> current.copy(loading = false, error = error.message) }
                    )
                }
            }
        }
    }
}

data class ProfileEditState(
    val name: String = "",
    val phone: String = "",
    val city: String = "",
    val editing: Boolean = false,
    val saving: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

data class ApplicationsUiState(
    val loading: Boolean = true,
    val items: List<AdoptionApplication> = emptyList()
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    animalRepository: AnimalRepository
) : ViewModel() {
    val user: StateFlow<UserProfile> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProfile())

    val applications: StateFlow<ApplicationsUiState> = animalRepository.applications()
        .map { ApplicationsUiState(loading = false, items = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ApplicationsUiState())

    private val _edit = MutableStateFlow(ProfileEditState())
    val edit: StateFlow<ProfileEditState> = _edit.asStateFlow()

    init {
        viewModelScope.launch {
            user.collect { profile ->
                _edit.update { current ->
                    if (current.editing || current.saving) current
                    else current.copy(name = profile.name, phone = profile.phone, city = profile.city)
                }
            }
        }
    }

    fun startEdit() = _edit.update { it.copy(editing = true, message = null, error = null) }

    fun cancelEdit() {
        val profile = user.value
        _edit.update {
            it.copy(
                editing = false,
                error = null,
                name = profile.name,
                phone = profile.phone,
                city = profile.city
            )
        }
    }

    fun updateEdit(transform: (ProfileEditState) -> ProfileEditState) = _edit.update(transform)

    fun save() = viewModelScope.launch {
        val current = _edit.value
        _edit.update { it.copy(saving = true, error = null, message = null) }
        val result = authRepository.updateProfile(current.name, current.phone, current.city)
        _edit.update { current ->
            result.fold(
                onSuccess = {
                    current.copy(saving = false, editing = false, message = "Профиль сохранён")
                },
                onFailure = { error -> current.copy(saving = false, error = error.message) }
            )
        }
    }

    fun logout(onDone: () -> Unit) = viewModelScope.launch {
        authRepository.logout()
        onDone()
    }
}

class ApplicationsViewModel(private val animalRepository: AnimalRepository) : ViewModel() {
    val ui: StateFlow<ApplicationsUiState> = animalRepository.applications()
        .map { ApplicationsUiState(loading = false, items = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ApplicationsUiState())

    fun delete(applicationId: String) = viewModelScope.launch {
        animalRepository.deleteApplication(applicationId)
    }
}

class NotificationsViewModel(private val animalRepository: AnimalRepository) : ViewModel() {
    val items: StateFlow<List<AppNotification>> = animalRepository.notifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun markRead(id: String) = animalRepository.markNotificationRead(id)
}

class ContentViewModel(
    animalRepository: AnimalRepository,
    donationRepository: com.adoptme.app.data.repository.DonationRepository
) : ViewModel() {
    val shelters: StateFlow<List<Shelter>> = animalRepository.observeShelters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val clinics: List<Clinic> = animalRepository.clinics()
    val donations: List<DonationOption> = animalRepository.donations()
    val donationHistory: StateFlow<List<com.adoptme.app.data.model.Donation>> =
        donationRepository.observeHistory()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val lostFound: StateFlow<List<LostFoundPost>> = animalRepository.lostFound()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class SplashViewModel(private val authRepository: AuthRepository) : ViewModel() {
    suspend fun awaitLoggedIn(): Boolean = authRepository.isLoggedInNow()
}

@Suppress("UNCHECKED_CAST")
class AdoptMeViewModelFactory(
    private val container: AppContainer,
    private val animalId: String? = null,
    private val animalName: String? = null,
    private val shelterId: String? = null,
    private val participantId: String? = null,
    private val participantType: String? = null,
    private val donationPurpose: String? = null,
    private val donationAmount: Int? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(container.authRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(container.animalRepository, container.authRepository) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) ->
                SearchViewModel(container.animalRepository) as T
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) ->
                FavoritesViewModel(container.animalRepository) as T
            modelClass.isAssignableFrom(DetailsViewModel::class.java) ->
                DetailsViewModel(container.animalRepository, animalId.orEmpty()) as T
            modelClass.isAssignableFrom(ApplicationViewModel::class.java) ->
                ApplicationViewModel(
                    container.animalRepository,
                    container.authRepository,
                    animalId.orEmpty(),
                    animalName.orEmpty()
                ) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(container.authRepository, container.animalRepository) as T
            modelClass.isAssignableFrom(ApplicationsViewModel::class.java) ->
                ApplicationsViewModel(container.animalRepository) as T
            modelClass.isAssignableFrom(NotificationsViewModel::class.java) ->
                NotificationsViewModel(container.animalRepository) as T
            modelClass.isAssignableFrom(ContentViewModel::class.java) ->
                ContentViewModel(container.animalRepository, container.donationRepository) as T
            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(container.authRepository) as T
            modelClass.isAssignableFrom(AddPetViewModel::class.java) ->
                AddPetViewModel(container.animalRepository, container.authRepository, container.petPhotoStore) as T
            modelClass.isAssignableFrom(ChatViewModel::class.java) ->
                ChatViewModel(
                    container.chatRepository,
                    participantId.orEmpty(),
                    com.adoptme.app.data.model.parseChatParticipantType(participantType),
                    animalName.orEmpty()
                ) as T
            modelClass.isAssignableFrom(ChatsViewModel::class.java) ->
                ChatsViewModel(container.chatRepository) as T
            modelClass.isAssignableFrom(MatchQuizViewModel::class.java) ->
                MatchQuizViewModel(container.animalRepository) as T
            modelClass.isAssignableFrom(DonationPaymentViewModel::class.java) ->
                DonationPaymentViewModel(
                    container.animalRepository,
                    container.donationRepository,
                    shelterId.orEmpty(),
                    donationPurpose.orEmpty(),
                    donationAmount
                ) as T
            modelClass.isAssignableFrom(ShelterDetailViewModel::class.java) ->
                ShelterDetailViewModel(container.animalRepository, shelterId.orEmpty()) as T
            modelClass.isAssignableFrom(ShelterAnimalsViewModel::class.java) ->
                ShelterAnimalsViewModel(container.animalRepository, shelterId.orEmpty()) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
