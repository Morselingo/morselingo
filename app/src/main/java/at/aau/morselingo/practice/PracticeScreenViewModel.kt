package at.aau.morselingo.practice

import android.content.Context
import android.util.Log
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

class PracticeScreenViewModel(
    private val repository: MorseStatsRepository
) : ViewModel() {
    private val _stats: MutableStateFlow<MorseStats> = MutableStateFlow(MorseStats())
    val stats: StateFlow<MorseStats> = _stats.asStateFlow()

    val expectedText = "Test" //TODO: when updating the text, make sure that you reset everything else as well and potentially save

    private val _currentLetterMorseInput = MutableStateFlow("")

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    //For display reasons
    private val _userInputForAttempt = MutableStateFlow("")
    val userInputForAttempt = _userInputForAttempt.asStateFlow()

    var charStartTime: Long = 0L //0 represents that timing has not started



    fun onInput(symbol: String) {
        if (_currentIndex.value >= expectedText.length) {
            //TODO: eventually start a new attempt
            Log.d("CUSTOMLOGGER", "Out of bounds should start new attempt")
            return
        }

        //start timer for first char
        if (charStartTime == 0L) {
            charStartTime = System.currentTimeMillis()
        }

        val newCurrentLetterInput = _currentLetterMorseInput.value + symbol
        _currentLetterMorseInput.value = newCurrentLetterInput

        val newUserInputForAttempt = _userInputForAttempt.value + symbol
        _userInputForAttempt.value = newUserInputForAttempt

        // validation
        val currentChar = expectedText[_currentIndex.value]
        val expectedMorseForCurrentChar = currentChar.toMorse()!! //TODO: better error handling
        val currentInput = _currentLetterMorseInput.value

        if (currentInput.length == expectedMorseForCurrentChar.length) {
            //we know that they should match if it is correct
            val correct = currentInput == expectedMorseForCurrentChar
            completeChar(currentChar, correct)
        }
    }

    fun completeChar(char: Char, isCorrect: Boolean) {
        val timeTaken = System.currentTimeMillis() - charStartTime
        updateStatsForCharCompletion(char, timeTaken, isCorrect)

        Log.d("CUSTOMLOGGER", stats.value.toDebugString())

        charStartTime = System.currentTimeMillis() //TODO: make sure the stats are correct

        _currentLetterMorseInput.value = ""
        _currentIndex.value++
    }

    private fun updateStatsForCharCompletion(char: Char, timeTaken: Long, isCorrect: Boolean) {
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

        _currentIndex.value = 0
        _userInputForAttempt.value = ""
    }

}

class PracticeScreenViewModelFactory(current: Context) : ViewModelProvider.Factory {
    private val database = MorselingoDatabase.getInstance(current)
    private val repository = MorseStatsRepository(database.morseStatsDao())

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PracticeScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PracticeScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }
}