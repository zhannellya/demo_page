package com.adoptme.app.ui.navigation

import android.net.Uri

sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Welcome : Route("welcome")
    data object Register : Route("register")
    data object Login : Route("login")
    data object Home : Route("home")
    data object Search : Route("search")
    data object Favorites : Route("favorites")
    data object Chats : Route("chats")
    data object Applications : Route("applications")
    data object Profile : Route("profile")
    data object Notifications : Route("notifications")
    data object LostFound : Route("lost_found")
    data object HelpDonate : Route("help_donate")
    data object Veterinary : Route("veterinary")
    data object Map : Route("map")
    data object AnimalDetails : Route("animal/{animalId}") {
        fun create(animalId: String) = "animal/$animalId"
    }
    data object AdoptionApplication : Route("apply/{animalId}/{animalName}") {
        fun create(animalId: String, animalName: String) = "apply/$animalId/$animalName"
    }
    data object AddPet : Route("add_pet")
    data object MatchQuiz : Route("match_quiz")
    data object AiChat : Route("ai_chat")
    data object Chat : Route("chat/{participantId}/{participantType}") {
        fun create(participantId: String, participantType: String) =
            "chat/${Uri.encode(participantId)}/${Uri.encode(participantType)}"
    }
    data object DonationPayment : Route("donation_payment/{shelterId}/{purpose}/{amount}") {
        fun create(shelterId: String, purpose: String, amount: Int?) =
            "donation_payment/${Uri.encode(shelterId.ifBlank { "s1" })}/${Uri.encode(purpose.ifBlank { "custom" })}/${amount ?: 0}"
    }
    data object ShelterDetail : Route("shelter_detail/{shelterId}") {
        fun create(shelterId: String) = "shelter_detail/${Uri.encode(shelterId)}"
    }
    data object ShelterAnimals : Route("shelter_animals/{shelterId}") {
        fun create(shelterId: String) = "shelter_animals/${Uri.encode(shelterId)}"
    }
}

val bottomBarRoutes = listOf(
    Route.Home.path,
    Route.Search.path,
    Route.Chats.path,
    Route.Applications.path,
    Route.Profile.path
)
