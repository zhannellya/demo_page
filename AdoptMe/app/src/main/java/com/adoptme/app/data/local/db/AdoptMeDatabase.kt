package com.adoptme.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        AnimalEntity::class,
        FavoriteEntity::class,
        ApplicationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AdoptMeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun animalDao(): AnimalDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun applicationDao(): ApplicationDao

    companion object {
        @Volatile
        private var instance: AdoptMeDatabase? = null

        fun create(context: Context): AdoptMeDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AdoptMeDatabase::class.java,
                    "adoptme.db"
                ).build().also { instance = it }
            }
        }
    }
}
