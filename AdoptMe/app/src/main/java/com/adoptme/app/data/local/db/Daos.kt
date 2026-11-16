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

    @Query("SELECT COUNT(*) FROM animals")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(animals: List<AnimalEntity>)
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
}
