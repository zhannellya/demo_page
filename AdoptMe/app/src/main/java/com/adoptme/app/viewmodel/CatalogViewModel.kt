package com.adoptme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalSize
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.UserProfile
import com.adoptme.app.data.repository.AnimalRepository
import com.adoptme.app.data.repository.AuthRepository
import com.adoptme.app.utils.AnimalQuery
import com.adoptme.app.utils.filterAnimals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CatalogUiState(
    val query: AnimalQuery = AnimalQuery(),
    val animals: List<Animal> = emptyList(),
    val userName: String = "",
    val cities: List<String> = emptyList(),
    val filtersOpen: Boolean = false
)

class CatalogViewModel(
    private val animalRepository: AnimalRepository,
    authRepository: AuthRepository
) : ViewModel() {
    private val query = MutableStateFlow(AnimalQuery())
    private val filtersOpen = MutableStateFlow(false)

    val ui: StateFlow<CatalogUiState> = combine(
        animalRepository.observeAnimals(),
        query,
        filtersOpen,
        authRepository.currentUser
    ) { animals, currentQuery, open, user ->
        CatalogUiState(
            query = currentQuery,
            animals = animals.filterAnimals(currentQuery),
            userName = user.name,
            cities = animals.map { it.city }.distinct().sorted(),
            filtersOpen = open
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogUiState())

    fun setText(value: String) {
        query.value = query.value.copy(text = value)
    }

    fun setType(type: AnimalType?) {
        query.value = query.value.copy(type = type)
    }

    fun setAge(age: AgeGroup?) {
        query.value = query.value.copy(ageGroup = age)
    }

    fun setSize(size: AnimalSize?) {
        query.value = query.value.copy(size = size)
    }

    fun setGender(gender: Gender?) {
        query.value = query.value.copy(gender = gender)
    }

    fun setCity(city: String?) {
        query.value = query.value.copy(city = city)
    }

    fun toggleFilters() {
        filtersOpen.value = !filtersOpen.value
    }

    fun resetFilters() {
        query.value = AnimalQuery(text = query.value.text)
    }

    fun toggleFavorite(id: String) = viewModelScope.launch {
        animalRepository.toggleFavorite(id)
    }
}

class FavoritesViewModel(private val animalRepository: AnimalRepository) : ViewModel() {
    val animals: StateFlow<List<Animal>> = animalRepository.observeAnimals()
        .map { list -> list.filter { it.isFavorite } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleFavorite(id: String) = viewModelScope.launch {
        animalRepository.toggleFavorite(id)
    }
}

class DetailsViewModel(
    private val animalRepository: AnimalRepository,
    animalId: String
) : ViewModel() {
    val animal: StateFlow<Animal?> = animalRepository.observeAnimal(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun toggleFavorite() = viewModelScope.launch {
        animal.value?.id?.let { animalRepository.toggleFavorite(it) }
    }
}

class ProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {
    val user: StateFlow<UserProfile> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProfile())

    fun logout() = viewModelScope.launch { authRepository.logout() }
}
