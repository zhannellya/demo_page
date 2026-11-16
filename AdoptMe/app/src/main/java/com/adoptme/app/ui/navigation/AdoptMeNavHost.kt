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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.automirrored.outlined.Assignment
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adoptme.app.ui.screens.admin.AdminApplicationScreen
import com.adoptme.app.ui.screens.admin.AdminScreen
import com.adoptme.app.ui.screens.admin.AnimalEditorScreen
import com.adoptme.app.ui.screens.application.AdoptionApplicationScreen
import com.adoptme.app.ui.screens.applications.MyApplicationsScreen
import com.adoptme.app.ui.screens.auth.LoginScreen
import com.adoptme.app.ui.screens.auth.RegisterScreen
import com.adoptme.app.ui.screens.chat.ChatListScreen
import com.adoptme.app.ui.screens.chat.ChatThreadScreen
import com.adoptme.app.ui.screens.details.AnimalDetailsScreen
import com.adoptme.app.ui.screens.favorites.FavoritesScreen
import com.adoptme.app.ui.screens.help.HelpDonateScreen
import com.adoptme.app.ui.screens.home.HomeScreen
import com.adoptme.app.ui.screens.lostfound.LostFoundScreen
import com.adoptme.app.ui.screens.map.ShelterSearchScreen
import com.adoptme.app.ui.screens.news.NewsDetailScreen
import com.adoptme.app.ui.screens.news.NewsScreen
import com.adoptme.app.ui.screens.notifications.NotificationsScreen
import com.adoptme.app.ui.screens.onboarding.OnboardingScreen
import com.adoptme.app.ui.screens.profile.ProfileScreen
import com.adoptme.app.ui.screens.profile.SettingsScreen
import com.adoptme.app.ui.screens.splash.SplashScreen
import com.adoptme.app.ui.screens.veterinary.VeterinaryScreen
import com.adoptme.app.ui.theme.BottomBarShape
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.PinkNeon
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
        BottomItem(Route.Applications.path, "Заявки", Icons.AutoMirrored.Outlined.Assignment, Icons.AutoMirrored.Outlined.Assignment),
        BottomItem(Route.Favorites.path, "Избранное", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        BottomItem(Route.Shelters.path, "Приюты", Icons.Filled.Place, Icons.Outlined.Place),
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
                                indicatorColor = PinkNeon.copy(alpha = 0.12f)
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
                        !onboardingDone -> Route.Onboarding.path
                        !loggedIn -> Route.Login.path
                        else -> Route.Home.path
                    }
                    navController.navigate(target) {
                        popUpTo(Route.Splash.path) { inclusive = true }
                    }
                }
            }
            composable(Route.Onboarding.path) {
                OnboardingScreen {
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Onboarding.path) { inclusive = true }
                    }
                }
            }
            composable(Route.Login.path) {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Login.path) { inclusive = true }
                        }
                    },
                    onGoRegister = { navController.navigate(Route.Register.path) }
                )
            }
            composable(Route.Register.path) {
                RegisterScreen(
                    onRegistered = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Login.path) { inclusive = true }
                        }
                    },
                    onGoLogin = { navController.popBackStack() }
                )
            }
            composable(Route.Home.path) {
                HomeScreen(
                    onOpenAnimal = { navController.navigate(Route.AnimalDetails.create(it)) },
                    onNews = { navController.navigate(Route.News.path) },
                    onChat = { navController.navigate(Route.Chats.path) },
                    onHelp = { navController.navigate(Route.Help.path) },
                    onNotifications = { navController.navigate(Route.Notifications.path) }
                )
            }
            composable(Route.Applications.path) {
                MyApplicationsScreen()
            }
            composable(Route.Favorites.path) {
                FavoritesScreen(
                    onOpenAnimal = { navController.navigate(Route.AnimalDetails.create(it)) },
                    onBrowse = {
                        navController.navigate(Route.Home.path) { launchSingleTop = true }
                    }
                )
            }
            composable(Route.Shelters.path) { ShelterSearchScreen() }
            composable(Route.Profile.path) {
                ProfileScreen(
                    onApplications = {
                        navController.navigate(Route.Applications.path) { launchSingleTop = true }
                    },
                    onFavorites = {
                        navController.navigate(Route.Favorites.path) { launchSingleTop = true }
                    },
                    onSettings = { navController.navigate(Route.Settings.path) },
                    onNews = { navController.navigate(Route.News.path) },
                    onHelp = { navController.navigate(Route.Help.path) },
                    onChats = { navController.navigate(Route.Chats.path) },
                    onNotifications = { navController.navigate(Route.Notifications.path) },
                    onLostFound = { navController.navigate(Route.LostFound.path) },
                    onVeterinary = { navController.navigate(Route.Veterinary.path) },
                    onAdmin = { navController.navigate(Route.Admin.path) },
                    onLogout = {
                        navController.navigate(Route.Login.path) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
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
                    onAdopt = { animalId -> navController.navigate(Route.Adoption.create(animalId)) }
                )
            }
            composable(
                route = Route.Adoption.path,
                arguments = listOf(navArgument("animalId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("animalId").orEmpty()
                AdoptionApplicationScreen(
                    animalId = id,
                    onBack = { navController.popBackStack() },
                    onSubmitted = {
                        navController.navigate(Route.Applications.path) {
                            popUpTo(Route.Home.path)
                        }
                    }
                )
            }
            composable(Route.Settings.path) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.News.path) {
                NewsScreen(
                    onBack = { navController.popBackStack() },
                    onOpen = { navController.navigate(Route.NewsDetails.create(it)) }
                )
            }
            composable(
                route = Route.NewsDetails.path,
                arguments = listOf(navArgument("articleId") { type = NavType.StringType })
            ) { entry ->
                NewsDetailScreen(
                    articleId = entry.arguments?.getString("articleId").orEmpty(),
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.Help.path) {
                HelpDonateScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.Chats.path) {
                ChatListScreen(
                    onBack = { navController.popBackStack() },
                    onOpen = { navController.navigate(Route.ChatThread.create(it)) }
                )
            }
            composable(
                route = Route.ChatThread.path,
                arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
            ) { entry ->
                ChatThreadScreen(
                    conversationId = entry.arguments?.getString("conversationId").orEmpty(),
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.Notifications.path) {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.Admin.path) {
                AdminScreen(
                    onBack = { navController.popBackStack() },
                    onOpenApplication = { navController.navigate(Route.AdminApplication.create(it)) },
                    onEditAnimal = { navController.navigate(Route.AnimalEditor.create(it)) },
                    onCreateAnimal = { navController.navigate(Route.AnimalEditor.create("new")) }
                )
            }
            composable(
                route = Route.AdminApplication.path,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType })
            ) { entry ->
                AdminApplicationScreen(
                    applicationId = entry.arguments?.getString("applicationId").orEmpty(),
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
            composable(Route.LostFound.path) {
                LostFoundScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.Veterinary.path) {
                VeterinaryScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
