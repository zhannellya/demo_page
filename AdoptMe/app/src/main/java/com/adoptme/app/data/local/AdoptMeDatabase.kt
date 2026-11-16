package com.adoptme.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.adoptme.app.data.local.dao.AnimalDao
import com.adoptme.app.data.local.dao.ApplicationDao
import com.adoptme.app.data.local.dao.ChatDao
import com.adoptme.app.data.local.dao.FavoriteDao
import com.adoptme.app.data.local.dao.NewsDao
import com.adoptme.app.data.local.dao.NotificationDao
import com.adoptme.app.data.local.dao.ShelterDao
import com.adoptme.app.data.local.dao.UserDao
import com.adoptme.app.data.local.entity.AnimalEntity
import com.adoptme.app.data.local.entity.ApplicationEntity
import com.adoptme.app.data.local.entity.ConversationEntity
import com.adoptme.app.data.local.entity.FavoriteEntity
import com.adoptme.app.data.local.entity.MessageEntity
import com.adoptme.app.data.local.entity.NewsEntity
import com.adoptme.app.data.local.entity.NotificationEntity
import com.adoptme.app.data.local.entity.ShelterEntity
import com.adoptme.app.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ShelterEntity::class,
        AnimalEntity::class,
        ApplicationEntity::class,
        FavoriteEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        NewsEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AdoptMeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun shelterDao(): ShelterDao
    abstract fun animalDao(): AnimalDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun chatDao(): ChatDao
    abstract fun newsDao(): NewsDao
    abstract fun notificationDao(): NotificationDao

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
