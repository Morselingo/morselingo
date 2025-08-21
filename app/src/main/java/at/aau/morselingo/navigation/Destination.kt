package at.aau.morselingo.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

private object Rout {
    const val PRACTICE = "practice"
    const val LEADERBOARD = "leaderboard"
    const val CHAT = "chat"
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
    LEADERBOARD(Rout.LEADERBOARD, "Leaderboard", Icons.Default.Menu, "Leaderboard", true),
    PRACTICE(Rout.PRACTICE, "Practice", Icons.Default.PlayArrow, "Practice", true),
    CHAT(Rout.CHAT, "Chat", Icons.Default.MailOutline, "Chat", true),
    SETTINGS(Rout.SETTINGS, "Settings", Icons.Default.Settings, "Settings", false);

    companion object {
        val defaultDestination = PRACTICE.route
    }
}