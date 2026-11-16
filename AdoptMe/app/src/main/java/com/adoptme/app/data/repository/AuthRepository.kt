@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.adoptme.app.data.repository

import android.util.Patterns
import com.adoptme.app.data.local.PasswordHasher
import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.db.UserDao
import com.adoptme.app.data.local.db.UserEntity
import com.adoptme.app.data.local.db.toProfile
import com.adoptme.app.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepository(
    private val sessionStore: SessionStore,
    private val userDao: UserDao
) {
    val isLoggedIn: Flow<Boolean> = sessionStore.userId.map { it != null }

    val currentUser: Flow<UserProfile> = sessionStore.userId.flatMapLatest { id ->
        if (id == null) {
            flowOf(UserProfile())
        } else {
            userDao.observeById(id).map { entity -> entity?.toProfile() ?: UserProfile() }
        }
    }

    suspend fun isLoggedInNow(): Boolean = sessionStore.userId.map { it != null }.first()

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        phone: String,
        city: String
    ): Result<Unit> = withContext(Dispatchers.Default) {
        val cleanName = name.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.trim()
        val cleanCity = city.trim()
        when {
            cleanName.isBlank() -> Result.failure(IllegalArgumentException("Введите имя."))
            !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() ->
                Result.failure(IllegalArgumentException("Введите корректный email."))
            password.length < 6 ->
                Result.failure(IllegalArgumentException("Пароль должен содержать не меньше 6 символов."))
            userDao.findByEmail(cleanEmail) != null ->
                Result.failure(IllegalArgumentException("Аккаунт с таким email уже существует."))
            else -> {
                val salt = PasswordHasher.newSalt()
                val id = userDao.insert(
                    UserEntity(
                        name = cleanName,
                        email = cleanEmail,
                        phone = cleanPhone,
                        city = cleanCity,
                        passwordHash = PasswordHasher.hash(password, salt),
                        passwordSalt = salt,
                        createdAt = System.currentTimeMillis()
                    )
                )
                sessionStore.setUserId(id)
                Result.success(Unit)
            }
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> = withContext(Dispatchers.Default) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || password.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Введите email и пароль."))
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return@withContext Result.failure(IllegalArgumentException("Введите корректный email."))
        }
        val user = userDao.findByEmail(cleanEmail)
        if (user == null || !PasswordHasher.verify(password, user.passwordSalt, user.passwordHash)) {
            return@withContext Result.failure(IllegalArgumentException("Неверный email или пароль."))
        }
        sessionStore.setUserId(user.id)
        Result.success(Unit)
    }

    suspend fun updateProfile(name: String, phone: String, city: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            val cleanName = name.trim()
            if (cleanName.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Имя не может быть пустым."))
            }
            val id = sessionStore.userId.first()
                ?: return@withContext Result.failure(IllegalStateException("Нет активной сессии."))
            val user = userDao.findById(id)
                ?: return@withContext Result.failure(IllegalStateException("Профиль не найден."))
            userDao.update(
                user.copy(
                    name = cleanName,
                    phone = phone.trim(),
                    city = city.trim()
                )
            )
            Result.success(Unit)
        }

    suspend fun logout() {
        sessionStore.clear()
    }
}
