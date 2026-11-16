package com.adoptme.app.ui.navigation

sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Welcome : Route("welcome")
    data object Register : Route("register")
    data object Login : Route("login")
    data object Home : Route("home")
    data object Search : Route("search")
    data object Favorites : Route("favorites")
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
}

val bottomBarRoutes = listOf(
    Route.Home.path,
    Route.Search.path,
    Route.Favorites.path,
    Route.Applications.path,
    Route.Profile.path
)
