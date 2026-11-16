@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.adoptme.app.data.repository

import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.dao.AnimalDao
import com.adoptme.app.data.local.dao.FavoriteDao
import com.adoptme.app.data.local.entity.FavoriteEntity
import com.adoptme.app.data.local.toEntity
import com.adoptme.app.data.local.toModel
import com.adoptme.app.data.model.Animal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class AnimalRepository(
    private val animalDao: AnimalDao,
    private val favoriteDao: FavoriteDao,
    private val sessionStore: SessionStore
) {
    fun observeAnimals(): Flow<List<Animal>> {
        return sessionStore.session.flatMapLatest { snapshot ->
            val favorites = if (snapshot.userId.isBlank()) {
                flowOf(emptyList())
            } else {
                favoriteDao.observeIds(snapshot.userId)
            }
            combine(animalDao.observeAll(), favorites) { animals, favoriteIds ->
                val favoriteSet = favoriteIds.toSet()
                animals.map { entity -> entity.toModel(isFavorite = entity.id in favoriteSet) }
            }
        }
    }

    fun observeAnimal(id: String): Flow<Animal?> = observeAnimals().map { animals ->
        animals.find { it.id == id }
    }

    suspend fun toggleFavorite(animalId: String) {
        val userId = sessionStore.currentUserId() ?: return
        if (favoriteDao.exists(userId, animalId)) {
            favoriteDao.delete(userId, animalId)
        } else {
            favoriteDao.insert(FavoriteEntity(userId, animalId))
        }
    }

    suspend fun save(animal: Animal): Result<Unit> {
        if (animal.name.trim().length < 2) {
            return Result.failure(IllegalArgumentException("Укажите кличку."))
        }
        if (animal.breed.isBlank() || animal.description.isBlank() || animal.story.isBlank() || animal.personality.isBlank()) {
            return Result.failure(IllegalArgumentException("Заполните породу, характер, описание и историю."))
        }
        if (animal.ageMonths <= 0 || animal.ageLabel.isBlank()) {
            return Result.failure(IllegalArgumentException("Укажите возраст."))
        }
        if (animal.city.isBlank() || animal.shelterName.isBlank()) {
            return Result.failure(IllegalArgumentException("Выберите приют."))
        }
        return try {
            animalDao.insert(animal.toEntity())
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(IllegalStateException("Не удалось сохранить карточку животного."))
        }
    }
}
