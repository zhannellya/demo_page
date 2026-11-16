package com.adoptme.app.utils

import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalQuery
import com.adoptme.app.data.model.Shelter

object AnimalFilter {
    fun apply(animals: List<Animal>, query: AnimalQuery): List<Animal> {
        return animals.filter { animal ->
            val name = query.name.trim()
            val matchesName = name.isEmpty() || animal.name.contains(name, ignoreCase = true)
            val matchesSpecies = query.species == null || animal.species == query.species
            val matchesAge = query.ageGroup == null || animal.ageGroup == query.ageGroup
            val matchesSize = query.size == null || animal.size == query.size
            val matchesGender = query.gender == null || animal.gender == query.gender
            val city = query.city?.trim().orEmpty()
            val matchesCity = city.isEmpty() || animal.city.equals(city, ignoreCase = true)
            matchesName && matchesSpecies && matchesAge && matchesSize && matchesGender && matchesCity
        }
    }
}

object ShelterFilter {
    fun apply(shelters: List<Shelter>, city: String?, query: String): List<Shelter> {
        val selectedCity = city?.trim().orEmpty()
        val text = query.trim()
        return shelters.filter { shelter ->
            val matchesCity = selectedCity.isEmpty() || shelter.city.equals(selectedCity, ignoreCase = true)
            val matchesText = text.isEmpty() ||
                shelter.name.contains(text, ignoreCase = true) ||
                shelter.description.contains(text, ignoreCase = true) ||
                shelter.city.contains(text, ignoreCase = true)
            matchesCity && matchesText
        }
    }
}
