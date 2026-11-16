package com.adoptme.app.data.repository

import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalFilter
import com.adoptme.app.data.model.AnimalType

fun List<Animal>.applyFilter(filter: AnimalFilter): List<Animal> {
    val query = filter.query.trim()
    return filter { animal ->
        val matchesQuery = query.isEmpty() ||
            animal.name.contains(query, ignoreCase = true) ||
            animal.breed.contains(query, ignoreCase = true) ||
            animal.location.contains(query, ignoreCase = true) ||
            animal.description.contains(query, ignoreCase = true)
        val matchesType = filter.type == null || animal.type == filter.type
        val matchesGender = filter.gender == null || animal.gender == filter.gender
        val matchesBreed = filter.breed.isNullOrBlank() ||
            animal.breed.equals(filter.breed, ignoreCase = true)
        val matchesAge = filter.ageGroup?.matches(animal.ageMonths) ?: true
        matchesQuery && matchesType && matchesGender && matchesBreed && matchesAge
    }
}

fun breedsFor(animals: List<Animal>, type: AnimalType?): List<String> {
    return animals
        .asSequence()
        .filter { type == null || it.type == type }
        .map { it.breed }
        .distinct()
        .sorted()
        .toList()
}
