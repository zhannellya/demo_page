package com.adoptme.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adoptme.app.ui.screens.application.AdoptionApplicationScreen
import com.adoptme.app.ui.screens.applications.ApplicationsScreen
import com.adoptme.app.ui.screens.auth.LoginScreen
import com.adoptme.app.ui.screens.auth.RegisterScreen
import com.adoptme.app.ui.screens.details.AnimalDetailsScreen
import com.adoptme.app.ui.screens.favorites.FavoritesScreen
import com.adoptme.app.ui.screens.help.HelpDonateScreen
import com.adoptme.app.ui.screens.home.HomeScreen
import com.adoptme.app.ui.screens.lostfound.LostFoundScreen
import com.adoptme.app.ui.screens.map.MapScreen
import com.adoptme.app.ui.screens.notifications.NotificationsScreen
import com.adoptme.app.ui.screens.profile.ProfileScreen
import com.adoptme.app.ui.screens.search.SearchScreen
import com.adoptme.app.ui.screens.splash.SplashScreen
import com.adoptme.app.ui.screens.veterinary.VeterinaryScreen
import com.adoptme.app.ui.screens.welcome.WelcomeScreen
import com.adoptme.app.ui.theme.Blush
import com.adoptme.app.ui.theme.BottomBarShape
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.TextMuted

private data class BottomItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private fun NavHostController.openTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AdoptMeNavHost() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes

    val bottomItems = listOf(
        BottomItem(Route.Home.path, "Главная", Icons.Filled.Home, Icons.Outlined.Home),
        BottomItem(Route.Search.path, "Поиск", Icons.Filled.Search, Icons.Outlined.Search),
        BottomItem(Route.Favorites.path, "Избранное", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        BottomItem(Route.Applications.path, "Заявки", Icons.AutoMirrored.Outlined.Assignment, Icons.AutoMirrored.Outlined.Assignment),
        BottomItem(Route.Profile.path, "Профиль", Icons.Filled.Person, Icons.Outlined.Person)
    )

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.clip(BottomBarShape),
                    containerColor = LilacCard,
                    tonalElevation = 8.dp
                ) {
                    bottomItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.openTab(item.route) },
                            icon = {
                                if (selected) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Plum.copy(alpha = 0.12f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(item.selectedIcon, contentDescription = item.label, tint = Plum)
                                    }
                                } else {
                                    Icon(item.unselectedIcon, contentDescription = item.label, tint = TextMuted)
                                }
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (selected) Plum else TextMuted
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Blush.copy(alpha = 0.65f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.Splash.path,
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.Splash.path) {
                SplashScreen { loggedIn ->
                    navController.navigate(if (loggedIn) Route.Home.path else Route.Welcome.path) {
                        popUpTo(Route.Splash.path) { inclusive = true }
                    }
                }
            }
            composable(Route.Welcome.path) {
                WelcomeScreen(
                    onLogin = { navController.navigate(Route.Login.path) },
                    onRegister = { navController.navigate(Route.Register.path) }
                )
            }
            composable(Route.Register.path) {
                RegisterScreen(
                    onRegistered = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onGoLogin = {
                        if (!navController.popBackStack(Route.Login.path, inclusive = false)) {
                            navController.navigate(Route.Login.path)
                        }
                    }
                )
            }
            composable(Route.Login.path) {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onGoRegister = { navController.navigate(Route.Register.path) }
                )
            }
            composable(Route.Home.path) {
                HomeScreen(
                    onOpenAnimal = { navController.navigate(Route.AnimalDetails.create(it)) },
                    onSeeAll = { navController.openTab(Route.Search.path) },
                    onNotifications = { navController.navigate(Route.Notifications.path) },
                    onLostFound = { navController.navigate(Route.LostFound.path) },
                    onHelp = { navController.navigate(Route.HelpDonate.path) },
                    onVeterinary = { navController.navigate(Route.Veterinary.path) }
                )
            }
            composable(Route.Search.path) {
                SearchScreen(onOpenAnimal = { navController.navigate(Route.AnimalDetails.create(it)) })
            }
            composable(Route.Favorites.path) {
                FavoritesScreen(
                    onOpenAnimal = { navController.navigate(Route.AnimalDetails.create(it)) },
                    onBrowse = { navController.openTab(Route.Search.path) }
                )
            }
            composable(Route.Applications.path) {
                ApplicationsScreen(
                    onOpenAnimal = { navController.navigate(Route.AnimalDetails.create(it)) },
                    onBrowse = { navController.openTab(Route.Search.path) }
                )
            }
            composable(Route.Map.path) { MapScreen(onBack = { navController.popBackStack() }) }
            composable(Route.Profile.path) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Route.Welcome.path) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onApplications = { navController.openTab(Route.Applications.path) },
                    onNotifications = { navController.navigate(Route.Notifications.path) },
                    onHelp = { navController.navigate(Route.HelpDonate.path) },
                    onLostFound = { navController.navigate(Route.LostFound.path) },
                    onVeterinary = { navController.navigate(Route.Veterinary.path) },
                    onMap = { navController.navigate(Route.Map.path) }
                )
            }
            composable(
                route = Route.AnimalDetails.path,
                arguments = listOf(navArgument("animalId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("animalId").orEmpty()
                AnimalDetailsScreen(
                    animalId = id,
                    onBack = { navController.popBackStack() },
                    onAdopt = { animalId, name ->
                        navController.navigate(Route.AdoptionApplication.create(animalId, name))
                    }
                )
            }
            composable(
                route = Route.AdoptionApplication.path,
                arguments = listOf(
                    navArgument("animalId") { type = NavType.StringType },
                    navArgument("animalName") { type = NavType.StringType }
                )
            ) { entry ->
                val id = entry.arguments?.getString("animalId").orEmpty()
                val name = entry.arguments?.getString("animalName").orEmpty()
                AdoptionApplicationScreen(
                    animalId = id,
                    animalName = name,
                    onBack = { navController.popBackStack() },
                    onSubmitted = { navController.openTab(Route.Applications.path) }
                )
            }
            composable(Route.LostFound.path) {
                LostFoundScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.HelpDonate.path) {
                HelpDonateScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.Veterinary.path) {
                VeterinaryScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.Notifications.path) {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
