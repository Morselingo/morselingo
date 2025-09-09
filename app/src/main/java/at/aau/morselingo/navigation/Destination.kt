package at.aau.morselingo.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

private object Rout {
    const val PRACTICE = "practice"
    const val STATISTICS = "leaderboard"
    const val SETTINGS = "settings"
}

//The order of the destinations is the order in which they are displayed in the navigation
enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
    val displayInNavigation: Boolean,
) {
    STATISTICS(Rout.STATISTICS, "Statistics", Icons.Default.Menu, "Statistics", true),
    PRACTICE(Rout.PRACTICE, "Practice", Icons.Default.PlayArrow, "Practice", true),
    SETTINGS(Rout.SETTINGS, "Settings", Icons.Default.Settings, "Settings", true);

    companion object {
        val defaultDestination = PRACTICE.route
    }
}