package com.adoptme.app.data.model

enum class AnimalType { CAT, DOG, OTHER }

enum class Gender { MALE, FEMALE }

enum class AnimalSize { SMALL, MEDIUM, LARGE }

enum class AgeGroup { UNDER_ONE, ONE_TO_THREE, OVER_THREE }

enum class HealthStatus { HEALTHY, NEEDS_CARE, RECOVERING }

enum class Availability { AVAILABLE, RESERVED, ADOPTED }

enum class ApplicationStatus { PENDING, APPROVED, REJECTED, CANCELLED }

enum class NewsCategory { SHELTER, CARE, STORY }

enum class LostFoundType { LOST, FOUND }

data class Animal(
    val id: String,
    val name: String,
    val type: AnimalType,
    val breed: String,
    val ageLabel: String,
    val ageMonths: Int,
    val gender: Gender,
    val size: AnimalSize,
    val healthStatus: HealthStatus,
    val availability: Availability,
    val personality: String,
    val description: String,
    val story: String,
    val shelterId: String,
    val shelterName: String,
    val city: String,
    val imageUrl: String,
    val vaccinated: Boolean = true,
    val sterilized: Boolean = false,
    val goodWithKids: Boolean = true,
    val isFavorite: Boolean = false
)

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val isAdmin: Boolean = false
)

data class AdoptionApplication(
    val id: String,
    val number: String,
    val sequence: Long,
    val animalId: String,
    val animalName: String,
    val userId: String,
    val applicantName: String,
    val email: String,
    val phone: String,
    val address: String,
    val experience: String,
    val reason: String,
    val status: ApplicationStatus,
    val submittedAt: Long
)

data class ApplicationDraft(
    val userId: String,
    val animalId: String,
    val animalName: String,
    val applicantName: String,
    val email: String,
    val phone: String,
    val address: String,
    val experience: String,
    val reason: String
)

data class Shelter(
    val id: String,
    val name: String,
    val city: String,
    val description: String,
    val contactNote: String,
    val hours: String,
    val isDemonstration: Boolean = true
)

data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val body: String,
    val category: NewsCategory,
    val publishedLabel: String
)

data class ChatConversation(
    val id: String,
    val title: String,
    val subtitle: String,
    val lastMessage: String,
    val updatedAt: Long
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val text: String,
    val fromUser: Boolean,
    val sentAt: Long
)

data class AppNotification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val createdAt: Long,
    val isRead: Boolean = false,
    val applicationId: String? = null
)

data class LostFoundPost(
    val id: String,
    val type: LostFoundType,
    val animalName: String,
    val species: String,
    val description: String,
    val location: String,
    val contact: String,
    val imageUrl: String,
    val date: String
)

data class Clinic(
    val id: String,
    val name: String,
    val city: String,
    val note: String,
    val specialties: String
)

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)

data class SessionSnapshot(
    val onboardingDone: Boolean = false,
    val userId: String = ""
)
