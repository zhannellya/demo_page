package com.adoptme.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adoptme.app.ui.screens.application.AdoptionApplicationScreen
import com.adoptme.app.ui.screens.animals.AnimalsScreen
import com.adoptme.app.ui.screens.auth.LoginScreen
import com.adoptme.app.ui.screens.auth.SignUpScreen
import com.adoptme.app.ui.screens.details.AnimalDetailsScreen
import com.adoptme.app.ui.screens.favorites.FavoritesScreen
import com.adoptme.app.ui.screens.help.HelpDonateScreen
import com.adoptme.app.ui.screens.home.HomeScreen
import com.adoptme.app.ui.screens.lostfound.LostFoundScreen
import com.adoptme.app.ui.screens.map.MapScreen
import com.adoptme.app.ui.screens.notifications.NotificationsScreen
import com.adoptme.app.ui.screens.onboarding.OnboardingScreen
import com.adoptme.app.ui.screens.profile.ProfileScreen
import com.adoptme.app.ui.screens.splash.SplashScreen
import com.adoptme.app.ui.screens.veterinary.VeterinaryScreen
import com.adoptme.app.ui.theme.BottomBarShape
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextMuted
import java.net.URLDecoder

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
        BottomItem(Route.Home.path, "Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomItem(Route.Animals.path, "Animals", Icons.Filled.Pets, Icons.Outlined.Pets),
        BottomItem(Route.Favorites.path, "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        BottomItem(Route.Map.path, "Map", Icons.Filled.Map, Icons.Outlined.Map),
        BottomItem(Route.Profile.path, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
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
                                    popUpTo(navController.graph.findStartDestination().id) {
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
                                            .size(40.dp)
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
                                    color = if (selected) PurplePrimary else TextMuted
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PinkNeon.copy(alpha = 0.08f)
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
                        !loggedIn -> Route.SignUp.path
                        else -> Route.Home.path
                    }
                    navController.navigate(target) {
                        popUpTo(Route.Splash.path) { inclusive = true }
                    }
                }
            }
            composable(Route.Onboarding.path) {
                OnboardingScreen {
                    navController.navigate(Route.SignUp.path) {
                        popUpTo(Route.Onboarding.path) { inclusive = true }
                    }
                }
            }
            composable(Route.SignUp.path) {
                SignUpScreen(
                    onSignedUp = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.SignUp.path) { inclusive = true }
                        }
                    },
                    onGoLogin = { navController.navigate(Route.Login.path) }
                )
            }
            composable(Route.Login.path) {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Login.path) { inclusive = true }
                        }
                    },
                    onGoSignUp = { navController.popBackStack() }
                )
            }
            composable(Route.Home.path) {
                HomeScreen(
                    onOpenAnimal = { navController.navigate(Route.AnimalDetails.create(it)) },
                    onSeeAll = {
                        navController.navigate(Route.Animals.path) {
                            launchSingleTop = true
                        }
                    },
                    onNotifications = { navController.navigate(Route.Notifications.path) },
                    onLostFound = { navController.navigate(Route.LostFound.path) },
                    onHelp = { navController.navigate(Route.HelpDonate.path) },
                    onVeterinary = { navController.navigate(Route.Veterinary.path) }
                )
            }
            composable(Route.Animals.path) {
                AnimalsScreen(onOpenAnimal = { navController.navigate(Route.AnimalDetails.create(it)) })
            }
            composable(Route.Favorites.path) {
                FavoritesScreen(
                    onOpenAnimal = { navController.navigate(Route.AnimalDetails.create(it)) },
                    onBrowse = {
                        navController.navigate(Route.Animals.path) { launchSingleTop = true }
                    }
                )
            }
            composable(Route.Map.path) { MapScreen() }
            composable(Route.Profile.path) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Route.Login.path) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNotifications = { navController.navigate(Route.Notifications.path) },
                    onHelp = { navController.navigate(Route.HelpDonate.path) },
                    onLostFound = { navController.navigate(Route.LostFound.path) },
                    onVeterinary = { navController.navigate(Route.Veterinary.path) }
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
                val name = URLDecoder.decode(entry.arguments?.getString("animalName").orEmpty(), "UTF-8")
                AdoptionApplicationScreen(
                    animalId = id,
                    animalName = name,
                    onBack = { navController.popBackStack() },
                    onSubmitted = {
                        navController.navigate(Route.Profile.path) {
                            popUpTo(Route.Home.path)
                        }
                    }
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
