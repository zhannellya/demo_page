package com.adoptme.app.data.repository

import com.adoptme.app.data.local.SampleData
import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.AppNotification
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Clinic
import com.adoptme.app.data.model.DonationOption
import com.adoptme.app.data.model.LostFoundPost
import com.adoptme.app.data.model.Shelter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import java.util.UUID

class AnimalRepository(private val sessionStore: SessionStore) {

    private val applications = MutableStateFlow(SampleData.sampleApplications)
    private val notifications = MutableStateFlow(SampleData.notifications)
    private val lostFoundPosts = MutableStateFlow(SampleData.lostFound)

    val favoriteIds: Flow<Set<String>> = sessionStore.favoriteIds

    fun animalsWithFavorites(): Flow<List<Animal>> =
        favoriteIds.combine(MutableStateFlow(SampleData.animals)) { favs, animals ->
            animals.map { it.copy(isFavorite = it.id in favs) }
        }

    fun getAnimal(id: String): Animal? = SampleData.animals.find { it.id == id }

    fun searchAnimals(
        query: String,
        type: AnimalType?,
        favoritesOnly: Boolean,
        favoriteIds: Set<String>
    ): List<Animal> {
        return SampleData.animals
            .map { it.copy(isFavorite = it.id in favoriteIds) }
            .filter { animal ->
                val matchesQuery = query.isBlank() ||
                    animal.name.contains(query, true) ||
                    animal.breed.contains(query, true) ||
                    animal.location.contains(query, true)
                val matchesType = type == null || animal.type == type
                val matchesFav = !favoritesOnly || animal.isFavorite
                matchesQuery && matchesType && matchesFav
            }
    }

    suspend fun toggleFavorite(id: String) = sessionStore.toggleFavorite(id)

    fun shelters(): List<Shelter> = SampleData.shelters
    fun clinics(): List<Clinic> = SampleData.clinics
    fun donations(): List<DonationOption> = SampleData.donations
    fun lostFound(): Flow<List<LostFoundPost>> = lostFoundPosts
    fun applications(): Flow<List<AdoptionApplication>> = applications
    fun notifications(): Flow<List<AppNotification>> = notifications

    fun submitApplication(application: AdoptionApplication): Result<AdoptionApplication> {
        val saved = application.copy(
            id = UUID.randomUUID().toString(),
            status = ApplicationStatus.PENDING,
            submittedAt = System.currentTimeMillis()
        )
        applications.update { listOf(saved) + it }
        notifications.update {
            listOf(
                AppNotification(
                    id = UUID.randomUUID().toString(),
                    title = "Application submitted",
                    message = "We received your application for ${saved.animalName}.",
                    time = "Just now",
                    isRead = false,
                    relatedApplicationId = saved.id
                )
            ) + it
        }
        return Result.success(saved)
    }

    fun markNotificationRead(id: String) {
        notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }
}
