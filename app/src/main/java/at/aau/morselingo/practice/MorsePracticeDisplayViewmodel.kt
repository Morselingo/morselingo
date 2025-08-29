package at.aau.morselingo.practice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.aau.morselingo.data.MorseStats
import at.aau.morselingo.data.MorseStatsRepository
import at.aau.morselingo.data.MorselingoDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MorsePracticeDisplayViewmodel(
    private val repository: MorseStatsRepository
) : ViewModel() {
    private val _stats: MutableStateFlow<MorseStats> = MutableStateFlow(MorseStats())
    val stats: StateFlow<MorseStats> = _stats.asStateFlow()

    fun onCharCompleted(char: Char, timeTaken: Long, isCorrect: Boolean) {
        val current = _stats.value

        val newTimeMap = current.averageTimePerChar.toMutableMap().apply {
            val (sum, count) = getOrDefault(char, 0.0 to 0)
            this[char] = (sum + timeTaken) to (count + 1)
        }

        val newAccuracyMap = current.averageAccuracyPerChar.toMutableMap().apply {
            val (sum, count) = getOrDefault(char, 0 to 0)
            this[char] = (sum + if (isCorrect) 1 else 0) to (count + 1)
        }

        val newTotalTime = current.totalTime + timeTaken
        val newTotalCorrect = current.totalCorrectSymbols + if (isCorrect) 1 else 0
        val newTotalSymbols = current.totalSymbols + 1

        _stats.value = MorseStats(
            averageTimePerChar = newTimeMap,
            totalTime = newTotalTime,
            averageAccuracyPerChar = newAccuracyMap,
            totalCorrectSymbols = newTotalCorrect,
            totalSymbols = newTotalSymbols
        )
    }

    fun saveStatsAndReset() {
        viewModelScope.launch {
            repository.insertStats(_stats.value)
            _stats.value = MorseStats()
        }
    }
}

class MorsePracticeDisplayViewmodelFactory(current: Context) : ViewModelProvider.Factory {
    private val database = MorselingoDatabase.getInstance(current)
    private val repository = MorseStatsRepository(database.morseStatsDao())

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MorsePracticeDisplayViewmodel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MorsePracticeDisplayViewmodel(repository) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }
}