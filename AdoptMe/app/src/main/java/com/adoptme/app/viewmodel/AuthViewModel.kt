package com.adoptme.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptme.app.data.repository.AuthRepository
import com.adoptme.app.di.AppContainer
import com.adoptme.app.utils.ClickLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SplashGate(
    val ready: Boolean = false,
    val onboardingDone: Boolean = false,
    val loggedIn: Boolean = false,
    val error: String? = null
)

class SplashViewModel(private val container: AppContainer) : ViewModel() {
    val gate: StateFlow<SplashGate> = combine(
        container.ready,
        container.startupError,
        container.authRepository.session
    ) { ready, error, session ->
        SplashGate(
            ready = ready,
            onboardingDone = session.onboardingDone,
            loggedIn = session.userId.isNotBlank(),
            error = error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SplashGate())

    fun retry() = container.seed()
}

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()
    private val lock = ClickLock()

    fun update(transform: (AuthUiState) -> AuthUiState) = _ui.update(transform)

    fun signUp() {
        if (!lock.tryAcquire()) return
        viewModelScope.launch {
            val state = _ui.value
            _ui.update { it.copy(loading = true, error = null) }
            val result = authRepository.signUp(state.name, state.email, state.password, state.confirmPassword)
            _ui.update { current ->
                result.fold(
                    onSuccess = { current.copy(loading = false, success = true, error = null) },
                    onFailure = { error -> current.copy(loading = false, error = error.message) }
                )
            }
            if (result.isFailure) lock.release()
        }
    }

    fun login() {
        if (!lock.tryAcquire()) return
        viewModelScope.launch {
            val state = _ui.value
            _ui.update { it.copy(loading = true, error = null) }
            val result = authRepository.login(state.email, state.password)
            _ui.update { current ->
                result.fold(
                    onSuccess = { current.copy(loading = false, success = true, error = null) },
                    onFailure = { error -> current.copy(loading = false, error = error.message) }
                )
            }
            if (result.isFailure) lock.release()
        }
    }
}
