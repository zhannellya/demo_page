package com.adoptme.app

import com.adoptme.app.data.model.ChatParticipantType
import com.adoptme.app.data.model.OwnerType
import com.adoptme.app.data.model.chatParticipantId
import com.adoptme.app.data.model.chatParticipantType
import com.adoptme.app.data.model.formatKzt
import com.adoptme.app.data.model.parseAgeMonths
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ModelHelpersTest {
    @Test
    fun formatsTengeWithSpaces() {
        assertEquals("1 500 ₸", formatKzt(1500))
        assertEquals("4 000 ₸", formatKzt(4000))
        assertEquals("2 500 ₸", formatKzt(2500))
    }

    @Test
    fun parsesAgeToMonths() {
        assertEquals(24, parseAgeMonths("2 года"))
        assertEquals(8, parseAgeMonths("8 месяцев"))
        assertEquals(36, parseAgeMonths("3"))
    }

    @Test
    fun mapsChatParticipantFromOwnerType() {
        val shelterPet = sampleAnimal(ownerType = OwnerType.SHELTER, shelterId = "s5", ownerPhone = "+77017500984")
        val privatePet = sampleAnimal(ownerType = OwnerType.PRIVATE, shelterId = null, ownerPhone = "+77011234567")
        assertEquals(ChatParticipantType.SHELTER, shelterPet.chatParticipantType())
        assertEquals("s5", shelterPet.chatParticipantId())
        assertEquals(ChatParticipantType.PRIVATE, privatePet.chatParticipantType())
        assertEquals("+77011234567", privatePet.chatParticipantId())
    }

    private fun sampleAnimal(
        ownerType: OwnerType,
        shelterId: String?,
        ownerPhone: String
    ) = Animal(
        id = "x",
        name = "Луна",
        type = AnimalType.CAT,
        breed = "Дворовая",
        age = "1 год",
        ageMonths = 12,
        gender = Gender.FEMALE,
        healthStatus = HealthStatus.HEALTHY,
        description = "Описание",
        imageUrl = "",
        location = "Алматы",
        size = "Средний",
        ownerType = ownerType,
        ownerPhone = ownerPhone,
        shelterId = shelterId
    )
}
