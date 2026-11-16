@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.adoptme.app.data.repository

import com.adoptme.app.data.local.SampleData
import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.db.AnimalDao
import com.adoptme.app.data.local.db.ApplicationDao
import com.adoptme.app.data.local.db.ApplicationEntity
import com.adoptme.app.data.local.db.FavoriteDao
import com.adoptme.app.data.local.db.FavoriteEntity
import com.adoptme.app.data.local.db.toDomain
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AppNotification
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Clinic
import com.adoptme.app.data.model.DonationOption
import com.adoptme.app.data.model.LostFoundPost
import com.adoptme.app.data.model.Shelter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class AnimalRepository(
    private val sessionStore: SessionStore,
    private val animalDao: AnimalDao,
    private val favoriteDao: FavoriteDao,
    private val applicationDao: ApplicationDao
) {
    private val seedMutex = Mutex()
    private val notifications = MutableStateFlow(SampleData.notifications)
    private val lostFoundPosts = MutableStateFlow(SampleData.lostFound)

    suspend fun ensureSeeded() {
        seedMutex.withLock {
            if (animalDao.count() == 0) {
                animalDao.insertAll(SampleData.animalEntities)
            }
        }
    }

    fun observeAnimals(): Flow<List<Animal>> = sessionStore.userId.flatMapLatest { userId ->
        flow {
            ensureSeeded()
            if (userId == null) {
                emitAll(animalDao.observeAll().map { list -> list.map { it.toDomain(isFavorite = false) } })
            } else {
                emitAll(
                    combine(animalDao.observeAll(), favoriteDao.observeIds(userId)) { animals, favoriteIds ->
                        val favorites = favoriteIds.toSet()
                        animals.map { it.toDomain(isFavorite = it.id in favorites) }
                    }
                )
            }
        }
    }

    suspend fun toggleFavorite(animalId: String) {
        val userId = sessionStore.userId.first() ?: return
        if (favoriteDao.isFavorite(userId, animalId)) {
            favoriteDao.delete(userId, animalId)
        } else {
            favoriteDao.insert(FavoriteEntity(userId = userId, animalId = animalId))
        }
    }

    fun applications(): Flow<List<AdoptionApplication>> = sessionStore.userId.flatMapLatest { userId ->
        if (userId == null) {
            flowOf(emptyList())
        } else {
            applicationDao.observeForUser(userId).map { list -> list.map { it.toDomain() } }
        }
    }

    suspend fun submitApplication(application: AdoptionApplication): Result<AdoptionApplication> {
        val userId = sessionStore.userId.first()
            ?: return Result.failure(IllegalStateException("Войдите в аккаунт, чтобы отправить заявку."))
        if (applicationDao.pendingCount(userId, application.animalId) > 0) {
            return Result.failure(IllegalStateException("Заявка на это животное уже отправлена."))
        }
        val saved = application.copy(
            id = UUID.randomUUID().toString(),
            status = ApplicationStatus.PENDING,
            submittedAt = System.currentTimeMillis()
        )
        applicationDao.insert(
            ApplicationEntity(
                id = saved.id,
                userId = userId,
                animalId = saved.animalId,
                animalName = saved.animalName,
                applicantName = saved.applicantName,
                email = saved.email,
                phone = saved.phone,
                address = saved.address,
                housingType = saved.housingType,
                hasOtherPets = saved.hasOtherPets,
                experience = saved.experience,
                reason = saved.reason,
                status = saved.status.name,
                submittedAt = saved.submittedAt
            )
        )
        notifications.update { current ->
            listOf(
                AppNotification(
                    id = UUID.randomUUID().toString(),
                    title = "Заявка отправлена",
                    message = "Приют получил заявку на ${saved.animalName}.",
                    time = "Только что",
                    isRead = false,
                    relatedApplicationId = saved.id
                )
            ) + current
        }
        return Result.success(saved)
    }

    fun shelters(): List<Shelter> = SampleData.shelters
    fun clinics(): List<Clinic> = SampleData.clinics
    fun donations(): List<DonationOption> = SampleData.donations
    fun lostFound(): Flow<List<LostFoundPost>> = lostFoundPosts
    fun notifications(): Flow<List<AppNotification>> = notifications

    fun markNotificationRead(id: String) {
        notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }
}
