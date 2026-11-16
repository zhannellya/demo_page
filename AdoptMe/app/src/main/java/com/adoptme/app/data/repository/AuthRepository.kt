@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.adoptme.app.data.repository

import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.dao.UserDao
import com.adoptme.app.data.local.entity.UserEntity
import com.adoptme.app.data.local.toProfile
import com.adoptme.app.data.model.SessionSnapshot
import com.adoptme.app.data.model.UserProfile
import com.adoptme.app.data.remote.AuthRemoteDataSource
import com.adoptme.app.data.remote.DisabledAuthRemoteDataSource
import com.adoptme.app.utils.AuthValidator
import com.adoptme.app.utils.PasswordHasher
import com.adoptme.app.utils.ValidationMessages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

class AuthRepository(
    private val userDao: UserDao,
    private val sessionStore: SessionStore,
    private val remote: AuthRemoteDataSource = DisabledAuthRemoteDataSource()
) {
    val session: Flow<SessionSnapshot> = sessionStore.session

    val currentUser: Flow<UserProfile> = sessionStore.session.flatMapLatest { snapshot ->
        if (snapshot.userId.isBlank()) {
            flowOf(UserProfile())
        } else {
            userDao.observeById(snapshot.userId).map { entity -> entity?.toProfile() ?: UserProfile() }
        }
    }

    suspend fun completeOnboarding() = sessionStore.setOnboardingCompleted()

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        AuthValidator.validateRegistration(name, email, password, confirmPassword)?.let { message ->
            return Result.failure(IllegalArgumentException(message))
        }
        val normalizedEmail = email.trim().lowercase()
        if (userDao.findByEmail(normalizedEmail) != null) {
            return Result.failure(IllegalArgumentException(ValidationMessages.EMAIL_TAKEN))
        }
        if (remote.isEnabled) {
            remote.register(normalizedEmail, password).onFailure {
                return Result.failure(IllegalStateException("Не удалось создать учётную запись на сервере."))
            }
        }
        val salt = PasswordHasher.generateSalt()
        val user = UserEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            email = normalizedEmail,
            phone = "",
            city = "",
            passwordSalt = salt,
            passwordHash = PasswordHasher.hash(password, salt),
            isAdmin = false,
            createdAt = System.currentTimeMillis()
        )
        return try {
            userDao.insert(user)
            sessionStore.startSession(user.id)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(IllegalStateException("Не удалось сохранить учётную запись. Попробуйте ещё раз."))
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        AuthValidator.validateLogin(email, password)?.let { message ->
            return Result.failure(IllegalArgumentException(message))
        }
        val normalizedEmail = email.trim().lowercase()
        val user = userDao.findByEmail(normalizedEmail)
        if (user == null || !PasswordHasher.verify(password, user.passwordSalt, user.passwordHash)) {
            return Result.failure(IllegalArgumentException(ValidationMessages.LOGIN_FAILED))
        }
        if (remote.isEnabled) {
            remote.signIn(normalizedEmail, password).onFailure {
                return Result.failure(IllegalStateException("Сервер отклонил вход."))
            }
        }
        sessionStore.startSession(user.id)
        return Result.success(Unit)
    }

    suspend fun logout() = sessionStore.logout()
}
