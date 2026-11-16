package com.adoptme.app

import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalSize
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.Availability
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.utils.AnimalQuery
import com.adoptme.app.utils.ageGroup
import com.adoptme.app.utils.filterAnimals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimalFilterTest {

    private val animals = listOf(
        sample("sharik", "Шарик", AnimalType.DOG, 48, AnimalSize.LARGE, Gender.MALE, "Москва"),
        sample("tosha", "Тоша", AnimalType.CAT, 24, AnimalSize.MEDIUM, Gender.MALE, "Санкт-Петербург", favorite = true),
        sample("barsik", "Барсик", AnimalType.CAT, 8, AnimalSize.SMALL, Gender.MALE, "Новосибирск"),
        sample("bella", "Белла", AnimalType.DOG, 36, AnimalSize.MEDIUM, Gender.FEMALE, "Казань")
    )

    @Test
    fun searchMatchesNameIgnoringCase() {
        val found = animals.filterAnimals(AnimalQuery(text = "шар"))
        assertEquals(listOf("Шарик"), found.map { it.name })
        assertTrue(animals.filterAnimals(AnimalQuery(text = "бобик")).isEmpty())
    }

    @Test
    fun filtersByTypeAgeSizeGenderCityAndFavorites() {
        assertEquals(listOf("Тоша", "Барсик"), animals.filterAnimals(AnimalQuery(type = AnimalType.CAT)).map { it.name })
        assertEquals(listOf("Барсик"), animals.filterAnimals(AnimalQuery(ageGroup = AgeGroup.UNDER_ONE)).map { it.name })
        assertEquals(listOf("Шарик"), animals.filterAnimals(AnimalQuery(size = AnimalSize.LARGE)).map { it.name })
        assertEquals(listOf("Белла"), animals.filterAnimals(AnimalQuery(gender = Gender.FEMALE)).map { it.name })
        assertEquals(listOf("Тоша"), animals.filterAnimals(AnimalQuery(city = "санкт-петербург")).map { it.name })
        assertEquals(listOf("Тоша"), animals.filterAnimals(AnimalQuery(favoritesOnly = true)).map { it.name })
    }

    @Test
    fun ageGroupBoundaries() {
        assertEquals(AgeGroup.UNDER_ONE, sample("a", "А", AnimalType.DOG, 11, AnimalSize.SMALL, Gender.MALE, "Москва").ageGroup())
        assertEquals(AgeGroup.ONE_TO_THREE, sample("b", "Б", AnimalType.DOG, 12, AnimalSize.SMALL, Gender.MALE, "Москва").ageGroup())
        assertEquals(AgeGroup.ONE_TO_THREE, sample("c", "В", AnimalType.DOG, 36, AnimalSize.SMALL, Gender.MALE, "Москва").ageGroup())
        assertEquals(AgeGroup.OVER_THREE, sample("d", "Г", AnimalType.DOG, 37, AnimalSize.SMALL, Gender.MALE, "Москва").ageGroup())
    }

    private fun sample(
        id: String,
        name: String,
        type: AnimalType,
        ageMonths: Int,
        size: AnimalSize,
        gender: Gender,
        city: String,
        favorite: Boolean = false
    ) = Animal(
        id = id,
        name = name,
        type = type,
        breed = "порода",
        ageLabel = "$ageMonths мес.",
        ageMonths = ageMonths,
        gender = gender,
        size = size,
        healthStatus = HealthStatus.HEALTHY,
        availability = Availability.AVAILABLE,
        personality = "характер",
        description = "описание",
        story = "история",
        shelterId = "shelter",
        shelterName = "приют",
        city = city,
        imageUrl = "",
        isFavorite = favorite
    )
}
