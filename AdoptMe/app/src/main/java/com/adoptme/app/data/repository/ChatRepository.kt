package com.adoptme.app.data.repository

import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.dao.ChatDao
import com.adoptme.app.data.local.entity.ChatMessageEntity
import com.adoptme.app.data.local.entity.ChatThreadEntity
import com.adoptme.app.data.model.ChatMessage
import com.adoptme.app.data.model.ChatThread
import com.adoptme.app.data.model.MessageSender
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ChatRepository(
    private val chatDao: ChatDao,
    private val sessionStore: SessionStore
) {
    val threads: Flow<List<ChatThread>> = sessionStore.session.flatMapLatest { session ->
        if (session.userId.isBlank()) flowOf(emptyList())
        else chatDao.observeThreads(session.userId).map { list -> list.map { it.toDomain() } }
    }

    fun messages(threadId: String): Flow<List<ChatMessage>> =
        chatDao.observeMessages(threadId).map { list -> list.map { it.toDomain() } }

    suspend fun ensureInbox() {
        val userId = sessionStore.snapshot().userId
        if (userId.isBlank()) return
        val threadId = "support-$userId"
        if (chatDao.findThread(threadId) != null) return
        chatDao.insertThread(
            ChatThreadEntity(
                id = threadId,
                userId = userId,
                title = "Поддержка ADOPTME",
                subtitle = "Локальный демонстрационный диалог",
                shelterId = null
            )
        )
        chatDao.insertMessage(
            ChatMessageEntity(
                id = "welcome-$threadId",
                threadId = threadId,
                sender = MessageSender.SHELTER.name,
                text = "Это локальный чат. Сообщения сохраняются на устройстве и никуда не отправляются, пока не подключён сервер.",
                sentAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun openShelterChat(shelterId: String, shelterName: String): String {
        val userId = sessionStore.snapshot().userId
        val threadId = "shelter-$shelterId-$userId"
        if (chatDao.findThread(threadId) == null) {
            chatDao.insertThread(
                ChatThreadEntity(
                    id = threadId,
                    userId = userId,
                    title = shelterName,
                    subtitle = "Демонстрационный чат приюта",
                    shelterId = shelterId
                )
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    id = "welcome-$threadId",
                    threadId = threadId,
                    sender = MessageSender.SHELTER.name,
                    text = "Здравствуйте! Это демонстрационный ответ приюта «$shelterName». Напишите, когда вам удобно познакомиться с животным.",
                    sentAt = System.currentTimeMillis()
                )
            )
        }
        return threadId
    }

    suspend fun send(threadId: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val now = System.currentTimeMillis()
        chatDao.insertMessage(
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                threadId = threadId,
                sender = MessageSender.USER.name,
                text = trimmed,
                sentAt = now
            )
        )
        chatDao.insertMessage(
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                threadId = threadId,
                sender = MessageSender.SHELTER.name,
                text = "Сообщение сохранено на устройстве. В демонстрационном режиме сотрудник приюта не подключён.",
                sentAt = now + 1
            )
        )
    }
}
