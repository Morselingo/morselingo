package at.aau.morselingo.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore:DataStore<Preferences> by preferencesDataStore("settings")

object SettingsKeys {
    val USERNAME = stringPreferencesKey("username")
    val DIFFICULTY = intPreferencesKey("difficulty")
    val MORSE_BUTTONS = stringPreferencesKey("morse_buttons")
}

class SettingsRepository(private val context: Context) {
    val settingsFlow: Flow<Settings> = context.dataStore.data.map { prefs ->
        Settings(
            username = prefs[SettingsKeys.USERNAME] ?: "",
            score = prefs[SettingsKeys.DIFFICULTY] ?: 1,
            themeChoice = prefs[SettingsKeys.MORSE_BUTTONS] ?: ""
        )
    }

    suspend fun setUsername(name: String) {
        context.dataStore.edit { it[SettingsKeys.USERNAME] = name }
    }

    suspend fun setDifficulty(level: Int) {
        context.dataStore.edit { it[SettingsKeys.DIFFICULTY] = level }
    }

    suspend fun setMorseButtonCount(count: String) {
        context.dataStore.edit { it[SettingsKeys.MORSE_BUTTONS] = count }
    }
}