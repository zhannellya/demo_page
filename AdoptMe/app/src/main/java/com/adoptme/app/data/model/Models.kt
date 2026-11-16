package com.adoptme.app.data.model

enum class AnimalType { CAT, DOG, OTHER }
enum class Gender { MALE, FEMALE }
enum class HealthStatus { HEALTHY, NEEDS_CARE, RECOVERING }
enum class ApplicationStatus { PENDING, UNDER_REVIEW, APPROVED, REJECTED }
enum class LostFoundType { LOST, FOUND }

enum class AgeGroup(val title: String) {
    UNDER_ONE("До 1 года"),
    ONE_TO_THREE("1–3 года"),
    OVER_THREE("Старше 3 лет");

    fun matches(ageMonths: Int): Boolean = when (this) {
        UNDER_ONE -> ageMonths < 12
        ONE_TO_THREE -> ageMonths in 12..36
        OVER_THREE -> ageMonths > 36
    }
}

data class AnimalFilter(
    val query: String = "",
    val type: AnimalType? = null,
    val gender: Gender? = null,
    val breed: String? = null,
    val ageGroup: AgeGroup? = null
)

data class Animal(
    val id: String,
    val name: String,
    val type: AnimalType,
    val breed: String,
    val age: String,
    val ageMonths: Int,
    val gender: Gender,
    val healthStatus: HealthStatus,
    val description: String,
    val imageUrl: String,
    val location: String,
    val size: String,
    val vaccinated: Boolean = true,
    val sterilized: Boolean = true,
    val goodWithKids: Boolean = true,
    val isFavorite: Boolean = false
)

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val avatarUrl: String = ""
)

data class AdoptionApplication(
    val id: String,
    val animalId: String,
    val animalName: String,
    val applicantName: String,
    val email: String,
    val phone: String,
    val address: String,
    val housingType: String,
    val hasOtherPets: Boolean,
    val experience: String,
    val reason: String,
    val status: ApplicationStatus = ApplicationStatus.PENDING,
    val submittedAt: Long = System.currentTimeMillis()
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

data class Shelter(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val openHours: String
)

data class Clinic(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val specialties: String,
    val rating: Float
)

data class DonationOption(
    val id: String,
    val title: String,
    val description: String,
    val amountLabel: String,
    val icon: String
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false,
    val relatedApplicationId: String? = null
)

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)

fun Gender.titleRu(): String = when (this) {
    Gender.MALE -> "Мальчик"
    Gender.FEMALE -> "Девочка"
}

fun HealthStatus.titleRu(): String = when (this) {
    HealthStatus.HEALTHY -> "Здоров"
    HealthStatus.RECOVERING -> "Восстанавливается"
    HealthStatus.NEEDS_CARE -> "Нужен уход"
}

fun ApplicationStatus.titleRu(): String = when (this) {
    ApplicationStatus.PENDING -> "На рассмотрении"
    ApplicationStatus.UNDER_REVIEW -> "Проверяется"
    ApplicationStatus.APPROVED -> "Одобрена"
    ApplicationStatus.REJECTED -> "Отклонена"
}

fun AnimalType.titleRu(): String = when (this) {
    AnimalType.CAT -> "Кошка"
    AnimalType.DOG -> "Собака"
    AnimalType.OTHER -> "Другое"
}

fun LostFoundType.titleRu(): String = when (this) {
    LostFoundType.LOST -> "Потерялся"
    LostFoundType.FOUND -> "Нашёлся"
}
