package com.adoptme.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.adoptme.app.data.local.dao.AnimalDao
import com.adoptme.app.data.local.dao.ApplicationDao
import com.adoptme.app.data.local.dao.ChatDao
import com.adoptme.app.data.local.dao.FavoriteDao
import com.adoptme.app.data.local.dao.NewsDao
import com.adoptme.app.data.local.dao.ShelterDao
import com.adoptme.app.data.local.dao.UserDao
import com.adoptme.app.data.local.entity.AnimalEntity
import com.adoptme.app.data.local.entity.ApplicationEntity
import com.adoptme.app.data.local.entity.ChatMessageEntity
import com.adoptme.app.data.local.entity.ChatThreadEntity
import com.adoptme.app.data.local.entity.FavoriteEntity
import com.adoptme.app.data.local.entity.NewsEntity
import com.adoptme.app.data.local.entity.ShelterEntity
import com.adoptme.app.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ShelterEntity::class,
        AnimalEntity::class,
        FavoriteEntity::class,
        ApplicationEntity::class,
        NewsEntity::class,
        ChatThreadEntity::class,
        ChatMessageEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AdoptMeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun shelterDao(): ShelterDao
    abstract fun animalDao(): AnimalDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun newsDao(): NewsDao
    abstract fun chatDao(): ChatDao

    companion object {
        fun build(context: Context): AdoptMeDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AdoptMeDatabase::class.java,
                "adoptme.db"
            ).addMigrations(MIGRATION_1_2).build()
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val photos = listOf(
                    "sharik" to "animal_sharik",
                    "tosha" to "animal_tosha",
                    "bella" to "animal_bella",
                    "barsik" to "animal_barsik",
                    "bobik" to "animal_bobik",
                    "druzhok" to "animal_druzhok",
                    "reks" to "animal_reks",
                    "max" to "animal_max"
                )
                photos.forEach { (id, key) ->
                    db.execSQL(
                        "UPDATE animals SET imageKey = ? WHERE id = ?",
                        arrayOf(key, id)
                    )
                }
            }
        }
    }
}
