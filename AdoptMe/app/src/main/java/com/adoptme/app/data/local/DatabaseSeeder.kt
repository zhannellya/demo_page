package com.adoptme.app.data.local

import androidx.room.withTransaction
import com.adoptme.app.data.local.entity.ConversationEntity
import com.adoptme.app.data.local.entity.MessageEntity
import com.adoptme.app.data.local.entity.UserEntity
import com.adoptme.app.utils.PasswordHasher
import java.util.UUID

class DatabaseSeeder(private val database: AdoptMeDatabase) {

    suspend fun seed() {
        database.withTransaction {
            val users = database.userDao()
            if (users.findByEmail(DemoAccounts.ADMIN_EMAIL) == null) {
                val salt = PasswordHasher.generateSalt()
                users.insert(
                    UserEntity(
                        id = "user-admin",
                        name = DemoAccounts.ADMIN_NAME,
                        email = DemoAccounts.ADMIN_EMAIL,
                        phone = "",
                        city = "",
                        passwordSalt = salt,
                        passwordHash = PasswordHasher.hash(DemoAccounts.ADMIN_PASSWORD, salt),
                        isAdmin = true,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }

            val shelters = database.shelterDao()
            val animals = database.animalDao()
            if (animals.count() == 0) {
                shelters.insertAll(SeedData.shelters.map { it.toEntity() })
                animals.insertAll(SeedData.animals.map { it.toEntity() })
            }

            val news = database.newsDao()
            if (news.count() == 0) {
                news.insertAll(SeedData.news.map { it.toEntity() })
            }

            val chat = database.chatDao()
            if (chat.conversationCount() == 0) {
                val createdAt = System.currentTimeMillis()
                val welcome =
                    "Здравствуйте! Это локальный демонстрационный чат. Сообщения остаются на устройстве и никуда не отправляются, пока не подключён сервер."
                chat.insertConversation(
                    ConversationEntity(
                        id = "chat-shelter",
                        title = "Приют «Тёплый дом»",
                        subtitle = "Локальная демонстрация",
                        lastMessage = welcome,
                        updatedAt = createdAt
                    )
                )
                chat.insertMessage(
                    MessageEntity(
                        id = UUID.randomUUID().toString(),
                        conversationId = "chat-shelter",
                        text = welcome,
                        fromUser = false,
                        sentAt = createdAt
                    )
                )
            }
        }
    }
}
