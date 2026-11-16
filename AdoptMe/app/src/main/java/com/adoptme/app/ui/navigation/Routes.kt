package com.adoptme.app.ui.navigation

sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Onboarding : Route("onboarding")
    data object Login : Route("login")
    data object Register : Route("register")
    data object Home : Route("home")
    data object Applications : Route("applications")
    data object Favorites : Route("favorites")
    data object Shelters : Route("shelters")
    data object Profile : Route("profile")
    data object Settings : Route("settings")
    data object News : Route("news")
    data object NewsDetails : Route("news/{articleId}") {
        fun create(articleId: String) = "news/$articleId"
    }
    data object Help : Route("help")
    data object Chats : Route("chats")
    data object ChatThread : Route("chat/{conversationId}") {
        fun create(conversationId: String) = "chat/$conversationId"
    }
    data object Notifications : Route("notifications")
    data object Admin : Route("admin")
    data object AdminApplication : Route("admin/application/{applicationId}") {
        fun create(applicationId: String) = "admin/application/$applicationId"
    }
    data object AnimalEditor : Route("admin/animal/{animalId}") {
        fun create(animalId: String) = "admin/animal/$animalId"
    }
    data object AnimalDetails : Route("animal/{animalId}") {
        fun create(animalId: String) = "animal/$animalId"
    }
    data object Adoption : Route("apply/{animalId}") {
        fun create(animalId: String) = "apply/$animalId"
    }
    data object LostFound : Route("lost_found")
    data object Veterinary : Route("veterinary")
}

val bottomBarRoutes = listOf(
    Route.Home.path,
    Route.Applications.path,
    Route.Favorites.path,
    Route.Shelters.path,
    Route.Profile.path
)
