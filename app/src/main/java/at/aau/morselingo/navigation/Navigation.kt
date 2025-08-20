package at.aau.morselingo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import at.aau.morselingo.chat.ChatScreen
import at.aau.morselingo.leaderboard.LeaderBoardScreen
import at.aau.morselingo.practice.PracticeScreen
import at.aau.morselingo.settings.SettingsScreen

@Composable
public fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.PRACTICE,
        modifier = modifier
    ) {
        composable(Destinations.PRACTICE) { PracticeScreen() }
        composable(Destinations.CHAT) { ChatScreen() }
        composable(Destinations.LEADERBOARD) { LeaderBoardScreen() }
        composable(Destinations.SETTINGS) { SettingsScreen() }
    }
}