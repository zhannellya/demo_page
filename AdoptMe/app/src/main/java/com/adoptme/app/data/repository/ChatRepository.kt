package com.adoptme.app.data.repository

import com.adoptme.app.data.local.db.ChatConversationEntity
import com.adoptme.app.data.local.db.ChatDao
import com.adoptme.app.data.local.db.ChatMessageEntity
import com.adoptme.app.data.local.db.ShelterDao
import com.adoptme.app.data.local.db.toDomain
import com.adoptme.app.data.model.ChatConversation
import com.adoptme.app.data.model.ChatMessage
import com.adoptme.app.data.model.ChatParticipantType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Local chat stored in Room. Swap the DAO-backed implementation for a remote API later
 * without changing ViewModels or UI.
 */
class ChatRepository(
    private val chatDao: ChatDao,
    private val shelterDao: ShelterDao
) {
    fun observeConversations(): Flow<List<ChatConversation>> =
        chatDao.observeConversations().map { list -> list.map { it.toDomain() } }

    fun observeConversation(id: String): Flow<ChatConversation?> =
        chatDao.observeConversation(id).map { it?.toDomain() }

    fun observeMessages(conversationId: String): Flow<List<ChatMessage>> =
        chatDao.observeMessages(conversationId).map { list -> list.map { it.toDomain() } }

    suspend fun getOrCreateConversation(
        participantId: String,
        participantType: ChatParticipantType,
        participantName: String
    ): ChatConversation {
        val existing = chatDao.findConversation(participantId, participantType.name)
        val resolvedName = participantName.ifBlank {
            existing?.participantName.orEmpty().ifBlank { fallbackName(participantType) }
        }
        if (existing != null) {
            if (resolvedName.isNotBlank() && resolvedName != existing.participantName) {
                chatDao.touchConversation(existing.id, resolvedName, existing.updatedAt)
            }
            return existing.toDomain().copy(participantName = resolvedName)
        }
        val created = ChatConversationEntity(
            id = UUID.randomUUID().toString(),
            participantId = participantId,
            participantType = participantType.name,
            participantName = resolvedName,
            updatedAt = System.currentTimeMillis()
        )
        chatDao.insertConversation(created)
        return created.toDomain()
    }

    suspend fun resolveParticipantName(participantId: String, participantType: ChatParticipantType): String {
        if (participantType == ChatParticipantType.SHELTER) {
            return shelterDao.findById(participantId)?.name ?: "Приют"
        }
        return chatDao.findConversation(participantId, participantType.name)?.participantName ?: "Владелец"
    }

    suspend fun sendMessage(
        conversationId: String,
        text: String
    ): Result<Unit> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Введите сообщение."))
        }
        val now = System.currentTimeMillis()
        val conversation = chatDao.findConversationById(conversationId)
            ?: return Result.failure(IllegalStateException("Диалог не найден."))
        chatDao.insertMessage(
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                text = trimmed,
                isFromCurrentUser = true,
                sentAt = now
            )
        )
        chatDao.touchConversation(conversationId, conversation.participantName, now)
        return Result.success(Unit)
    }

    suspend fun addLocalReply(conversationId: String, participantType: ChatParticipantType) {
        val conversation = chatDao.findConversationById(conversationId) ?: return
        val reply = if (participantType == ChatParticipantType.SHELTER) {
            "Здравствуйте! ${conversation.participantName} получил(а) ваше сообщение. Мы ответим, как только сможем. Для срочных вопросов позвоните по указанному телефону."
        } else {
            "Здравствуйте! Спасибо, что написали. Я на связи и отвечу здесь."
        }
        val sentAt = System.currentTimeMillis()
        chatDao.insertMessage(
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                text = reply,
                isFromCurrentUser = false,
                sentAt = sentAt
            )
        )
        chatDao.touchConversation(conversationId, conversation.participantName, sentAt)
    }

    private fun fallbackName(type: ChatParticipantType): String =
        if (type == ChatParticipantType.SHELTER) "Приют" else "Владелец"
}
