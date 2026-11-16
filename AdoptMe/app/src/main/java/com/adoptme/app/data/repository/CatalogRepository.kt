package com.adoptme.app.data.repository

import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.dao.AnimalDao
import com.adoptme.app.data.local.dao.ApplicationDao
import com.adoptme.app.data.local.dao.FavoriteDao
import com.adoptme.app.data.local.dao.ShelterDao
import com.adoptme.app.data.local.entity.FavoriteEntity
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalStatus
import com.adoptme.app.data.model.ApplicationDraft
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.remote.CatalogRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogRepository(
    private val animalDao: AnimalDao,
    private val shelterDao: ShelterDao,
    private val favoriteDao: FavoriteDao,
    private val applicationDao: ApplicationDao,
    private val sessionStore: SessionStore,
    private val submission: ApplicationSubmission,
    remote: CatalogRemoteDataSource
) {
    init {
        remote.prepare()
    }

    val shelters: Flow<List<Shelter>> = shelterDao.observeAll().map { list -> list.map { it.toDomain() } }

    val animals: Flow<List<Animal>> = sessionStore.session.flatMapLatest { session ->
        combine(
            animalDao.observeAll(),
            shelterDao.observeAll(),
            if (session.userId.isBlank()) flowOf(emptyList()) else favoriteDao.observeIds(session.userId)
        ) { animals, shelters, favorites ->
            val names = shelters.associate { it.id to it.name }
            animals.map { entity ->
                entity.toDomain(
                    shelterName = names[entity.shelterId] ?: "Приют",
                    favorite = entity.id in favorites
                )
            }
        }
    }

    fun observeAnimal(id: String): Flow<Animal?> = animals.map { list -> list.find { it.id == id } }

    fun observeShelter(id: String): Flow<Shelter?> = shelterDao.observeById(id).map { it?.toDomain() }

    val myApplications: Flow<List<AdoptionApplication>> = sessionStore.session.flatMapLatest { session ->
        if (session.userId.isBlank()) flowOf(emptyList())
        else applicationDao.observeForUser(session.userId).map { list -> list.map { it.toDomain() } }
    }

    val allApplications: Flow<List<AdoptionApplication>> =
        applicationDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeApplication(id: String): Flow<AdoptionApplication?> =
        applicationDao.observeById(id).map { it?.toDomain() }

    suspend fun toggleFavorite(animalId: String) {
        val userId = sessionStore.snapshot().userId
        if (userId.isBlank()) return
        if (favoriteDao.count(userId, animalId) > 0) {
            favoriteDao.delete(userId, animalId)
        } else {
            favoriteDao.insert(FavoriteEntity(userId, animalId))
        }
    }

    suspend fun submitApplication(draft: ApplicationDraft): Result<AdoptionApplication> {
        return submission.submit(draft)
    }

    suspend fun cancelApplication(id: String): Result<Unit> {
        val entity = applicationDao.findById(id) ?: return Result.failure(
            IllegalStateException("Заявка не найдена.")
        )
        val session = sessionStore.snapshot()
        if (entity.userId != session.userId) {
            return Result.failure(IllegalStateException("Эту заявку нельзя отменить."))
        }
        if (entity.status != ApplicationStatus.PENDING.name) {
            return Result.failure(IllegalStateException("Отменить можно только заявку на рассмотрении."))
        }
        applicationDao.updateStatus(id, ApplicationStatus.CANCELLED.name)
        return Result.success(Unit)
    }

    suspend fun setApplicationStatus(id: String, status: ApplicationStatus) {
        val app = applicationDao.findById(id) ?: return
        applicationDao.updateStatus(id, status.name)
        if (status == ApplicationStatus.APPROVED) {
            val animal = animalDao.findById(app.animalId) ?: return
            if (animal.status == AnimalStatus.AVAILABLE.name) {
                animalDao.updateStatus(animal.id, AnimalStatus.RESERVED.name)
            }
        }
    }

    suspend fun saveAnimal(animal: Animal) {
        animalDao.upsert(animal.toEntity())
    }

    suspend fun setAnimalStatus(id: String, status: AnimalStatus) {
        animalDao.updateStatus(id, status.name)
    }
}
