package com.adoptme.app

import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalFilter
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.repository.applyFilter
import com.adoptme.app.data.repository.breedsFor
import org.junit.Assert.assertEquals
import org.junit.Test

class AnimalFilterTest {
    @Test
    fun filtersByTypeAgeGenderBreedAndQuery() {
        val animals = listOf(
            animal("1", "Луна", AnimalType.CAT, "Сиамская", 8, Gender.FEMALE),
            animal("2", "Рекс", AnimalType.DOG, "Лабрадор", 20, Gender.MALE),
            animal("3", "Клео", AnimalType.CAT, "Персидская", 60, Gender.FEMALE)
        )

        assertEquals(2, animals.applyFilter(AnimalFilter(type = AnimalType.CAT)).size)
        assertEquals(
            listOf("Луна"),
            animals.applyFilter(
                AnimalFilter(gender = Gender.FEMALE, ageGroup = AgeGroup.UNDER_ONE)
            ).map { it.name }
        )
        assertEquals("Рекс", animals.applyFilter(AnimalFilter(breed = "Лабрадор")).single().name)
        assertEquals("Клео", animals.applyFilter(AnimalFilter(query = "перс")).single().name)
        assertEquals(listOf("Лабрадор"), breedsFor(animals, AnimalType.DOG))
    }

    private fun animal(
        id: String,
        name: String,
        type: AnimalType,
        breed: String,
        ageMonths: Int,
        gender: Gender
    ) = Animal(
        id = id,
        name = name,
        type = type,
        breed = breed,
        age = "$ageMonths мес.",
        ageMonths = ageMonths,
        gender = gender,
        healthStatus = HealthStatus.HEALTHY,
        description = "Описание $breed",
        imageUrl = "",
        location = "Приют",
        size = "Средний"
    )
}
