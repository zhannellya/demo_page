package com.adoptme.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adoptme.app.data.local.entity.AnimalEntity
import com.adoptme.app.data.local.entity.ApplicationEntity
import com.adoptme.app.data.local.entity.ConversationEntity
import com.adoptme.app.data.local.entity.FavoriteEntity
import com.adoptme.app.data.local.entity.MessageEntity
import com.adoptme.app.data.local.entity.NewsEntity
import com.adoptme.app.data.local.entity.NotificationEntity
import com.adoptme.app.data.local.entity.ShelterEntity
import com.adoptme.app.data.local.entity.UserEntity
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Availability
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<UserEntity?>

    @Query("SELECT id FROM users WHERE isAdmin = 1")
    suspend fun adminIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)
}

@Dao
interface ShelterDao {
    @Query("SELECT COUNT(*) FROM shelters")
    suspend fun count(): Int

    @Query("SELECT * FROM shelters ORDER BY city, name")
    fun observeAll(): Flow<List<ShelterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShelterEntity>)
}

@Dao
interface AnimalDao {
    @Query("SELECT COUNT(*) FROM animals")
    suspend fun count(): Int

    @Query("SELECT * FROM animals ORDER BY name")
    fun observeAll(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE id = :id LIMIT 1")
    suspend fun get(id: String): AnimalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(animal: AnimalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AnimalEntity>)

    @Update
    suspend fun update(animal: AnimalEntity)

    @Query("UPDATE animals SET availability = :availability WHERE id = :id")
    suspend fun updateAvailability(id: String, availability: Availability)
}

@Dao
interface ApplicationDao {
    @Query(
        "SELECT COUNT(*) FROM applications WHERE userId = :userId AND animalId = :animalId AND status = :status"
    )
    suspend fun countStatus(userId: String, animalId: String, status: ApplicationStatus): Int

    @Query("SELECT MAX(sequence) FROM applications")
    suspend fun maxSequence(): Long?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(application: ApplicationEntity)

    @Update
    suspend fun update(application: ApplicationEntity)

    @Query("SELECT * FROM applications WHERE id = :id LIMIT 1")
    suspend fun get(id: String): ApplicationEntity?

    @Query("SELECT * FROM applications ORDER BY submittedAt DESC")
    fun observeAll(): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE userId = :userId ORDER BY submittedAt DESC")
    fun observeForUser(userId: String): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications")
    suspend fun all(): List<ApplicationEntity>
}

@Dao
interface FavoriteDao {
    @Query("SELECT animalId FROM favorites WHERE userId = :userId")
    fun observeIds(userId: String): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND animalId = :animalId)")
    suspend fun exists(userId: String, animalId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND animalId = :animalId")
    suspend fun delete(userId: String, animalId: String)
}

@Dao
interface ChatDao {
    @Query("SELECT COUNT(*) FROM conversations")
    suspend fun conversationCount(): Int

    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun observeConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY sentAt ASC")
    fun observeMessages(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE conversations SET lastMessage = :text, updatedAt = :updatedAt WHERE id = :id")
    suspend fun touch(id: String, text: String, updatedAt: Long)
}

@Dao
interface NewsDao {
    @Query("SELECT COUNT(*) FROM news")
    suspend fun count(): Int

    @Query("SELECT * FROM news")
    fun observeAll(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<NewsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<NewsEntity>)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeForUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: String)
}
