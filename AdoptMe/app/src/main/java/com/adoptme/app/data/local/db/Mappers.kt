package com.adoptme.app.data.local.db

import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
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
    type = AnimalType.valueOf(type),
    breed = breed,
    age = ageLabel,
    ageMonths = ageMonths,
    gender = Gender.valueOf(gender),
    healthStatus = HealthStatus.valueOf(healthStatus),
    description = description,
    imageUrl = imageUrl,
    location = location,
    size = size,
    vaccinated = vaccinated,
    sterilized = sterilized,
    goodWithKids = goodWithKids,
    isFavorite = isFavorite
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
