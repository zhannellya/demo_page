package com.adoptme.app

import com.adoptme.app.data.ai.LocalPetAiService
import com.adoptme.app.data.model.parseChatParticipantType
import com.adoptme.app.data.model.ChatParticipantType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PetAiServiceTest {
    private val service = LocalPetAiService()

    @Test
    fun emergencySymptomsAskForVet() = runBlocking {
        val reply = service.reply("Собака не дышит и без сознания")
        assertEquals(
            "Это может быть экстренная ситуация. Немедленно обратитесь в ближайшую ветеринарную клинику",
            reply
        )
    }

    @Test
    fun kittenFeedingDoesNotPrescribeMedicine() = runBlocking {
        val reply = service.reply("Чем кормить котёнка?")
        assertTrue(reply.contains("котён", ignoreCase = true) || reply.contains("котят", ignoreCase = true))
        assertFalse(reply.contains("мг"))
        assertFalse(reply.contains("таблет", ignoreCase = true))
    }

    @Test
    fun parsesOwnerAndAiParticipantTypes() {
        assertEquals(ChatParticipantType.PRIVATE_OWNER, parseChatParticipantType("PRIVATE"))
        assertEquals(ChatParticipantType.PRIVATE_OWNER, parseChatParticipantType("PRIVATE_OWNER"))
        assertEquals(ChatParticipantType.AI, parseChatParticipantType("AI"))
        assertEquals(ChatParticipantType.SHELTER, parseChatParticipantType("SHELTER"))
    }
}
