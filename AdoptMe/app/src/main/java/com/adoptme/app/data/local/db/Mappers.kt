package com.adoptme.app.data.local.db

import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.ChatConversation
import com.adoptme.app.data.model.ChatMessage
import com.adoptme.app.data.model.ChatParticipantType
import com.adoptme.app.data.model.ChatSenderType
import com.adoptme.app.data.model.parseChatParticipantType
import com.adoptme.app.data.model.Donation
import com.adoptme.app.data.model.DonationStatus
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.model.OwnerType
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.data.model.UserProfile

fun UserEntity.toProfile(): UserProfile = UserProfile(
    id = id.toString(),
    name = name,
    email = email,
    phone = phone,
    city = city
)

fun AnimalEntity.toDomain(isFavorite: Boolean): Animal = Animal(
    id = id,
    name = name,
    type = runCatching { AnimalType.valueOf(type) }.getOrDefault(AnimalType.OTHER),
    breed = breed,
    age = ageLabel,
    ageMonths = ageMonths,
    gender = runCatching { Gender.valueOf(gender) }.getOrDefault(Gender.FEMALE),
    healthStatus = runCatching { HealthStatus.valueOf(healthStatus) }.getOrDefault(HealthStatus.HEALTHY),
    description = description,
    imageUrl = imageUrl,
    location = location,
    size = size,
    vaccinated = vaccinated,
    sterilized = sterilized,
    goodWithKids = goodWithKids,
    isFavorite = isFavorite,
    city = city,
    ownerType = runCatching { OwnerType.valueOf(ownerType) }.getOrDefault(OwnerType.SHELTER),
    ownerName = ownerName,
    ownerPhone = ownerPhone,
    shelterId = shelterId,
    isUserCreated = isUserCreated
)

fun ShelterEntity.toDomain(): Shelter = Shelter(
    id = id,
    name = name,
    address = address,
    phone = phone,
    latitude = latitude,
    longitude = longitude,
    openHours = workingHours,
    city = city,
    description = description,
    logoUri = logoUri,
    bankDetails = bankDetails
)

fun ChatConversationEntity.toDomain(): ChatConversation = ChatConversation(
    id = id,
    participantId = participantId,
    participantType = parseChatParticipantType(participantType),
    participantName = participantName,
    updatedAt = updatedAt,
    avatarUri = avatarUri,
    lastMessage = lastMessage,
    lastMessageAt = updatedAt,
    unreadCount = unreadCount,
    pinned = pinned
)

fun ChatMessageEntity.toDomain(): ChatMessage {
    val sender = runCatching { ChatSenderType.valueOf(senderType) }.getOrElse {
        if (isFromCurrentUser) ChatSenderType.USER else ChatSenderType.PARTICIPANT
    }
    return ChatMessage(
        id = id,
        conversationId = conversationId,
        text = text,
        isFromCurrentUser = sender == ChatSenderType.USER,
        sentAt = sentAt,
        senderType = sender,
        isRead = isRead
    )
}

fun DonationEntity.toDomain(): Donation = Donation(
    id = id,
    shelterId = shelterId,
    purpose = purpose,
    amountKzt = amountKzt,
    createdAt = createdAt,
    status = runCatching { DonationStatus.valueOf(status) }.getOrDefault(DonationStatus.TEST_SUCCESS)
)

fun ApplicationEntity.toDomain(): AdoptionApplication = AdoptionApplication(
    id = id,
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
    status = ApplicationStatus.valueOf(status),
    submittedAt = submittedAt
)
