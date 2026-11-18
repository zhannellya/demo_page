package com.adoptme.app.data.model

enum class AnimalType { CAT, DOG, OTHER }
enum class Gender { MALE, FEMALE }
enum class HealthStatus { HEALTHY, NEEDS_CARE, RECOVERING }
enum class ApplicationStatus { PENDING, UNDER_REVIEW, APPROVED, REJECTED }
enum class LostFoundType { LOST, FOUND }
enum class OwnerType { PRIVATE, SHELTER }
enum class ChatParticipantType { PRIVATE, PRIVATE_OWNER, SHELTER, AI }
enum class ChatSenderType { USER, PARTICIPANT, AI }
enum class DonationStatus { TEST_SUCCESS, TEST_PENDING, TEST_FAILED }
enum class PaymentMethod { BANK_CARD }

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
    val isFavorite: Boolean = false,
    val city: String = "",
    val ownerType: OwnerType = OwnerType.SHELTER,
    val ownerName: String = "",
    val ownerPhone: String = "",
    val shelterId: String? = null,
    val isUserCreated: Boolean = false
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
    val openHours: String,
    val city: String = "",
    val description: String = "",
    val logoUri: String = "",
    val bankDetails: String = ""
) {
    val workingHours: String get() = openHours
}

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
    val icon: String,
    val amountKzt: Int? = null,
    val purposeKey: String = icon
)

data class ChatConversation(
    val id: String,
    val participantId: String,
    val participantType: ChatParticipantType,
    val participantName: String,
    val updatedAt: Long,
    val avatarUri: String = "",
    val lastMessage: String = "",
    val lastMessageAt: Long = updatedAt,
    val unreadCount: Int = 0,
    val pinned: Boolean = false
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val text: String,
    val isFromCurrentUser: Boolean,
    val sentAt: Long,
    val senderType: ChatSenderType = if (isFromCurrentUser) ChatSenderType.USER else ChatSenderType.PARTICIPANT,
    val isRead: Boolean = true
)

data class ChatContact(
    val participantId: String,
    val participantType: ChatParticipantType,
    val name: String,
    val subtitle: String = "",
    val avatarUri: String = ""
)

object AdoptMeAiChat {
    const val PARTICIPANT_ID = "adoptme_ai"
    const val NAME = "ИИ-помощник ADOPTME"
    const val SUBTITLE = "Спросите об уходе за питомцем"
    const val GREETING =
        "Здравствуйте! Я ИИ-помощник ADOPTME. Спросите меня об уходе, питании, поведении или здоровье питомца"
}

data class Donation(
    val id: String,
    val shelterId: String,
    val purpose: String,
    val amountKzt: Int,
    val createdAt: Long,
    val status: DonationStatus
)

data class DonationRequest(
    val shelterId: String,
    val purpose: String,
    val amountKzt: Int,
    val method: PaymentMethod
)

data class DonationReceipt(
    val id: String,
    val shelterId: String,
    val purpose: String,
    val amountKzt: Int,
    val createdAt: Long,
    val status: DonationStatus
)

object KazakhstanCities {
    val featured = listOf("Алматы", "Астана", "Шымкент", "Караганда", "Тараз")
}

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

fun OwnerType.titleRu(): String = when (this) {
    OwnerType.PRIVATE -> "Частное лицо"
    OwnerType.SHELTER -> "Приют"
}

fun DonationStatus.titleRu(): String = when (this) {
    DonationStatus.TEST_SUCCESS -> "Тестовый платёж выполнен"
    DonationStatus.TEST_PENDING -> "Тестовый платёж обрабатывается"
    DonationStatus.TEST_FAILED -> "Тестовый платёж не выполнен"
}

fun PaymentMethod.titleRu(): String = when (this) {
    PaymentMethod.BANK_CARD -> "Банковская карта"
}

fun formatKzt(amount: Int): String {
    val grouped = "%,d".format(java.util.Locale.US, amount).replace(',', ' ')
    return "$grouped ₸"
}

fun Animal.chatParticipantId(): String = when (ownerType) {
    OwnerType.SHELTER -> shelterId?.takeIf { it.isNotBlank() } ?: ownerPhone.ifBlank { id }
    OwnerType.PRIVATE -> {
        val phone = ownerPhone.filter { it.isDigit() || it == '+' }
        phone.ifBlank { "private_$id" }
    }
}

fun ChatParticipantType.canonical(): ChatParticipantType = when (this) {
    ChatParticipantType.PRIVATE -> ChatParticipantType.PRIVATE_OWNER
    else -> this
}

fun parseChatParticipantType(raw: String?): ChatParticipantType {
    val value = raw.orEmpty()
    return runCatching { ChatParticipantType.valueOf(value).canonical() }
        .getOrElse {
            when (value) {
                "PRIVATE" -> ChatParticipantType.PRIVATE_OWNER
                else -> ChatParticipantType.SHELTER
            }
        }
}

fun Animal.chatParticipantType(): ChatParticipantType = when (ownerType) {
    OwnerType.SHELTER -> ChatParticipantType.SHELTER
    OwnerType.PRIVATE -> ChatParticipantType.PRIVATE_OWNER
}

fun Animal.chatParticipantName(): String = when (ownerType) {
    OwnerType.SHELTER -> ownerName.ifBlank { location }
    OwnerType.PRIVATE -> ownerName.ifBlank { "Владелец" }
}

fun parseAgeMonths(age: String): Int {
    val trimmed = age.trim().lowercase()
    val years = Regex("""(\d+)\s*(год|года|лет)""").find(trimmed)?.groupValues?.get(1)?.toIntOrNull()
    val months = Regex("""(\d+)\s*(мес)""").find(trimmed)?.groupValues?.get(1)?.toIntOrNull()
    val number = trimmed.toIntOrNull()
    return when {
        years != null && months != null -> years * 12 + months
        years != null -> years * 12
        months != null -> months
        number != null -> if (number <= 30) number * 12 else number
        else -> 12
    }
}
