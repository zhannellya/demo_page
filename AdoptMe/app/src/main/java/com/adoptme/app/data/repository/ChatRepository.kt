package com.adoptme.app.data.repository

import com.adoptme.app.data.ai.PetAiService
import com.adoptme.app.data.local.db.AnimalDao
import com.adoptme.app.data.local.db.ChatConversationEntity
import com.adoptme.app.data.local.db.ChatDao
import com.adoptme.app.data.local.db.ChatMessageEntity
import com.adoptme.app.data.local.db.ShelterDao
import com.adoptme.app.data.local.db.toDomain
import com.adoptme.app.data.model.AdoptMeAiChat
import com.adoptme.app.data.model.ChatContact
import com.adoptme.app.data.model.ChatConversation
import com.adoptme.app.data.model.ChatMessage
import com.adoptme.app.data.model.ChatParticipantType
import com.adoptme.app.data.model.ChatSenderType
import com.adoptme.app.data.model.OwnerType
import com.adoptme.app.data.model.canonical
import com.adoptme.app.data.model.chatParticipantId
import com.adoptme.app.data.model.chatParticipantName
import com.adoptme.app.data.model.chatParticipantType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Local chat stored in Room. Swap DAO / PetAiService for a remote API later
 * without changing ViewModels or UI.
 */
class ChatRepository(
    private val chatDao: ChatDao,
    private val shelterDao: ShelterDao,
    private val animalDao: AnimalDao,
    private val petAiService: PetAiService
) {
    fun observeConversations(): Flow<List<ChatConversation>> = chatDao.observeConversations().map { list ->
        list.map { it.toDomain() }
            .sortedWith(
                compareByDescending<ChatConversation> { it.pinned || it.participantType == ChatParticipantType.AI }
                    .thenByDescending { it.lastMessageAt }
            )
    }

    fun observeConversation(id: String): Flow<ChatConversation?> =
        chatDao.observeConversation(id).map { it?.toDomain() }

    fun observeMessages(conversationId: String): Flow<List<ChatMessage>> =
        chatDao.observeMessages(conversationId).map { list -> list.map { it.toDomain() } }

    fun observeNewChatContacts(): Flow<List<ChatContact>> = combine(
        shelterDao.observeAll(),
        animalDao.observeAll()
    ) { shelters, animals ->
        val shelterContacts = shelters.map { shelter ->
            ChatContact(
                participantId = shelter.id,
                participantType = ChatParticipantType.SHELTER,
                name = shelter.name,
                subtitle = listOf(shelter.city, shelter.phone).filter { it.isNotBlank() }.joinToString(" · "),
                avatarUri = shelter.logoUri
            )
        }
        val ownerContacts = animals
            .map { it.toDomain(isFavorite = false) }
            .filter { it.ownerType == OwnerType.PRIVATE }
            .distinctBy { it.chatParticipantId() }
            .map { animal ->
                ChatContact(
                    participantId = animal.chatParticipantId(),
                    participantType = ChatParticipantType.PRIVATE_OWNER,
                    name = animal.chatParticipantName(),
                    subtitle = listOf(animal.city, animal.ownerPhone).filter { it.isNotBlank() }.joinToString(" · "),
                    avatarUri = animal.imageUrl
                )
            }
        shelterContacts + ownerContacts
    }

    suspend fun ensureAiConversation() {
        val existing = findExisting(AdoptMeAiChat.PARTICIPANT_ID, ChatParticipantType.AI)
        val id = existing?.id ?: UUID.randomUUID().toString()
        if (existing == null) {
            chatDao.insertConversation(
                ChatConversationEntity(
                    id = id,
                    participantId = AdoptMeAiChat.PARTICIPANT_ID,
                    participantType = ChatParticipantType.AI.name,
                    participantName = AdoptMeAiChat.NAME,
                    updatedAt = System.currentTimeMillis(),
                    avatarUri = "",
                    lastMessage = AdoptMeAiChat.SUBTITLE,
                    unreadCount = 0,
                    pinned = true
                )
            )
        } else if (!existing.pinned) {
            chatDao.insertConversation(existing.copy(pinned = true, participantName = AdoptMeAiChat.NAME))
        }
        if (chatDao.messageCount(id) == 0) {
            insertMessage(
                conversationId = id,
                text = AdoptMeAiChat.GREETING,
                senderType = ChatSenderType.AI,
                incrementUnread = false
            )
        }
    }

    suspend fun getOrCreateConversation(
        participantId: String,
        participantType: ChatParticipantType,
        participantName: String,
        avatarUri: String = ""
    ): ChatConversation {
        val type = participantType.canonical()
        val existing = findExisting(participantId, type)
        val resolvedName = participantName.ifBlank {
            existing?.participantName.orEmpty().ifBlank { fallbackName(type) }
        }
        if (existing != null) {
            if (resolvedName != existing.participantName || (avatarUri.isNotBlank() && avatarUri != existing.avatarUri)) {
                chatDao.updatePreview(
                    id = existing.id,
                    participantName = resolvedName,
                    lastMessage = existing.lastMessage,
                    updatedAt = existing.updatedAt,
                    unreadCount = existing.unreadCount,
                    avatarUri = avatarUri
                )
            }
            return existing.toDomain().copy(participantName = resolvedName, avatarUri = avatarUri.ifBlank { existing.avatarUri })
        }
        val created = ChatConversationEntity(
            id = UUID.randomUUID().toString(),
            participantId = participantId,
            participantType = type.name,
            participantName = resolvedName,
            updatedAt = System.currentTimeMillis(),
            avatarUri = avatarUri,
            lastMessage = "",
            unreadCount = 0,
            pinned = type == ChatParticipantType.AI
        )
        chatDao.insertConversation(created)
        return created.toDomain()
    }

    suspend fun resolveParticipantName(participantId: String, participantType: ChatParticipantType): String {
        val type = participantType.canonical()
        return when (type) {
            ChatParticipantType.AI -> AdoptMeAiChat.NAME
            ChatParticipantType.SHELTER -> shelterDao.findById(participantId)?.name ?: "Приют"
            ChatParticipantType.PRIVATE, ChatParticipantType.PRIVATE_OWNER ->
                findExisting(participantId, type)?.participantName ?: "Владелец"
        }
    }

    suspend fun resolveAvatar(participantId: String, participantType: ChatParticipantType): String {
        val type = participantType.canonical()
        findExisting(participantId, type)?.avatarUri?.takeIf { it.isNotBlank() }?.let { return it }
        return when (type) {
            ChatParticipantType.SHELTER -> shelterDao.findById(participantId)?.logoUri.orEmpty()
            else -> ""
        }
    }

    suspend fun markRead(conversationId: String) {
        chatDao.markMessagesRead(conversationId)
        chatDao.clearUnread(conversationId)
    }

    suspend fun sendMessage(conversationId: String, text: String): Result<Unit> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Введите сообщение."))
        }
        insertMessage(conversationId, trimmed, ChatSenderType.USER, incrementUnread = false)
        return Result.success(Unit)
    }

    suspend fun addLocalReply(conversationId: String, participantType: ChatParticipantType) {
        if (participantType.canonical() == ChatParticipantType.AI) return
        val conversation = chatDao.findConversationById(conversationId) ?: return
        val reply = if (participantType.canonical() == ChatParticipantType.SHELTER) {
            "Здравствуйте! ${conversation.participantName} получил(а) ваше сообщение. Мы ответим, как только сможем. Для срочных вопросов позвоните по указанному телефону."
        } else {
            "Здравствуйте! Спасибо, что написали. Я на связи и отвечу здесь."
        }
        insertMessage(conversationId, reply, ChatSenderType.PARTICIPANT, incrementUnread = true)
    }

    suspend fun sendAiReply(conversationId: String, userText: String) {
        val reply = petAiService.reply(userText)
        insertMessage(conversationId, reply, ChatSenderType.AI, incrementUnread = true)
    }

    private suspend fun insertMessage(
        conversationId: String,
        text: String,
        senderType: ChatSenderType,
        incrementUnread: Boolean
    ) {
        val conversation = chatDao.findConversationById(conversationId) ?: return
        val now = System.currentTimeMillis()
        chatDao.insertMessage(
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                text = text,
                isFromCurrentUser = senderType == ChatSenderType.USER,
                sentAt = now,
                senderType = senderType.name,
                isRead = senderType == ChatSenderType.USER || !incrementUnread
            )
        )
        val unread = if (incrementUnread) conversation.unreadCount + 1 else conversation.unreadCount
        chatDao.updatePreview(
            id = conversationId,
            participantName = conversation.participantName,
            lastMessage = text,
            updatedAt = now,
            unreadCount = unread,
            avatarUri = conversation.avatarUri
        )
    }

    private suspend fun findExisting(
        participantId: String,
        type: ChatParticipantType
    ): ChatConversationEntity? {
        val types = when (type) {
            ChatParticipantType.PRIVATE, ChatParticipantType.PRIVATE_OWNER ->
                listOf(ChatParticipantType.PRIVATE_OWNER.name, ChatParticipantType.PRIVATE.name)
            else -> listOf(type.name)
        }
        return chatDao.findConversationAny(participantId, types)
            ?: chatDao.findConversation(participantId, type.name)
    }

    private fun fallbackName(type: ChatParticipantType): String = when (type.canonical()) {
        ChatParticipantType.AI -> AdoptMeAiChat.NAME
        ChatParticipantType.SHELTER -> "Приют"
        ChatParticipantType.PRIVATE, ChatParticipantType.PRIVATE_OWNER -> "Владелец"
    }
}
