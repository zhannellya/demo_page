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
    val goodWithKids: Boolean
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
