package com.adoptme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.ApplicationDraft
import com.adoptme.app.data.repository.AnimalRepository
import com.adoptme.app.data.repository.ApplicationRepository
import com.adoptme.app.data.repository.AuthRepository
import com.adoptme.app.utils.ClickLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdoptionFormState(
    val animal: Animal? = null,
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val experience: String = "",
    val reason: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val submittedNumber: String? = null
)

class AdoptionFormViewModel(
    private val animalRepository: AnimalRepository,
    private val applicationRepository: ApplicationRepository,
    private val authRepository: AuthRepository,
    private val animalId: String
) : ViewModel() {
    private val _form = MutableStateFlow(AdoptionFormState())
    val form: StateFlow<AdoptionFormState> = _form.asStateFlow()
    private val lock = ClickLock()

    init {
        viewModelScope.launch {
            animalRepository.observeAnimal(animalId).collect { animal ->
                _form.update { it.copy(animal = animal) }
            }
        }
        viewModelScope.launch {
            var filled = false
            authRepository.currentUser.collect { user ->
                if (filled || user.id.isBlank()) return@collect
                filled = true
                _form.update {
                    it.copy(
                        fullName = user.name,
                        email = user.email
                    )
                }
            }
        }
    }

    fun update(transform: (AdoptionFormState) -> AdoptionFormState) = _form.update(transform)

    fun submit() {
        if (_form.value.submittedNumber != null) return
        if (!lock.tryAcquire()) return
        val snapshot = _form.value
        val animal = snapshot.animal
        if (animal == null) {
            _form.update { it.copy(error = "Животное не найдено.") }
            lock.release()
            return
        }
        viewModelScope.launch {
            _form.update { it.copy(loading = true, error = null) }
            val userId = authRepository.currentUser.first().id
            val result = applicationRepository.submit(
                ApplicationDraft(
                    userId = userId,
                    animalId = animal.id,
                    animalName = animal.name,
                    applicantName = snapshot.fullName,
                    email = snapshot.email,
                    phone = snapshot.phone,
                    address = snapshot.address,
                    experience = snapshot.experience,
                    reason = snapshot.reason
                )
            )
            _form.update { current ->
                result.fold(
                    onSuccess = { saved ->
                        current.copy(loading = false, submittedNumber = saved.number, error = null)
                    },
                    onFailure = { error ->
                        current.copy(loading = false, error = error.message)
                    }
                )
            }
            if (result.isFailure) lock.release()
        }
    }
}

class MyApplicationsViewModel(
    private val applicationRepository: ApplicationRepository
) : ViewModel() {
    val applications: StateFlow<List<AdoptionApplication>> = applicationRepository.observeMine()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()
    private val lock = ClickLock()

    fun cancel(id: String) {
        if (!lock.tryAcquire()) return
        viewModelScope.launch {
            val result = applicationRepository.cancel(id)
            _message.value = result.fold(
                onSuccess = { "Заявка ${it.number} отменена." },
                onFailure = { it.message }
            )
            lock.release()
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
