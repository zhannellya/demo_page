package com.adoptme.app.utils

import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalSize
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.Gender

data class AnimalQuery(
    val text: String = "",
    val type: AnimalType? = null,
    val ageGroup: AgeGroup? = null,
    val size: AnimalSize? = null,
    val gender: Gender? = null,
    val city: String? = null,
    val favoritesOnly: Boolean = false
)

fun Animal.ageGroup(): AgeGroup = when {
    ageMonths < 12 -> AgeGroup.UNDER_ONE
    ageMonths <= 36 -> AgeGroup.ONE_TO_THREE
    else -> AgeGroup.OVER_THREE
}

fun List<Animal>.filterAnimals(query: AnimalQuery): List<Animal> {
    val needle = query.text.trim()
    return filter { animal ->
        val nameMatches = needle.isEmpty() || animal.name.contains(needle, ignoreCase = true)
        val typeMatches = query.type == null || animal.type == query.type
        val ageMatches = query.ageGroup == null || animal.ageGroup() == query.ageGroup
        val sizeMatches = query.size == null || animal.size == query.size
        val genderMatches = query.gender == null || animal.gender == query.gender
        val cityMatches = query.city.isNullOrBlank() || animal.city.equals(query.city, ignoreCase = true)
        val favoriteMatches = !query.favoritesOnly || animal.isFavorite
        nameMatches && typeMatches && ageMatches && sizeMatches && genderMatches && cityMatches && favoriteMatches
    }
}
