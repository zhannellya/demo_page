package com.adoptme.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<UserEntity?>

    @Insert
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)
}

@Dao
interface AnimalDao {
    @Query("SELECT * FROM animals ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE shelterId = :shelterId ORDER BY name COLLATE NOCASE")
    fun observeByShelter(shelterId: String): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<AnimalEntity?>

    @Query("SELECT * FROM animals WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): AnimalEntity?

    @Query("SELECT COUNT(*) FROM animals")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(animals: List<AnimalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(animal: AnimalEntity)

    @Query("DELETE FROM animals WHERE id = :id AND isUserCreated = 1")
    suspend fun deleteUserCreated(id: String): Int
}

@Dao
interface ShelterDao {
    @Query("SELECT * FROM shelters ORDER BY city COLLATE NOCASE, name COLLATE NOCASE")
    fun observeAll(): Flow<List<ShelterEntity>>

    @Query("SELECT * FROM shelters WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<ShelterEntity?>

    @Query("SELECT * FROM shelters WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): ShelterEntity?

    @Query("SELECT COUNT(*) FROM shelters")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shelters: List<ShelterEntity>)
}

@Dao
interface FavoriteDao {
    @Query("SELECT animalId FROM favorites WHERE userId = :userId")
    fun observeIds(userId: Long): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND animalId = :animalId)")
    suspend fun isFavorite(userId: Long, animalId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND animalId = :animalId")
    suspend fun delete(userId: Long, animalId: String)
}

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications WHERE userId = :userId ORDER BY submittedAt DESC")
    fun observeForUser(userId: Long): Flow<List<ApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(application: ApplicationEntity)

    @Query(
        "SELECT COUNT(*) FROM applications WHERE userId = :userId AND animalId = :animalId AND status = 'PENDING'"
    )
    suspend fun pendingCount(userId: Long, animalId: String): Int

    @Query("SELECT * FROM applications WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): ApplicationEntity?

    @Query("DELETE FROM applications WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_conversations ORDER BY updatedAt DESC")
    fun observeConversations(): Flow<List<ChatConversationEntity>>

    @Query("SELECT * FROM chat_conversations WHERE id = :id LIMIT 1")
    fun observeConversation(id: String): Flow<ChatConversationEntity?>

    @Query("SELECT * FROM chat_conversations WHERE id = :id LIMIT 1")
    suspend fun findConversationById(id: String): ChatConversationEntity?

    @Query(
        "SELECT * FROM chat_conversations WHERE participantId = :participantId AND participantType = :participantType LIMIT 1"
    )
    suspend fun findConversation(participantId: String, participantType: String): ChatConversationEntity?

    @Query(
        "SELECT * FROM chat_conversations WHERE participantId = :participantId AND participantType IN (:types) LIMIT 1"
    )
    suspend fun findConversationAny(participantId: String, types: List<String>): ChatConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ChatConversationEntity)

    @Query(
        """
        UPDATE chat_conversations SET
            updatedAt = :updatedAt,
            participantName = :participantName,
            lastMessage = :lastMessage,
            unreadCount = :unreadCount,
            avatarUri = CASE WHEN :avatarUri = '' THEN avatarUri ELSE :avatarUri END
        WHERE id = :id
        """
    )
    suspend fun updatePreview(
        id: String,
        participantName: String,
        lastMessage: String,
        updatedAt: Long,
        unreadCount: Int,
        avatarUri: String = ""
    )

    @Query("UPDATE chat_conversations SET unreadCount = 0 WHERE id = :id")
    suspend fun clearUnread(id: String)

    @Query("UPDATE chat_messages SET isRead = 1 WHERE conversationId = :conversationId")
    suspend fun markMessagesRead(conversationId: String)

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY sentAt ASC")
    fun observeMessages(conversationId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT COUNT(*) FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun messageCount(conversationId: String): Int

    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)
}

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DonationEntity>>

    @Insert
    suspend fun insert(donation: DonationEntity)
}
