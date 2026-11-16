package com.adoptme.app.data.repository

import com.adoptme.app.data.local.dao.ChatDao
import com.adoptme.app.data.local.entity.MessageEntity
import com.adoptme.app.data.local.toModel
import com.adoptme.app.data.model.ChatConversation
import com.adoptme.app.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ChatRepository(private val chatDao: ChatDao) {
    fun observeConversations(): Flow<List<ChatConversation>> =
        chatDao.observeConversations().map { rows -> rows.map { it.toModel() } }

    fun observeMessages(conversationId: String): Flow<List<ChatMessage>> =
        chatDao.observeMessages(conversationId).map { rows -> rows.map { it.toModel() } }

    suspend fun send(conversationId: String, text: String): Result<Unit> {
        val clean = text.trim()
        if (clean.isEmpty()) {
            return Result.failure(IllegalArgumentException("Введите сообщение."))
        }
        if (clean.length > 1000) {
            return Result.failure(IllegalArgumentException("Сообщение слишком длинное."))
        }
        return try {
            val now = System.currentTimeMillis()
            chatDao.insertMessage(
                MessageEntity(
                    id = UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    text = clean,
                    fromUser = true,
                    sentAt = now
                )
            )
            val reply = "Сообщение сохранено на устройстве. Это автоматический локальный ответ: сотрудник приюта не подключён."
            chatDao.insertMessage(
                MessageEntity(
                    id = UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    text = reply,
                    fromUser = false,
                    sentAt = now + 1
                )
            )
            chatDao.touch(conversationId, reply, now + 1)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(IllegalStateException("Не удалось сохранить сообщение."))
        }
    }
}
