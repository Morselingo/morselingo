package at.aau.morselingo.practice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.aau.morselingo.data.MorseStats
import at.aau.morselingo.data.MorseStatsRepository
import at.aau.morselingo.data.MorselingoDatabase
import at.aau.morselingo.trainingdata.TrainingWordsGenerator
import at.aau.morselingo.trainingdata.WordsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PracticeScreenViewModel(
    private val repository: MorseStatsRepository,
    private val wordsGenerator: TrainingWordsGenerator
) : ViewModel() {
    private val _stats: MutableStateFlow<MorseStats> = MutableStateFlow(MorseStats())
    val stats: StateFlow<MorseStats> = _stats.asStateFlow()

    private val _level = MutableStateFlow(0)
    val level: StateFlow<Int> = _level.asStateFlow()

    sealed class UiEvent {
        data class LevelUp(val level: Int) : UiEvent()
    }
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    private val _expectedText = MutableStateFlow("-")
    val expectedText: StateFlow<String> = _expectedText.asStateFlow()

    private val _currentLetterMorseInput = MutableStateFlow("")

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    // For display reasons
    private val _userInputForAttempt = MutableStateFlow("")
    val userInputForAttempt = _userInputForAttempt.asStateFlow()

    var charStartTime: Long = 0L // 0 represents that timing has not started
    var lang = "en"
    var allowedChars = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")

    init {
        reload()
    }

    fun reload() {
        viewModelScope.launch {
            resetInputState()

            _level.value++

            if (_level.value != 1) {
                _events.emit(UiEvent.LevelUp(_level.value))
            }

            val text = getTrainingWords(_level.value, lang, allowedChars)
            _expectedText.value = text
        }
    }

    private fun resetInputState() {
        _expectedText.value = "-"
        _currentIndex.value = 0
        _userInputForAttempt.value = ""
        _currentLetterMorseInput.value = ""
        charStartTime = 0L
        saveStatsAndReset()
    }

    fun onInput(symbol: String) {
        val text = expectedText.value
        if (text.isEmpty() || _currentIndex.value >= text.length - 1) {
            reload()
            return
        }

        // start timer for first char
        if (charStartTime == 0L) {
            charStartTime = System.currentTimeMillis()
        }

        val newCurrentLetterInput = _currentLetterMorseInput.value + symbol
        _currentLetterMorseInput.value = newCurrentLetterInput

        val newUserInputForAttempt = _userInputForAttempt.value + symbol
        _userInputForAttempt.value = newUserInputForAttempt

        // validation
        val currentChar = text[_currentIndex.value]
        val expectedMorseForCurrentChar = currentChar.toMorse()!! //TODO: better error handling
        val currentInput = _currentLetterMorseInput.value

        if (currentInput.length == expectedMorseForCurrentChar.length) {
            // we know that they should match if it is correct
            val correct = currentInput == expectedMorseForCurrentChar
            completeChar(currentChar, correct)
        }
    }

    fun completeChar(char: Char, isCorrect: Boolean) {
        val timeTaken = System.currentTimeMillis() - charStartTime
        updateStatsForCharCompletion(char, timeTaken, isCorrect)

        charStartTime = System.currentTimeMillis()

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

    private suspend fun getTrainingWords(level: Int, lang: String, allowedChars: List<String>): String {
        val words = wordsGenerator.generate(
            level = level,
            lang = lang,
            allowedChars = allowedChars
        )
        return words.joinToString(" ")
    }
}

class PracticeScreenViewModelFactory(current: Context) : ViewModelProvider.Factory {
    private val database = MorselingoDatabase.getInstance(current)
    private val repository = MorseStatsRepository(database.morseStatsDao())

    private val wordsRepository = WordsRepository(current)

    private val wordsGenerator = TrainingWordsGenerator(wordsRepository)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PracticeScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PracticeScreenViewModel(repository, wordsGenerator) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }
}