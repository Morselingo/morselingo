package at.aau.morselingo.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val context: Context) {
    val minAllowedChars = 5
    val maxAllowedChars = 26
    val charList: List<String> = listOf("e", "t", "i", "a", "n", "m", "s", "u", "r", "w", "d", "k", "g", "o", "h", "v", "f", "l", "p", "j", "b", "x", "c", "y", "z", "q")
    val appSettingsFlow: Flow<AppSettings> =
        context.settingsDataStore.data.map { it.toDomain() }


    suspend fun setScoreVisibility(visibility: Boolean) {
        context.settingsDataStore.updateData { proto ->
            proto.toBuilder()
                .setScoreVisibility(visibility)
                .build()
        }
    }

    suspend fun setHintVisibility(visibility: Boolean) {
        context.settingsDataStore.updateData { proto ->
            proto.toBuilder()
                .setHintVisibility(visibility)
                .build()
        }
    }

    suspend fun setSimpleInput(simpleInput: Boolean) {
        context.settingsDataStore.updateData { proto ->
            proto.toBuilder()
                .setSimpleInput(simpleInput)
                .build()
        }
    }

    suspend fun setLongTouchTime(longTouchTime: Long) {
        context.settingsDataStore.updateData { proto ->
            proto.toBuilder()
                .setLongTouchTime(longTouchTime)
                .build()
        }
    }

    suspend fun setAllowedChars(amount: Int) {
        require(amount >= 5) { "allowedChars must contain at least 5 characters" } // if ui check fails
        context.settingsDataStore.updateData { proto ->
            proto.toBuilder()
                .clearAllowedChars()
                .addAllAllowedChars(charList.take(amount))
                .build()
        }
    }

    suspend fun addOneAllowedChar() {
        context.settingsDataStore.updateData { proto ->
            val current = proto.allowedCharsList.toMutableList()
            if (current.size >= 26) {
                throw IllegalStateException("Cannot add more characters, AllowedChar is full")
            }
            current.add(charList[current.size])
            proto.toBuilder()
                .clearAllowedChars()
                .addAllAllowedChars(current)
                .build()
        }
    }

    suspend fun removeOneAllowedChar() {
        context.settingsDataStore.updateData { proto ->
            val current = proto.allowedCharsList.toMutableList()
            if (current.size <= minAllowedChars) {
                throw IllegalStateException("Cannot remove more characters, AllowedChar is at minimum: $minAllowedChars")
            }
            current.removeLast()
            proto.toBuilder()
                .clearAllowedChars()
                .addAllAllowedChars(current)
                .build()
        }
    }
}