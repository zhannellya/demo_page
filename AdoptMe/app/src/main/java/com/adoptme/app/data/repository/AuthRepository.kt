package com.adoptme.app.data.repository

import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.dao.UserDao
import com.adoptme.app.data.local.entity.UserEntity
import com.adoptme.app.data.model.UserProfile
import com.adoptme.app.data.model.UserRole
import com.adoptme.app.remote.AuthRemoteDataSource
import com.adoptme.app.utils.FormValidators
import com.adoptme.app.utils.PasswordHasher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepository(
    private val userDao: UserDao,
    private val sessionStore: SessionStore,
    private val remote: AuthRemoteDataSource
) {
    val session = sessionStore.session

    val currentUser: Flow<UserProfile> = sessionStore.session.flatMapLatest { session ->
        if (!session.loggedIn || session.userId.isBlank()) {
            flowOf(UserProfile())
        } else {
            userDao.observeById(session.userId).map { entity ->
                entity?.toDomain() ?: UserProfile()
            }
        }
    }

    suspend fun completeOnboarding() = sessionStore.setOnboardingCompleted(true)

    suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        val validation = FormValidators.validateRegister(name, email, password, confirmPassword)
        if (validation != null) return Result.failure(IllegalArgumentException(validation))
        val normalized = email.trim().lowercase()
        if (userDao.findByEmail(normalized) != null) {
            return Result.failure(IllegalArgumentException("Аккаунт с таким email уже есть."))
        }
        val salt = PasswordHasher.newSalt()
        val user = UserEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            email = normalized,
            passwordHash = PasswordHasher.hash(password, salt),
            passwordSalt = salt,
            phone = "",
            city = "",
            role = UserRole.USER.name,
            createdAt = System.currentTimeMillis()
        )
        userDao.insert(user)
        sessionStore.saveLogin(user.id)
        remote.onLocalAccountReady(user.id, user.email)
        return Result.success(Unit)
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        val validation = FormValidators.validateLogin(email, password)
        if (validation != null) return Result.failure(IllegalArgumentException(validation))
        val user = userDao.findByEmail(email.trim().lowercase())
        if (user == null || !PasswordHasher.verify(password, user.passwordSalt, user.passwordHash)) {
            return Result.failure(IllegalArgumentException("Неверный email или пароль."))
        }
        sessionStore.saveLogin(user.id)
        return Result.success(Unit)
    }

    suspend fun logout() = sessionStore.logout()

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        sessionStore.setNotificationsEnabled(enabled)
    }
}
