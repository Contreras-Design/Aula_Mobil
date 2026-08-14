package com.itespf.aulamovil.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.itespf.aulamovil.AppContainer
import com.itespf.aulamovil.ViewModelFactory
import com.itespf.aulamovil.ui.screens.AttendanceScreen
import com.itespf.aulamovil.ui.screens.GradesScreen
import com.itespf.aulamovil.ui.screens.LoginScreen
import com.itespf.aulamovil.ui.screens.ProfileScreen

private object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val GRADES = "grades"
    const val ATTENDANCE = "attendance"
}

@Composable
fun AulaMovilNavGraph(
    appContainer: AppContainer,
    startLoggedIn: Boolean,
    darkModeEnabled: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val navController = rememberNavController()
    val factory = ViewModelFactory(appContainer)

    NavHost(
        navController = navController,
        startDestination = if (startLoggedIn) Routes.HOME else Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                factory = factory,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeWithBottomBar(
                factory = factory,
                appContainer = appContainer,
                darkModeEnabled = darkModeEnabled,
                onToggleDarkMode = onToggleDarkMode,
                onSessionExpiredOrLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun HomeWithBottomBar(
    factory: ViewModelFactory,
    appContainer: AppContainer,
    darkModeEnabled: Boolean,
    onToggleDarkMode: () -> Unit,
    onSessionExpiredOrLoggedOut: () -> Unit
) {
    val innerNav = rememberNavController()

    val items = listOf(
        Triple(Routes.GRADES, "Calificaciones", Icons.Filled.Grade),
        Triple(Routes.ATTENDANCE, "Asistencia", Icons.Filled.CalendarMonth),
        Triple(Routes.PROFILE, "Perfil", Icons.Filled.AccountCircle)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by innerNav.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                items.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                        onClick = {
                            innerNav.navigate(route) {
                                popUpTo(innerNav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNav,
            startDestination = Routes.GRADES,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.GRADES) {
                GradesScreen(factory = factory, onSessionExpired = onSessionExpiredOrLoggedOut)
            }
            composable(Routes.ATTENDANCE) {
                AttendanceScreen(factory = factory, onSessionExpired = onSessionExpiredOrLoggedOut)
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    factory = factory,
                    darkModeEnabled = darkModeEnabled,
                    onToggleDarkMode = onToggleDarkMode,
                    appContainer = appContainer,
                    onLoggedOut = onSessionExpiredOrLoggedOut
                )
            }
        }
    }
}