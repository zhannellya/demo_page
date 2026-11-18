@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.adoptme.app.data.repository

import com.adoptme.app.data.local.SampleData
import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.db.AnimalDao
import com.adoptme.app.data.local.db.AnimalEntity
import com.adoptme.app.data.local.db.ApplicationDao
import com.adoptme.app.data.local.db.ApplicationEntity
import com.adoptme.app.data.local.db.FavoriteDao
import com.adoptme.app.data.local.db.FavoriteEntity
import com.adoptme.app.data.local.db.ShelterDao
import com.adoptme.app.data.local.db.toDomain
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AppNotification
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Clinic
import com.adoptme.app.data.model.DonationOption
import com.adoptme.app.data.model.LostFoundPost
import com.adoptme.app.data.model.OwnerType
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.data.model.parseAgeMonths
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

data class NewPetDraft(
    val name: String,
    val type: com.adoptme.app.data.model.AnimalType,
    val breed: String,
    val gender: com.adoptme.app.data.model.Gender,
    val ageLabel: String,
    val city: String,
    val description: String,
    val healthStatus: com.adoptme.app.data.model.HealthStatus,
    val imageUri: String,
    val ownerType: OwnerType,
    val ownerName: String,
    val ownerPhone: String,
    val shelterId: String?
)

class AnimalRepository(
    private val sessionStore: SessionStore,
    private val animalDao: AnimalDao,
    private val favoriteDao: FavoriteDao,
    private val applicationDao: ApplicationDao,
    private val shelterDao: ShelterDao
) {
    private val seedMutex = Mutex()
    private val notifications = MutableStateFlow(SampleData.notifications)
    private val lostFoundPosts = MutableStateFlow(SampleData.lostFound)

    suspend fun ensureSeeded() {
        seedMutex.withLock {
            shelterDao.insertAll(SampleData.shelterEntities)
            if (animalDao.count() == 0) {
                animalDao.insertAll(SampleData.animalEntities)
            } else {
                SampleData.animalEntities.forEach { seed ->
                    val existing = animalDao.findById(seed.id)
                    if (existing == null) {
                        animalDao.insert(seed)
                    } else if (!existing.isUserCreated && (existing.city.isBlank() || existing.shelterId.isNullOrBlank())) {
                        animalDao.insert(seed.copy(isUserCreated = false))
                    }
                }
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

    fun observeAnimal(id: String): Flow<Animal?> = observeAnimals().map { list -> list.find { it.id == id } }

    fun observeAnimalsByShelter(shelterId: String): Flow<List<Animal>> =
        observeAnimals().map { list -> list.filter { it.shelterId == shelterId } }

    fun observeShelters(): Flow<List<Shelter>> = flow {
        ensureSeeded()
        emitAll(shelterDao.observeAll().map { list -> list.map { it.toDomain() } })
    }

    fun observeShelter(id: String): Flow<Shelter?> = flow {
        ensureSeeded()
        emitAll(shelterDao.observeById(id).map { it?.toDomain() })
    }

    suspend fun findShelter(id: String): Shelter? {
        ensureSeeded()
        return shelterDao.findById(id)?.toDomain()
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
        applicationDao.insert(saved.toEntity(userId))
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

    suspend fun publishPet(draft: NewPetDraft, applicant: com.adoptme.app.data.model.UserProfile): Result<Animal> {
        val userId = sessionStore.userId.first()
            ?: return Result.failure(IllegalStateException("Войдите в аккаунт, чтобы опубликовать питомца."))
        val shelter = draft.shelterId?.let { shelterDao.findById(it) }
        val location = when (draft.ownerType) {
            OwnerType.SHELTER -> listOfNotNull(shelter?.name, draft.city).joinToString(", ")
            OwnerType.PRIVATE -> draft.city
        }
        val ownerName = when (draft.ownerType) {
            OwnerType.SHELTER -> shelter?.name.orEmpty().ifBlank { draft.ownerName }
            OwnerType.PRIVATE -> draft.ownerName.ifBlank { applicant.name }
        }
        val ownerPhone = when (draft.ownerType) {
            OwnerType.SHELTER -> shelter?.phone.orEmpty()
            OwnerType.PRIVATE -> draft.ownerPhone
        }
        val animalId = UUID.randomUUID().toString()
        val entity = AnimalEntity(
            id = animalId,
            name = draft.name.trim(),
            type = draft.type.name,
            breed = draft.breed.trim().ifBlank { "Не указана" },
            ageMonths = parseAgeMonths(draft.ageLabel),
            ageLabel = draft.ageLabel.trim(),
            gender = draft.gender.name,
            healthStatus = draft.healthStatus.name,
            description = draft.description.trim(),
            imageUrl = draft.imageUri,
            location = location,
            size = "Средний",
            vaccinated = true,
            sterilized = true,
            goodWithKids = true,
            city = draft.city.trim(),
            ownerType = draft.ownerType.name,
            ownerName = ownerName,
            ownerPhone = ownerPhone,
            shelterId = if (draft.ownerType == OwnerType.SHELTER) draft.shelterId else null,
            isUserCreated = true
        )
        animalDao.insert(entity)
        val listing = AdoptionApplication(
            id = UUID.randomUUID().toString(),
            animalId = animalId,
            animalName = entity.name,
            applicantName = applicant.name.ifBlank { ownerName },
            email = applicant.email,
            phone = ownerPhone.ifBlank { applicant.phone },
            address = draft.city,
            housingType = if (draft.ownerType == OwnerType.SHELTER) "Приют" else "Частное лицо",
            hasOtherPets = false,
            experience = "",
            reason = "Публикация объявления: ${entity.name}",
            status = ApplicationStatus.PENDING,
            submittedAt = System.currentTimeMillis()
        )
        applicationDao.insert(listing.toEntity(userId))
        notifications.update { current ->
            listOf(
                AppNotification(
                    id = UUID.randomUUID().toString(),
                    title = "Объявление отправлено",
                    message = "${entity.name} появится в общем списке. Заявка на рассмотрении.",
                    time = "Только что",
                    isRead = false,
                    relatedApplicationId = listing.id
                )
            ) + current
        }
        return Result.success(entity.toDomain(isFavorite = false))
    }

    suspend fun deleteApplication(applicationId: String): Result<Unit> {
        val userId = sessionStore.userId.first()
            ?: return Result.failure(IllegalStateException("Войдите в аккаунт."))
        val application = applicationDao.findById(applicationId)
            ?: return Result.failure(IllegalStateException("Заявка не найдена."))
        if (application.userId != userId) {
            return Result.failure(IllegalStateException("Нельзя удалить чужую заявку."))
        }
        val animal = animalDao.findById(application.animalId)
        applicationDao.deleteById(applicationId)
        if (animal?.isUserCreated == true) {
            animalDao.deleteUserCreated(animal.id)
        }
        return Result.success(Unit)
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

private fun AdoptionApplication.toEntity(userId: Long) = ApplicationEntity(
    id = id,
    userId = userId,
    animalId = animalId,
    animalName = animalName,
    applicantName = applicantName,
    email = email,
    phone = phone,
    address = address,
    housingType = housingType,
    hasOtherPets = hasOtherPets,
    experience = experience,
    reason = reason,
    status = status.name,
    submittedAt = submittedAt
)
