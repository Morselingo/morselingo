package at.aau.morselingo.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import at.aau.morselingo.statistics.StatisticsScreen
import at.aau.morselingo.practice.PracticeScreen
import at.aau.morselingo.settings.LocalSettings
import at.aau.morselingo.settings.SettingsScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: NavigationViewModel = viewModel(factory = NavigationViewModelFactory(
        LocalContext.current
    ))
) {
    val settings by viewModel.settings.collectAsState()

    CompositionLocalProvider(LocalSettings provides settings) {
        NavHost(
            navController = navController,
            startDestination = Destination.defaultDestination,
            modifier = modifier
        ) {
            composable(Destination.PRACTICE.route) { PracticeScreen() }
            composable(Destination.STATISTICS.route) { StatisticsScreen() }
            composable(Destination.SETTINGS.route) { SettingsScreen() }
        }
}

@Composable
fun AppNavBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // keep track of which Navigation Bar route was last visited to keep it highlighted
    var lastNavigationRoute by rememberSaveable { mutableStateOf(Destination.defaultDestination) }
    val isBottomBarRoute =
        Destination.entries.firstOrNull { it.route == currentRoute }?.displayInNavigation == true
    if (isBottomBarRoute && currentRoute != null) {
        lastNavigationRoute = currentRoute
    }

    NavigationBar(
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        Destination.entries
            .filter { it.displayInNavigation }
            .forEachIndexed { index, destination ->
                NavigationBarItem(
                    selected = lastNavigationRoute == destination.route,
                    onClick = {
                        if (currentRoute != destination.route) {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            destination.icon,
                            contentDescription = destination.contentDescription,
                        )
                    },
                    label = {
                        Text(destination.label)
                    }
                )
            }
    }
}