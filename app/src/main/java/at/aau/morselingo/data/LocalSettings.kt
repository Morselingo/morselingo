package at.aau.morselingo.data
import androidx.compose.runtime.staticCompositionLocalOf

// Holds Settings (could be data class, ViewModel, etc.)
val LocalAppSettings = staticCompositionLocalOf<AppSettings> {
    error("No Settings provided")
}
