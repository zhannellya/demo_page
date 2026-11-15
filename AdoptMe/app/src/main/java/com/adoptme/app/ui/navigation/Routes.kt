package com.adoptme.app.ui.navigation

sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Onboarding : Route("onboarding")
    data object SignUp : Route("signup")
    data object Login : Route("login")
    data object Home : Route("home")
    data object Animals : Route("animals")
    data object Favorites : Route("favorites")
    data object Map : Route("map")
    data object Profile : Route("profile")
    data object Notifications : Route("notifications")
    data object LostFound : Route("lost_found")
    data object HelpDonate : Route("help_donate")
    data object Veterinary : Route("veterinary")
    data object AnimalDetails : Route("animal/{animalId}") {
        fun create(animalId: String) = "animal/$animalId"
    }
    data object AdoptionApplication : Route("apply/{animalId}/{animalName}") {
        fun create(animalId: String, animalName: String) =
            "apply/$animalId/${java.net.URLEncoder.encode(animalName, "UTF-8")}"
    }
}

val bottomBarRoutes = listOf(
    Route.Home.path,
    Route.Animals.path,
    Route.Favorites.path,
    Route.Map.path,
    Route.Profile.path
)
