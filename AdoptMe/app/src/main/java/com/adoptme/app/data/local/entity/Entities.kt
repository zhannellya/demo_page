package com.adoptme.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.adoptme.app.data.model.AnimalSize
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Availability
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.model.NewsCategory

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val city: String,
    val passwordSalt: String,
    val passwordHash: String,
    val isAdmin: Boolean,
    val createdAt: Long
)

@Entity(tableName = "shelters")
data class ShelterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val description: String,
    val contactNote: String,
    val hours: String,
    val isDemonstration: Boolean
)

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey val id: String,
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
    val vaccinated: Boolean,
    val sterilized: Boolean,
    val goodWithKids: Boolean
)

@Entity(
    tableName = "applications",
    indices = [
        Index("userId"),
        Index(value = ["number"], unique = true),
        Index(value = ["userId", "animalId", "status"])
    ]
)
data class ApplicationEntity(
    @PrimaryKey val id: String,
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

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "animalId"]
)
data class FavoriteEntity(
    val userId: String,
    val animalId: String
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val lastMessage: String,
    val updatedAt: Long
)

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val text: String,
    val fromUser: Boolean,
    val sentAt: Long
)

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val body: String,
    val category: NewsCategory,
    val publishedLabel: String
)

@Entity(
    tableName = "notifications",
    indices = [Index("userId")]
)
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val createdAt: Long,
    val isRead: Boolean,
    val applicationId: String?
)
