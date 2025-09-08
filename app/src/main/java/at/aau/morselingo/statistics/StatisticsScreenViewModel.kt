package at.aau.morselingo.statistics

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.aau.morselingo.data.MorseStats
import at.aau.morselingo.data.MorseStatsRepository
import at.aau.morselingo.data.MorselingoDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class LetterStats(
    val averageAccuracy: Float,
    val averageTimeTaken: Long,
    val timeForRecentAttempts: List<Long>,
    val accuracyForRecentAttempts: List<Float>,
    val score: Float,
)

data class StatisticsData(
    val averageLetterTime: Long,
    val totalSymbolsCorrect: Int,
    val totalSymbolsAttempted: Int,
    val letterData: Map<Char, LetterStats>,
)

class StatisticsScreenViewModel(
    repository: MorseStatsRepository
) : ViewModel() {

    val statisticalData = repository.getStats()
        .map { it.toStatisticalData() }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsData(0L, 0, 0, emptyMap())
    )
}

fun calculateScore(
    correctSymbols: Int,
    totalSymbols: Int,
    avgTime: Long,
    idealTime: Long = 2000L,
    weightAccuracy: Float = 0.7f,
    weightTime: Float = 0.3f
): Float {
    if (totalSymbols == 0) return 0f

    val accuracyScore = correctSymbols.toFloat() / totalSymbols
    val timeScore = 1f / (1f + (avgTime.toFloat() / idealTime.toFloat()))

    return (weightAccuracy * accuracyScore) + (weightTime * timeScore)
}

fun scoreToColor(score: Float): Color {
    val clamped = score.coerceIn(0f, 1f)
    val green = Color(0xFF4CAF50)
    val yellow = Color(0xFFFFEB3B)
    val red = Color(0xFFF44336)

    return when {
        clamped <= 0.5f -> lerp(green, yellow, clamped / 0.5f)
        else -> lerp(yellow, red, (clamped - 0.5f) / 0.5f)
    }
}

fun Color.isDark(): Boolean {
    val luminance = 0.299 * red + 0.587 * green + 0.114 * blue
    return luminance < 0.5
}

private fun List<MorseStats>.toStatisticalData(
    recencyLimit: Int = 5
): StatisticsData {
    if (isEmpty()) {
        return StatisticsData(
            averageLetterTime = 0L,
            totalSymbolsCorrect = 0,
            totalSymbolsAttempted = 0,
            letterData = emptyMap()
        )
    }

    val groupedAttempts = mutableMapOf<Char, MutableList<MorseStats>>()
    for (attempt in this) {
        for ((char, _) in attempt.averageTimePerChar) {
            groupedAttempts.getOrPut(char) { mutableListOf() }.add(attempt)
        }
    }

    val letterData: Map<Char, LetterStats> = groupedAttempts.mapValues { (char, attempts) ->
        val totalTime = attempts.sumOf { it.averageTimePerChar[char]?.first?.toLong() ?: 0L }
        val totalCount = attempts.sumOf { it.averageTimePerChar[char]?.second ?: 0 }
        val avgTime = if (totalCount > 0) totalTime / totalCount else 0L

        val totalCorrect = attempts.sumOf { it.averageAccuracyPerChar[char]?.first ?: 0 }
        val totalAttempts = attempts.sumOf { it.averageAccuracyPerChar[char]?.second ?: 0 }
        val avgAccuracy = if (totalAttempts > 0) totalCorrect.toFloat() / totalAttempts else 0f

        val recentAttempts = attempts.takeLast(recencyLimit)
        val timeForRecent = recentAttempts.map { it.averageTimePerChar[char]?.first?.toLong() ?: 0L }
        val accuracyForRecent = recentAttempts.map {
            val pair = it.averageAccuracyPerChar[char]
            if (pair != null && pair.second > 0) {
                pair.first.toFloat() / pair.second
            } else 0f
        }

        LetterStats(
            averageAccuracy = avgAccuracy,
            averageTimeTaken = avgTime,
            timeForRecentAttempts = timeForRecent,
            accuracyForRecentAttempts = accuracyForRecent,
            score = calculateScore(totalCorrect, totalAttempts, avgTime)
        )
    }

    val totalSymbolsAttempted = sumOf { it.totalSymbols }
    val totalSymbolsCorrect = sumOf { it.totalCorrectSymbols }
    val totalTime = sumOf { it.totalTime }
    val avgLetterTime = if (totalSymbolsAttempted > 0) totalTime / totalSymbolsAttempted else 0L

    return StatisticsData(
        averageLetterTime = avgLetterTime,
        totalSymbolsCorrect = totalSymbolsCorrect,
        totalSymbolsAttempted = totalSymbolsAttempted,
        letterData = letterData
    )
}

class StatisticsScreenViewModelFactory(current: Context) : ViewModelProvider.Factory {
    private val database = MorselingoDatabase.getInstance(current)
    private val repository = MorseStatsRepository(database.morseStatsDao())

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }
}