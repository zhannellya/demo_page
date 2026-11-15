package com.adoptme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.AppNotification
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Clinic
import com.adoptme.app.data.model.DonationOption
import com.adoptme.app.data.model.LostFoundPost
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.data.model.UserProfile
import com.adoptme.app.data.repository.AnimalRepository
import com.adoptme.app.data.repository.AuthRepository
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
            _ui.update { it.copy(error = "Passwords do not match.") }
            return@launch
        }
        _ui.update { it.copy(loading = true, error = null) }
        val result = authRepository.signUp(state.name, state.email, state.password, state.phone)
        _ui.update { current ->
            result.fold(
                onSuccess = { current.copy(loading = false, success = true) },
                onFailure = { e -> current.copy(loading = false, error = e.message) }
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
                onFailure = { e -> current.copy(loading = false, error = e.message) }
            )
        }
    }
}

data class HomeUiState(
    val query: String = "",
    val selectedCategory: AnimalType? = null,
    val animals: List<Animal> = emptyList(),
    val userName: String = "Friend"
)

class HomeViewModel(
    private val animalRepository: AnimalRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val category = MutableStateFlow<AnimalType?>(null)

    val ui: StateFlow<HomeUiState> = combine(
        query,
        category,
        animalRepository.animalsWithFavorites(),
        authRepository.currentUser
    ) { q, cat, animals, user ->
        val filtered = animals.filter {
            (q.isBlank() || it.name.contains(q, true) || it.breed.contains(q, true) || it.location.contains(q, true)) &&
                (cat == null || it.type == cat)
        }
        HomeUiState(
            query = q,
            selectedCategory = cat,
            animals = filtered,
            userName = user.name.ifBlank { "Friend" }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun setQuery(value: String) { query.value = value }
    fun setCategory(type: AnimalType?) { category.value = type }
    fun toggleFavorite(id: String) = viewModelScope.launch { animalRepository.toggleFavorite(id) }
}

class AnimalsViewModel(private val animalRepository: AnimalRepository) : ViewModel() {
    private val query = MutableStateFlow("")
    private val category = MutableStateFlow<AnimalType?>(null)

    val animals: StateFlow<List<Animal>> = combine(
        query,
        category,
        animalRepository.animalsWithFavorites()
    ) { q, cat, list ->
        list.filter {
            (q.isBlank() || it.name.contains(q, true) || it.breed.contains(q, true) || it.location.contains(q, true)) &&
                (cat == null || it.type == cat)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val queryState = query.asStateFlow()
    val categoryState = category.asStateFlow()

    fun setQuery(value: String) { query.value = value }
    fun setCategory(type: AnimalType?) { category.value = type }
    fun toggleFavorite(id: String) = viewModelScope.launch { animalRepository.toggleFavorite(id) }
}

class FavoritesViewModel(private val animalRepository: AnimalRepository) : ViewModel() {
    val animals: StateFlow<List<Animal>> = animalRepository.animalsWithFavorites()
        .map { list -> list.filter { it.isFavorite } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleFavorite(id: String) = viewModelScope.launch { animalRepository.toggleFavorite(id) }
}

class DetailsViewModel(
    private val animalRepository: AnimalRepository,
    animalId: String
) : ViewModel() {
    private val _animal = MutableStateFlow<Animal?>(null)
    val animal: StateFlow<Animal?> = _animal.asStateFlow()

    init {
        viewModelScope.launch {
            animalRepository.animalsWithFavorites().collect { list ->
                _animal.value = list.find { it.id == animalId }
            }
        }
    }

    fun toggleFavorite() = viewModelScope.launch {
        _animal.value?.id?.let { animalRepository.toggleFavorite(it) }
    }
}

data class ApplicationFormState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val housingType: String = "Apartment",
    val hasOtherPets: Boolean = false,
    val experience: String = "",
    val reason: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val submitted: Boolean = false
)

class ApplicationViewModel(
    private val animalRepository: AnimalRepository,
    private val authRepository: AuthRepository,
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
                        phone = it.phone.ifBlank { user.phone }
                    )
                }
            }
        }
    }

    fun update(transform: (ApplicationFormState) -> ApplicationFormState) = _form.update(transform)

    fun submit() = viewModelScope.launch {
        val f = _form.value
        when {
            f.fullName.isBlank() -> _form.update { it.copy(error = "Name is required.") }
            f.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(f.email).matches() ->
                _form.update { it.copy(error = "Valid email is required.") }
            f.phone.isBlank() -> _form.update { it.copy(error = "Phone is required.") }
            f.address.isBlank() -> _form.update { it.copy(error = "Address is required.") }
            f.reason.isBlank() -> _form.update { it.copy(error = "Tell us why you’d like to adopt.") }
            else -> {
                _form.update { it.copy(loading = true, error = null) }
                animalRepository.submitApplication(
                    AdoptionApplication(
                        id = "",
                        animalId = animalId,
                        animalName = animalName,
                        applicantName = f.fullName.trim(),
                        email = f.email.trim(),
                        phone = f.phone.trim(),
                        address = f.address.trim(),
                        housingType = f.housingType,
                        hasOtherPets = f.hasOtherPets,
                        experience = f.experience.trim(),
                        reason = f.reason.trim(),
                        status = ApplicationStatus.PENDING
                    )
                )
                _form.update { it.copy(loading = false, submitted = true) }
            }
        }
    }
}

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val animalRepository: AnimalRepository
) : ViewModel() {
    val user: StateFlow<UserProfile> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProfile())
    val applications: StateFlow<List<AdoptionApplication>> = animalRepository.applications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun logout() = viewModelScope.launch { authRepository.logout() }
}

class NotificationsViewModel(private val animalRepository: AnimalRepository) : ViewModel() {
    val items: StateFlow<List<AppNotification>> = animalRepository.notifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun markRead(id: String) = animalRepository.markNotificationRead(id)
}

class ContentViewModel(private val animalRepository: AnimalRepository) : ViewModel() {
    val shelters: List<Shelter> = animalRepository.shelters()
    val clinics: List<Clinic> = animalRepository.clinics()
    val donations: List<DonationOption> = animalRepository.donations()
    val lostFound: StateFlow<List<LostFoundPost>> = animalRepository.lostFound()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class SplashViewModel(private val authRepository: AuthRepository) : ViewModel() {
    val onboardingDone = authRepository.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val loggedIn = authRepository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun completeOnboarding() = viewModelScope.launch { authRepository.completeOnboarding() }
}

@Suppress("UNCHECKED_CAST")
class AdoptMeViewModelFactory(
    private val container: AppContainer,
    private val animalId: String? = null,
    private val animalName: String? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(container.authRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(container.animalRepository, container.authRepository) as T
            modelClass.isAssignableFrom(AnimalsViewModel::class.java) ->
                AnimalsViewModel(container.animalRepository) as T
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
            modelClass.isAssignableFrom(NotificationsViewModel::class.java) ->
                NotificationsViewModel(container.animalRepository) as T
            modelClass.isAssignableFrom(ContentViewModel::class.java) ->
                ContentViewModel(container.animalRepository) as T
            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(container.authRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
