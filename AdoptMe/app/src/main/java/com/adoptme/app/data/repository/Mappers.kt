package com.adoptme.app.data.repository

import com.adoptme.app.data.local.entity.AnimalEntity
import com.adoptme.app.data.local.entity.ApplicationEntity
import com.adoptme.app.data.local.entity.ChatMessageEntity
import com.adoptme.app.data.local.entity.ChatThreadEntity
import com.adoptme.app.data.local.entity.NewsEntity
import com.adoptme.app.data.local.entity.ShelterEntity
import com.adoptme.app.data.local.entity.UserEntity
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalStatus
import com.adoptme.app.data.model.ApplicationDraft
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.ChatMessage
import com.adoptme.app.data.model.ChatThread
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.MessageSender
import com.adoptme.app.data.model.NewsArticle
import com.adoptme.app.data.model.PetSize
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.data.model.Species
import com.adoptme.app.data.model.UserProfile
import com.adoptme.app.data.model.UserRole
import com.adoptme.app.data.model.ageGroupOf

fun ShelterEntity.toDomain() = Shelter(
    id = id,
    name = name,
    city = city,
    description = description,
    phone = phone,
    email = email,
    hours = hours,
    addressNote = addressNote,
    latitude = latitude,
    longitude = longitude
)

fun AnimalEntity.toDomain(shelterName: String, favorite: Boolean) = Animal(
    id = id,
    name = name,
    species = Species.valueOf(species),
    breed = breed,
    ageMonths = ageMonths,
    ageLabel = ageLabel,
    ageGroup = AgeGroup.valueOf(ageGroup),
    gender = Gender.valueOf(gender),
    size = PetSize.valueOf(size),
    temperament = temperament,
    story = story,
    shelterId = shelterId,
    shelterName = shelterName,
    city = city,
    imageKey = imageKey,
    status = AnimalStatus.valueOf(status),
    isFavorite = favorite
)

fun Animal.toEntity() = AnimalEntity(
    id = id,
    name = name.trim(),
    species = species.name,
    breed = breed.trim(),
    ageMonths = ageMonths,
    ageLabel = ageLabel.trim(),
    ageGroup = ageGroupOf(ageMonths).name,
    gender = gender.name,
    size = size.name,
    temperament = temperament.trim(),
    story = story.trim(),
    shelterId = shelterId,
    city = city.trim(),
    imageKey = imageKey.ifBlank { "animal_placeholder" },
    status = status.name
)

fun UserEntity.toDomain() = UserProfile(
    id = id,
    name = name,
    email = email,
    phone = phone,
    city = city,
    role = UserRole.valueOf(role)
)

fun ApplicationEntity.toDomain() = AdoptionApplication(
    id = id,
    number = number,
    userId = userId,
    animalId = animalId,
    animalName = animalName,
    applicantName = applicantName,
    email = email,
    phone = phone,
    address = address,
    experience = experience,
    reason = reason,
    status = ApplicationStatus.valueOf(status),
    submittedAt = submittedAt
)

fun ApplicationDraft.toEntity(id: String) = ApplicationEntity(
    id = id,
    number = "",
    userId = userId,
    animalId = animalId,
    animalName = animalName,
    applicantName = applicantName.trim(),
    email = email.trim(),
    phone = phone.trim(),
    address = address.trim(),
    experience = experience.trim(),
    reason = reason.trim(),
    status = ApplicationStatus.PENDING.name,
    submittedAt = System.currentTimeMillis()
)

fun NewsEntity.toDomain() = NewsArticle(id, title, category, summary, body, publishedAt)

fun ChatThreadEntity.toDomain() = ChatThread(id, userId, title, subtitle, shelterId)

fun ChatMessageEntity.toDomain() = ChatMessage(
    id = id,
    threadId = threadId,
    sender = MessageSender.valueOf(sender),
    text = text,
    sentAt = sentAt
)
