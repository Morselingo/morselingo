package at.aau.morselingo.settings
import androidx.compose.runtime.staticCompositionLocalOf

// Holds Settings (could be data class, ViewModel, etc.)
val LocalSettings = staticCompositionLocalOf<Settings> {
    error("No Settings provided")
}
