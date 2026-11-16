package com.adoptme.app.data.local

import com.adoptme.app.data.local.entity.AnimalEntity
import com.adoptme.app.data.local.entity.ApplicationEntity
import com.adoptme.app.data.local.entity.ConversationEntity
import com.adoptme.app.data.local.entity.MessageEntity
import com.adoptme.app.data.local.entity.NewsEntity
import com.adoptme.app.data.local.entity.NotificationEntity
import com.adoptme.app.data.local.entity.ShelterEntity
import com.adoptme.app.data.local.entity.UserEntity
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AppNotification
import com.adoptme.app.data.model.ChatConversation
import com.adoptme.app.data.model.ChatMessage
import com.adoptme.app.data.model.NewsArticle
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.data.model.UserProfile

fun UserEntity.toProfile(): UserProfile = UserProfile(
    id = id,
    name = name,
    email = email,
    phone = phone,
    city = city,
    isAdmin = isAdmin
)

fun ShelterEntity.toModel(): Shelter = Shelter(
    id = id,
    name = name,
    city = city,
    description = description,
    contactNote = contactNote,
    hours = hours,
    isDemonstration = isDemonstration
)

fun Shelter.toEntity(): ShelterEntity = ShelterEntity(
    id = id,
    name = name,
    city = city,
    description = description,
    contactNote = contactNote,
    hours = hours,
    isDemonstration = isDemonstration
)

fun AnimalEntity.toModel(isFavorite: Boolean): Animal = Animal(
    id = id,
    name = name,
    type = type,
    breed = breed,
    ageLabel = ageLabel,
    ageMonths = ageMonths,
    gender = gender,
    size = size,
    healthStatus = healthStatus,
    availability = availability,
    personality = personality,
    description = description,
    story = story,
    shelterId = shelterId,
    shelterName = shelterName,
    city = city,
    imageUrl = imageUrl,
    vaccinated = vaccinated,
    sterilized = sterilized,
    goodWithKids = goodWithKids,
    isFavorite = isFavorite
)

fun Animal.toEntity(): AnimalEntity = AnimalEntity(
    id = id,
    name = name,
    type = type,
    breed = breed,
    ageLabel = ageLabel,
    ageMonths = ageMonths,
    gender = gender,
    size = size,
    healthStatus = healthStatus,
    availability = availability,
    personality = personality,
    description = description,
    story = story,
    shelterId = shelterId,
    shelterName = shelterName,
    city = city,
    imageUrl = imageUrl,
    vaccinated = vaccinated,
    sterilized = sterilized,
    goodWithKids = goodWithKids
)

fun ApplicationEntity.toModel(): AdoptionApplication = AdoptionApplication(
    id = id,
    number = number,
    sequence = sequence,
    animalId = animalId,
    animalName = animalName,
    userId = userId,
    applicantName = applicantName,
    email = email,
    phone = phone,
    address = address,
    experience = experience,
    reason = reason,
    status = status,
    submittedAt = submittedAt
)

fun AdoptionApplication.toEntity(): ApplicationEntity = ApplicationEntity(
    id = id,
    number = number,
    sequence = sequence,
    animalId = animalId,
    animalName = animalName,
    userId = userId,
    applicantName = applicantName,
    email = email,
    phone = phone,
    address = address,
    experience = experience,
    reason = reason,
    status = status,
    submittedAt = submittedAt
)

fun ConversationEntity.toModel(): ChatConversation = ChatConversation(
    id = id,
    title = title,
    subtitle = subtitle,
    lastMessage = lastMessage,
    updatedAt = updatedAt
)

fun MessageEntity.toModel(): ChatMessage = ChatMessage(
    id = id,
    conversationId = conversationId,
    text = text,
    fromUser = fromUser,
    sentAt = sentAt
)

fun NewsEntity.toModel(): NewsArticle = NewsArticle(
    id = id,
    title = title,
    summary = summary,
    body = body,
    category = category,
    publishedLabel = publishedLabel
)

fun NewsArticle.toEntity(): NewsEntity = NewsEntity(
    id = id,
    title = title,
    summary = summary,
    body = body,
    category = category,
    publishedLabel = publishedLabel
)

fun NotificationEntity.toModel(): AppNotification = AppNotification(
    id = id,
    userId = userId,
    title = title,
    message = message,
    createdAt = createdAt,
    isRead = isRead,
    applicationId = applicationId
)
