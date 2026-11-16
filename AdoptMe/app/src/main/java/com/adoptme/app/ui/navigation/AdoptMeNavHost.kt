package com.adoptme.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adoptme.app.ui.screens.admin.AdminScreen
import com.adoptme.app.ui.screens.admin.AnimalEditorScreen
import com.adoptme.app.ui.screens.admin.ApplicationReviewScreen
import com.adoptme.app.ui.screens.application.AdoptionScreen
import com.adoptme.app.ui.screens.application.MyApplicationsScreen
import com.adoptme.app.ui.screens.auth.LoginScreen
import com.adoptme.app.ui.screens.auth.RegisterScreen
import com.adoptme.app.ui.screens.chat.ChatListScreen
import com.adoptme.app.ui.screens.chat.ChatThreadScreen
import com.adoptme.app.ui.screens.details.AnimalDetailsScreen
import com.adoptme.app.ui.screens.favorites.FavoritesScreen
import com.adoptme.app.ui.screens.help.HelpScreen
import com.adoptme.app.ui.screens.home.HomeScreen
import com.adoptme.app.ui.screens.map.ShelterDetailScreen
import com.adoptme.app.ui.screens.map.ShelterSearchScreen
import com.adoptme.app.ui.screens.news.NewsDetailScreen
import com.adoptme.app.ui.screens.news.NewsScreen
import com.adoptme.app.ui.screens.onboarding.WelcomeScreen
import com.adoptme.app.ui.screens.profile.ProfileScreen
import com.adoptme.app.ui.screens.profile.SettingsScreen
import com.adoptme.app.ui.screens.splash.SplashScreen
import com.adoptme.app.ui.theme.BottomBarShape
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextMuted

private data class BottomItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun AdoptMeNavHost() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes

    val bottomItems = listOf(
        BottomItem(Route.Home.path, "Главная", Icons.Filled.Home, Icons.Outlined.Home),
        BottomItem(Route.Favorites.path, "Избранное", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        BottomItem(Route.Shelters.path, "Приюты", Icons.Filled.Place, Icons.Outlined.Place),
        BottomItem(Route.Chats.path, "Чаты", Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat),
        BottomItem(Route.Profile.path, "Профиль", Icons.Filled.Person, Icons.Outlined.Person)
    )

    Scaffold(
        containerColor = Color.Transparent,
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
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Route.Home.path) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                if (selected) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(PurplePrimary.copy(0.12f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(item.selectedIcon, contentDescription = item.label, tint = PurplePrimary)
                                    }
                                } else {
                                    Icon(item.unselectedIcon, contentDescription = item.label, tint = TextMuted)
                                }
                            },
                            label = {
                                Text(
                                    item.label,
                                    color = if (selected) PurplePrimary else TextMuted,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
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
                SplashScreen { onboardingDone, loggedIn ->
                    val target = when {
                        !onboardingDone -> Route.Welcome.path
                        !loggedIn -> Route.Login.path
                        else -> Route.Home.path
                    }
                    navController.navigate(target) {
                        popUpTo(Route.Splash.path) { inclusive = true }
                    }
                }
            }
            composable(Route.Welcome.path) {
                WelcomeScreen {
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Welcome.path) { inclusive = true }
                    }
                }
            }
            composable(Route.Login.path) {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Login.path) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onRegister = { navController.navigate(Route.Register.path) }
                )
            }
            composable(Route.Register.path) {
                RegisterScreen(
                    onRegistered = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Login.path) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onLogin = { navController.popBackStack() }
                )
            }
            composable(Route.Home.path) {
                HomeScreen(
                    onOpenAnimal = { navController.navigate(Route.AnimalDetails.create(it)) },
                    onNews = { navController.navigate(Route.News.path) },
                    onHelp = { navController.navigate(Route.Help.path) },
                    onApplications = { navController.navigate(Route.Applications.path) }
                )
            }
            composable(Route.Favorites.path) {
                FavoritesScreen(
                    onOpenAnimal = { navController.navigate(Route.AnimalDetails.create(it)) },
                    onBrowse = {
                        navController.navigate(Route.Home.path) { launchSingleTop = true }
                    }
                )
            }
            composable(Route.Shelters.path) {
                ShelterSearchScreen(
                    onOpenShelter = { navController.navigate(Route.ShelterDetails.create(it)) }
                )
            }
            composable(Route.Chats.path) {
                ChatListScreen(onOpenThread = { navController.navigate(Route.ChatThread.create(it)) })
            }
            composable(Route.Profile.path) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Route.Login.path) {
                            popUpTo(Route.Home.path) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onApplications = { navController.navigate(Route.Applications.path) },
                    onFavorites = {
                        navController.navigate(Route.Favorites.path) { launchSingleTop = true }
                    },
                    onSettings = { navController.navigate(Route.Settings.path) },
                    onHelp = { navController.navigate(Route.Help.path) },
                    onNews = { navController.navigate(Route.News.path) },
                    onAdmin = { navController.navigate(Route.Admin.path) }
                )
            }
            composable(Route.Settings.path) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.Applications.path) {
                MyApplicationsScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.News.path) {
                NewsScreen(
                    onBack = { navController.popBackStack() },
                    onOpen = { navController.navigate(Route.NewsDetails.create(it)) }
                )
            }
            composable(Route.Help.path) {
                HelpScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.Admin.path) {
                AdminScreen(
                    onBack = { navController.popBackStack() },
                    onOpenApplication = { navController.navigate(Route.ApplicationDetails.create(it)) },
                    onEditAnimal = { navController.navigate(Route.AnimalEditor.create(it)) },
                    onCreateAnimal = { navController.navigate(Route.AnimalEditor.create("new")) }
                )
            }
            composable(
                route = Route.AnimalDetails.path,
                arguments = listOf(navArgument("animalId") { type = NavType.StringType })
            ) { entry ->
                AnimalDetailsScreen(
                    animalId = entry.arguments?.getString("animalId").orEmpty(),
                    onBack = { navController.popBackStack() },
                    onAdopt = { navController.navigate(Route.Adoption.create(it)) }
                )
            }
            composable(
                route = Route.Adoption.path,
                arguments = listOf(navArgument("animalId") { type = NavType.StringType })
            ) { entry ->
                AdoptionScreen(
                    animalId = entry.arguments?.getString("animalId").orEmpty(),
                    onBack = { navController.popBackStack() },
                    onSubmitted = {
                        navController.navigate(Route.Applications.path) {
                            popUpTo(Route.Home.path)
                        }
                    }
                )
            }
            composable(
                route = Route.ShelterDetails.path,
                arguments = listOf(navArgument("shelterId") { type = NavType.StringType })
            ) { entry ->
                ShelterDetailScreen(
                    shelterId = entry.arguments?.getString("shelterId").orEmpty(),
                    onBack = { navController.popBackStack() },
                    onOpenChat = { navController.navigate(Route.ChatThread.create(it)) }
                )
            }
            composable(
                route = Route.ChatThread.path,
                arguments = listOf(navArgument("threadId") { type = NavType.StringType })
            ) { entry ->
                ChatThreadScreen(
                    threadId = entry.arguments?.getString("threadId").orEmpty(),
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Route.NewsDetails.path,
                arguments = listOf(navArgument("newsId") { type = NavType.StringType })
            ) { entry ->
                NewsDetailScreen(
                    newsId = entry.arguments?.getString("newsId").orEmpty(),
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Route.AnimalEditor.path,
                arguments = listOf(navArgument("animalId") { type = NavType.StringType })
            ) { entry ->
                AnimalEditorScreen(
                    animalId = entry.arguments?.getString("animalId").orEmpty(),
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Route.ApplicationDetails.path,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType })
            ) { entry ->
                ApplicationReviewScreen(
                    applicationId = entry.arguments?.getString("applicationId").orEmpty(),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
