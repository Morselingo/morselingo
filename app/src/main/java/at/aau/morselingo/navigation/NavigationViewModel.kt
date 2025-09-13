package at.aau.morselingo.navigation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.aau.morselingo.settings.Settings
import at.aau.morselingo.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class NavigationViewModel(repo: SettingsRepository) : ViewModel() {

    val settings: StateFlow<Settings> = repo.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly, // Always collect data (hot Flow)
        Settings()
    )

}

class NavigationViewModelFactory(current: Context) : ViewModelProvider.Factory {
    private val repository = SettingsRepository(current)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NavigationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NavigationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }
}