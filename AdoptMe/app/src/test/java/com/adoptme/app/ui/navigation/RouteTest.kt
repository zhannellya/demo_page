package com.adoptme.app.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class RouteTest {
    @Test
    fun everyScreenHasADistinctPath() {
        val paths = listOf(
            Route.Splash.path,
            Route.Welcome.path,
            Route.Login.path,
            Route.Register.path,
            Route.Home.path,
            Route.Favorites.path,
            Route.Shelters.path,
            Route.Chats.path,
            Route.Profile.path,
            Route.Settings.path,
            Route.Applications.path,
            Route.News.path,
            Route.Help.path,
            Route.Admin.path,
            Route.AnimalDetails.path,
            Route.Adoption.path,
            Route.ShelterDetails.path,
            Route.ChatThread.path,
            Route.NewsDetails.path,
            Route.AnimalEditor.path,
            Route.ApplicationDetails.path
        )
        assertEquals(paths.size, paths.toSet().size)
        assertEquals("animal/sharik", Route.AnimalDetails.create("sharik"))
        assertEquals("apply/sharik", Route.Adoption.create("sharik"))
        assertEquals(5, bottomBarRoutes.size)
    }
}
