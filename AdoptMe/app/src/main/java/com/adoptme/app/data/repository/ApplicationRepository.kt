@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.adoptme.app.data.repository

import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.dao.AnimalDao
import com.adoptme.app.data.local.dao.ApplicationDao
import com.adoptme.app.data.local.dao.NotificationDao
import com.adoptme.app.data.local.dao.UserDao
import com.adoptme.app.data.local.entity.NotificationEntity
import com.adoptme.app.data.local.toModel
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.ApplicationDraft
import com.adoptme.app.data.model.Availability
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

class ApplicationRepository(
    private val applicationDao: ApplicationDao,
    private val animalDao: AnimalDao,
    private val notificationDao: NotificationDao,
    private val userDao: UserDao,
    private val sessionStore: SessionStore,
    private val service: ApplicationService
) {
    fun observeMine(): Flow<List<AdoptionApplication>> {
        return sessionStore.session.flatMapLatest { snapshot ->
            if (snapshot.userId.isBlank()) {
                flowOf(emptyList())
            } else {
                applicationDao.observeForUser(snapshot.userId).map { rows -> rows.map { it.toModel() } }
            }
        }
    }

    fun observeAll(): Flow<List<AdoptionApplication>> =
        applicationDao.observeAll().map { rows -> rows.map { it.toModel() } }

    suspend fun submit(draft: ApplicationDraft): Result<AdoptionApplication> {
        val animal = animalDao.get(draft.animalId)
            ?: return Result.failure(IllegalArgumentException("Животное не найдено."))
        if (animal.availability != Availability.AVAILABLE) {
            return Result.failure(IllegalStateException("Это животное сейчас нельзя усыновить."))
        }
        val saved = service.submit(draft)
        saved.onSuccess { application ->
            notifySafely(
                userId = application.userId,
                title = "Заявка отправлена",
                message = "Заявка ${application.number} на ${application.animalName} сохранена. Статус: На рассмотрении.",
                applicationId = application.id
            )
            userDao.adminIds().forEach { adminId ->
                if (adminId != application.userId) {
                    notifySafely(
                        userId = adminId,
                        title = "Новая заявка",
                        message = "Поступила заявка ${application.number} от ${application.applicantName} на ${application.animalName}.",
                        applicationId = application.id
                    )
                }
            }
        }
        return saved
    }

    suspend fun cancel(id: String): Result<AdoptionApplication> {
        val userId = sessionStore.currentUserId()
            ?: return Result.failure(IllegalStateException("Войдите в учётную запись."))
        val result = service.cancel(id, userId)
        result.onSuccess { application ->
            notifySafely(
                userId = userId,
                title = "Заявка отменена",
                message = "Заявка ${application.number} отменена.",
                applicationId = application.id
            )
        }
        return result
    }

    suspend fun review(id: String, approve: Boolean): Result<AdoptionApplication> {
        val userId = sessionStore.currentUserId()
            ?: return Result.failure(IllegalStateException("Войдите в учётную запись."))
        val actor = userDao.findById(userId)
        if (actor?.isAdmin != true) {
            return Result.failure(
                IllegalStateException("Решение по заявке доступно только демонстрационному администратору на этом устройстве.")
            )
        }
        val result = service.review(id, approve)
        result.onSuccess { application ->
            if (approve) {
                animalDao.updateAvailability(application.animalId, Availability.RESERVED)
            }
            val status = if (approve) "Одобрено" else "Отклонено"
            notifySafely(
                userId = application.userId,
                title = "Статус заявки изменён",
                message = "Заявка ${application.number}: $status.",
                applicationId = application.id
            )
        }
        return result
    }

    private suspend fun notifySafely(
        userId: String,
        title: String,
        message: String,
        applicationId: String
    ) {
        try {
            notificationDao.insert(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    title = title,
                    message = message,
                    createdAt = System.currentTimeMillis(),
                    isRead = false,
                    applicationId = applicationId
                )
            )
        } catch (_: Exception) {
            // The application itself is already stored. A missed notice must not look like a failed save.
        }
    }
}
