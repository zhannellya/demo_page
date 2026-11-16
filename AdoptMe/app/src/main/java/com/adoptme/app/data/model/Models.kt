package com.adoptme.app.data.model

enum class Species { CAT, DOG, OTHER }

enum class Gender { MALE, FEMALE }

enum class PetSize { SMALL, MEDIUM, LARGE }

enum class AgeGroup { BABY, YOUNG, ADULT, SENIOR }

enum class AnimalStatus { AVAILABLE, RESERVED, ADOPTED }

enum class ApplicationStatus { PENDING, APPROVED, REJECTED, CANCELLED }

enum class UserRole { USER, LOCAL_ADMIN }

enum class MessageSender { USER, SHELTER }

fun ageGroupOf(ageMonths: Int): AgeGroup = when {
    ageMonths < 12 -> AgeGroup.BABY
    ageMonths < 36 -> AgeGroup.YOUNG
    ageMonths < 84 -> AgeGroup.ADULT
    else -> AgeGroup.SENIOR
}

data class Animal(
    val id: String,
    val name: String,
    val species: Species,
    val breed: String,
    val ageMonths: Int,
    val ageLabel: String,
    val ageGroup: AgeGroup,
    val gender: Gender,
    val size: PetSize,
    val temperament: String,
    val story: String,
    val shelterId: String,
    val shelterName: String,
    val city: String,
    val imageKey: String,
    val status: AnimalStatus,
    val isFavorite: Boolean = false
)

data class Shelter(
    val id: String,
    val name: String,
    val city: String,
    val description: String,
    val phone: String,
    val email: String,
    val hours: String,
    val addressNote: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val role: UserRole = UserRole.USER
)

data class AdoptionApplication(
    val id: String,
    val number: String,
    val userId: String,
    val animalId: String,
    val animalName: String,
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
    val animalStatus: AnimalStatus,
    val applicantName: String,
    val email: String,
    val phone: String,
    val address: String,
    val experience: String,
    val reason: String
)

data class NewsArticle(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val body: String,
    val publishedAt: Long
)

data class ChatThread(
    val id: String,
    val userId: String,
    val title: String,
    val subtitle: String,
    val shelterId: String?
)

data class ChatMessage(
    val id: String,
    val threadId: String,
    val sender: MessageSender,
    val text: String,
    val sentAt: Long
)

data class AnimalQuery(
    val name: String = "",
    val species: Species? = null,
    val ageGroup: AgeGroup? = null,
    val size: PetSize? = null,
    val gender: Gender? = null,
    val city: String? = null
)
