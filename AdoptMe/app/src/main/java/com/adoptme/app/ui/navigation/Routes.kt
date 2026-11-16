package com.adoptme.app.ui.navigation

sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Welcome : Route("welcome")
    data object Login : Route("login")
    data object Register : Route("register")
    data object Home : Route("home")
    data object Favorites : Route("favorites")
    data object Shelters : Route("shelters")
    data object Chats : Route("chats")
    data object Profile : Route("profile")
    data object Settings : Route("settings")
    data object Applications : Route("applications")
    data object News : Route("news")
    data object Help : Route("help")
    data object Admin : Route("admin")
    data object AnimalDetails : Route("animal/{animalId}") {
        fun create(animalId: String) = "animal/$animalId"
    }
    data object Adoption : Route("apply/{animalId}") {
        fun create(animalId: String) = "apply/$animalId"
    }
    data object ShelterDetails : Route("shelter/{shelterId}") {
        fun create(shelterId: String) = "shelter/$shelterId"
    }
    data object ChatThread : Route("chat/{threadId}") {
        fun create(threadId: String) = "chat/$threadId"
    }
    data object NewsDetails : Route("news/{newsId}") {
        fun create(newsId: String) = "news/$newsId"
    }
    data object AnimalEditor : Route("admin/animal/{animalId}") {
        fun create(animalId: String) = "admin/animal/$animalId"
    }
    data object ApplicationDetails : Route("admin/application/{applicationId}") {
        fun create(applicationId: String) = "admin/application/$applicationId"
    }
}

val bottomBarRoutes = setOf(
    Route.Home.path,
    Route.Favorites.path,
    Route.Shelters.path,
    Route.Chats.path,
    Route.Profile.path
)
