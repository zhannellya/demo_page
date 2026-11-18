package com.adoptme.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserEntity::class,
        AnimalEntity::class,
        FavoriteEntity::class,
        ApplicationEntity::class,
        ShelterEntity::class,
        ChatConversationEntity::class,
        ChatMessageEntity::class,
        DonationEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AdoptMeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun animalDao(): AnimalDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun shelterDao(): ShelterDao
    abstract fun chatDao(): ChatDao
    abstract fun donationDao(): DonationDao

    companion object {
        @Volatile
        private var instance: AdoptMeDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE animals ADD COLUMN city TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE animals ADD COLUMN ownerType TEXT NOT NULL DEFAULT 'SHELTER'")
                db.execSQL("ALTER TABLE animals ADD COLUMN ownerName TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE animals ADD COLUMN ownerPhone TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE animals ADD COLUMN shelterId TEXT")
                db.execSQL("ALTER TABLE animals ADD COLUMN isUserCreated INTEGER NOT NULL DEFAULT 0")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS shelters (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        city TEXT NOT NULL,
                        address TEXT NOT NULL,
                        phone TEXT NOT NULL,
                        workingHours TEXT NOT NULL,
                        description TEXT NOT NULL,
                        logoUri TEXT NOT NULL,
                        bankDetails TEXT NOT NULL,
                        latitude REAL NOT NULL,
                        longitude REAL NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS chat_conversations (
                        id TEXT NOT NULL PRIMARY KEY,
                        participantId TEXT NOT NULL,
                        participantType TEXT NOT NULL,
                        participantName TEXT NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_chat_conversations_participantId_participantType ON chat_conversations(participantId, participantType)"
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS chat_messages (
                        id TEXT NOT NULL PRIMARY KEY,
                        conversationId TEXT NOT NULL,
                        text TEXT NOT NULL,
                        isFromCurrentUser INTEGER NOT NULL,
                        sentAt INTEGER NOT NULL,
                        FOREIGN KEY(conversationId) REFERENCES chat_conversations(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_chat_messages_conversationId ON chat_messages(conversationId)"
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS donations (
                        id TEXT NOT NULL PRIMARY KEY,
                        shelterId TEXT NOT NULL,
                        purpose TEXT NOT NULL,
                        amountKzt INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        status TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE chat_conversations ADD COLUMN avatarUri TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE chat_conversations ADD COLUMN lastMessage TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE chat_conversations ADD COLUMN unreadCount INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE chat_conversations ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN senderType TEXT NOT NULL DEFAULT 'PARTICIPANT'")
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN isRead INTEGER NOT NULL DEFAULT 1")
                db.execSQL(
                    "UPDATE chat_messages SET senderType = CASE WHEN isFromCurrentUser = 1 THEN 'USER' ELSE 'PARTICIPANT' END"
                )
                db.execSQL(
                    "UPDATE chat_conversations SET participantType = 'PRIVATE_OWNER' WHERE participantType = 'PRIVATE'"
                )
            }
        }

        fun create(context: Context): AdoptMeDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AdoptMeDatabase::class.java,
                    "adoptme.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
