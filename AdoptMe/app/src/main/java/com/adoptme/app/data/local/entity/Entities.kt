package com.adoptme.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val phone: String,
    val city: String,
    val role: String,
    val createdAt: Long
)

@Entity(tableName = "shelters")
data class ShelterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val description: String,
    val phone: String,
    val email: String,
    val hours: String,
    val addressNote: String,
    val latitude: Double?,
    val longitude: Double?
)

@Entity(tableName = "animals", indices = [Index("shelterId"), Index("city")])
data class AnimalEntity(
    @PrimaryKey val id: String,
    val name: String,
    val species: String,
    val breed: String,
    val ageMonths: Int,
    val ageLabel: String,
    val ageGroup: String,
    val gender: String,
    val size: String,
    val temperament: String,
    val story: String,
    val shelterId: String,
    val city: String,
    val imageKey: String,
    val status: String
)

@Entity(tableName = "favorites", primaryKeys = ["userId", "animalId"])
data class FavoriteEntity(
    val userId: String,
    val animalId: String
)

@Entity(
    tableName = "applications",
    indices = [
        Index(value = ["number"], unique = true),
        Index("userId"),
        Index("animalId")
    ]
)
data class ApplicationEntity(
    @PrimaryKey val id: String,
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
    val status: String,
    val submittedAt: Long
)

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val body: String,
    val publishedAt: Long
)

@Entity(tableName = "chat_threads", indices = [Index("userId")])
data class ChatThreadEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val subtitle: String,
    val shelterId: String?
)

@Entity(tableName = "chat_messages", indices = [Index("threadId")])
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val threadId: String,
    val sender: String,
    val text: String,
    val sentAt: Long
)
