package com.adoptme.app.utils

import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalQuery
import com.adoptme.app.data.model.AnimalStatus
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.PetSize
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.data.model.Species
import org.junit.Assert.assertEquals
import org.junit.Test

class AnimalFilterTest {
    private val animals = listOf(
        pet("sharik", "Шарик", Species.DOG, 24, AgeGroup.YOUNG, PetSize.MEDIUM, Gender.MALE, "Москва"),
        pet("tosha", "Тоша", Species.CAT, 8, AgeGroup.BABY, PetSize.SMALL, Gender.MALE, "Санкт-Петербург"),
        pet("bella", "Белла", Species.DOG, 48, AgeGroup.ADULT, PetSize.MEDIUM, Gender.FEMALE, "Казань"),
        pet("reks", "Рекс", Species.DOG, 96, AgeGroup.SENIOR, PetSize.LARGE, Gender.MALE, "Казань")
    )

    @Test
    fun searchIsCaseInsensitiveByName() {
        val found = AnimalFilter.apply(animals, AnimalQuery(name = "шаР"))
        assertEquals(listOf("sharik"), found.map { it.id })
    }

    @Test
    fun filtersCombineSpeciesAgeSizeGenderAndCity() {
        val found = AnimalFilter.apply(
            animals,
            AnimalQuery(
                species = Species.DOG,
                ageGroup = AgeGroup.ADULT,
                size = PetSize.MEDIUM,
                gender = Gender.FEMALE,
                city = "Казань"
            )
        )
        assertEquals(listOf("bella"), found.map { it.id })
    }

    @Test
    fun emptyQueryReturnsEveryone() {
        assertEquals(4, AnimalFilter.apply(animals, AnimalQuery()).size)
    }

    @Test
    fun shelterSearchUsesCityAndName() {
        val shelters = listOf(
            Shelter("1", "Добрые лапы", "Москва", "кошки", "", "", "", ""),
            Shelter("2", "Невский дом", "Санкт-Петербург", "собаки", "", "", "", "")
        )
        assertEquals(listOf("1"), ShelterFilter.apply(shelters, "Москва", "лапы").map { it.id })
        assertEquals(listOf("2"), ShelterFilter.apply(shelters, null, "невский").map { it.id })
    }

    private fun pet(
        id: String,
        name: String,
        species: Species,
        months: Int,
        age: AgeGroup,
        size: PetSize,
        gender: Gender,
        city: String
    ) = Animal(
        id = id,
        name = name,
        species = species,
        breed = "порода",
        ageMonths = months,
        ageLabel = "$months мес.",
        ageGroup = age,
        gender = gender,
        size = size,
        temperament = "спокойный",
        story = "история животного длиннее десяти символов",
        shelterId = "s",
        shelterName = "Приют",
        city = city,
        imageKey = "animal_placeholder",
        status = AnimalStatus.AVAILABLE
    )
}
