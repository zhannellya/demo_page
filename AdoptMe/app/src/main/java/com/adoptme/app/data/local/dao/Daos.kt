package com.adoptme.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.adoptme.app.data.local.entity.AnimalEntity
import com.adoptme.app.data.local.entity.ApplicationEntity
import com.adoptme.app.data.local.entity.ChatMessageEntity
import com.adoptme.app.data.local.entity.ChatThreadEntity
import com.adoptme.app.data.local.entity.FavoriteEntity
import com.adoptme.app.data.local.entity.NewsEntity
import com.adoptme.app.data.local.entity.ShelterEntity
import com.adoptme.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)
}

@Dao
interface ShelterDao {
    @Query("SELECT COUNT(*) FROM shelters")
    suspend fun count(): Int

    @Query("SELECT * FROM shelters ORDER BY city, name")
    fun observeAll(): Flow<List<ShelterEntity>>

    @Query("SELECT * FROM shelters ORDER BY city, name")
    suspend fun all(): List<ShelterEntity>

    @Query("SELECT * FROM shelters WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<ShelterEntity?>

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
    fun observeById(id: String): Flow<AnimalEntity?>

    @Query("SELECT * FROM animals WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): AnimalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(animal: AnimalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AnimalEntity>)

    @Query("UPDATE animals SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)
}

@Dao
interface FavoriteDao {
    @Query("SELECT animalId FROM favorites WHERE userId = :userId")
    fun observeIds(userId: String): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId AND animalId = :animalId")
    suspend fun count(userId: String, animalId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND animalId = :animalId")
    suspend fun delete(userId: String, animalId: String)
}

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications WHERE userId = :userId ORDER BY submittedAt DESC")
    fun observeForUser(userId: String): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications ORDER BY submittedAt DESC")
    fun observeAll(): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<ApplicationEntity?>

    @Query("SELECT * FROM applications WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): ApplicationEntity?

    @Query(
        "SELECT * FROM applications WHERE userId = :userId AND animalId = :animalId AND status = 'PENDING' LIMIT 1"
    )
    suspend fun findPending(userId: String, animalId: String): ApplicationEntity?

    @Query("SELECT COUNT(*) FROM applications")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: ApplicationEntity)

    @Query("UPDATE applications SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Transaction
    suspend fun insertIfNoPending(entity: ApplicationEntity): ApplicationEntity? {
        if (findPending(entity.userId, entity.animalId) != null) return null
        val number = "ADM-%05d".format(count() + 1)
        val saved = entity.copy(number = number)
        insert(saved)
        return saved
    }
}

@Dao
interface NewsDao {
    @Query("SELECT COUNT(*) FROM news")
    suspend fun count(): Int

    @Query("SELECT * FROM news ORDER BY publishedAt DESC")
    fun observeAll(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<NewsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<NewsEntity>)
}

@Dao
interface ChatDao {
    @Query("SELECT COUNT(*) FROM chat_threads WHERE userId = :userId")
    suspend fun countForUser(userId: String): Int

    @Query("SELECT * FROM chat_threads WHERE userId = :userId ORDER BY title")
    fun observeThreads(userId: String): Flow<List<ChatThreadEntity>>

    @Query("SELECT * FROM chat_threads WHERE id = :id LIMIT 1")
    suspend fun findThread(id: String): ChatThreadEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertThread(thread: ChatThreadEntity)

    @Query("SELECT * FROM chat_messages WHERE threadId = :threadId ORDER BY sentAt ASC")
    fun observeMessages(threadId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMessage(message: ChatMessageEntity)
}
