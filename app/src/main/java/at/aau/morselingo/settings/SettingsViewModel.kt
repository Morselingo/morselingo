package at.aau.morselingo.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.aau.morselingo.data.MorseStatsRepository
import at.aau.morselingo.data.MorselingoDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: at.aau.morselingo.data.SettingsRepository, private val statsRepository: MorseStatsRepository) : ViewModel() {

    val settings: StateFlow<Settings> = settingsRepository.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly, // Always collect data (hot Flow)
        Settings()
    )

    fun setScoreVisibility(visibility: Boolean) {
        viewModelScope.launch { settingsRepository.setScoreVisibility(visibility) }
    }

    fun setHintVisibility(visibility: Boolean) {
        viewModelScope.launch { settingsRepository.setHintVisibility(visibility) }
    }

    fun setSimpleInput(simpleInput: Boolean) {
        viewModelScope.launch { settingsRepository.setSimpleInput(simpleInput) }
    }

    fun setLongTouchTime(longTouchTime: Long){
        viewModelScope.launch { settingsRepository.setLongTouchTime(longTouchTime) }
    }

    fun deleteStatistics(){
        viewModelScope.launch { statsRepository.deleteAllData() }
    }


}

class SettingsViewModelFactory(current: Context) : ViewModelProvider.Factory {
    private val settingsRepository =
        _root_ide_package_.at.aau.morselingo.data.SettingsRepository(current)
    private val database = MorselingoDatabase.getInstance(current)
    private val statsRepository = MorseStatsRepository(database.morseStatsDao())

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsRepository, statsRepository) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }
}