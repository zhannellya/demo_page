package com.adoptme.app.data.local.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val city: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAt: Long
)

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val breed: String,
    val ageMonths: Int,
    val ageLabel: String,
    val gender: String,
    val healthStatus: String,
    val description: String,
    val imageUrl: String,
    val location: String,
    val size: String,
    val vaccinated: Boolean,
    val sterilized: Boolean,
    val goodWithKids: Boolean,
    val city: String = "",
    val ownerType: String = "SHELTER",
    val ownerName: String = "",
    val ownerPhone: String = "",
    val shelterId: String? = null,
    val isUserCreated: Boolean = false
)

@Entity(tableName = "shelters")
data class ShelterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val address: String,
    val phone: String,
    val workingHours: String,
    val description: String,
    val logoUri: String,
    val bankDetails: String,
    val latitude: Double,
    val longitude: Double
)

@Entity(
    tableName = "chat_conversations",
    indices = [Index(value = ["participantId", "participantType"], unique = true)]
)
data class ChatConversationEntity(
    @PrimaryKey val id: String,
    val participantId: String,
    val participantType: String,
    val participantName: String,
    val updatedAt: Long,
    val avatarUri: String = "",
    val lastMessage: String = "",
    val unreadCount: Int = 0,
    val pinned: Boolean = false
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val text: String,
    val isFromCurrentUser: Boolean,
    val sentAt: Long,
    val senderType: String = if (isFromCurrentUser) "USER" else "PARTICIPANT",
    val isRead: Boolean = true
)

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey val id: String,
    val shelterId: String,
    val purpose: String,
    val amountKzt: Int,
    val createdAt: Long,
    val status: String
)

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "animalId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AnimalEntity::class,
            parentColumns = ["id"],
            childColumns = ["animalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("animalId")]
)
data class FavoriteEntity(
    val userId: Long,
    val animalId: String
)

@Entity(
    tableName = "applications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AnimalEntity::class,
            parentColumns = ["id"],
            childColumns = ["animalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("animalId")]
)
data class ApplicationEntity(
    @PrimaryKey val id: String,
    val userId: Long,
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
    val status: String,
    val submittedAt: Long
)
