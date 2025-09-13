package at.aau.morselingo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore:DataStore<Preferences> by preferencesDataStore("settings")

object SettingsKeys {
    val SCORE_VISIBILITY = booleanPreferencesKey("score_visibility")
    val HINT_VISIBILITY = booleanPreferencesKey("hint_visibility")

    val SIMPLE_INPUT = booleanPreferencesKey("simple_input")
    val LONG_TOUCH_TIME = longPreferencesKey("long_touch_time")
}

class SettingsRepository(private val context: Context) {
    val settingsFlow: Flow<at.aau.morselingo.settings.Settings> = context.dataStore.data.map { prefs ->
        _root_ide_package_.at.aau.morselingo.settings.Settings(
            scoreVisibility = prefs[SettingsKeys.SCORE_VISIBILITY] ?: false,
            hintVisibility = prefs[SettingsKeys.HINT_VISIBILITY] ?: false,
            simpleInput = prefs[SettingsKeys.SIMPLE_INPUT] ?: true,
            longTouchTime = prefs[SettingsKeys.LONG_TOUCH_TIME] ?: 1000L
        )
    }

    suspend fun setScoreVisibility(visibility: Boolean) {
        context.dataStore.edit { it[SettingsKeys.SCORE_VISIBILITY] = visibility }
    }

    suspend fun setHintVisibility(visibility: Boolean) {
        context.dataStore.edit { it[SettingsKeys.HINT_VISIBILITY] = visibility }
    }

    suspend fun setSimpleInput(simpleInput: Boolean) {
        context.dataStore.edit { it[SettingsKeys.SIMPLE_INPUT] = simpleInput }
    }

    suspend fun setLongTouchTime(longTouchTime: Long){
        context.dataStore.edit { it[SettingsKeys.LONG_TOUCH_TIME] = longTouchTime }
    }
}