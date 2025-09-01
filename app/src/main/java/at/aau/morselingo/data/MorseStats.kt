package at.aau.morselingo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class MorseStats(
    val averageTimePerChar: Map<Char, Pair<Double, Int>> = mapOf(), // Char -> Sum of Time for Char, Amount of Chars
    val totalTime: Long = 0L,
    val averageAccuracyPerChar: Map<Char, Pair<Int, Int>> = mapOf(), // Char -> Sum of Accuracies, Amount of Chars
    val totalCorrectSymbols: Int = 0,
    val totalSymbols: Int = 0,
)

@Entity(tableName = "morse_stats")
data class MorseStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val averageTimePerChar: String,
    val totalTime: Long,
    val averageAccuracyPerChar: String,
    val totalCorrectSymbols: Int,
    val totalSymbols: Int
)

fun MorseStats.toEntity() = MorseStatsEntity(
    averageTimePerChar = Gson().toJson(averageTimePerChar),
    totalTime = totalTime,
    averageAccuracyPerChar = Gson().toJson(averageAccuracyPerChar),
    totalCorrectSymbols = totalCorrectSymbols,
    totalSymbols = totalSymbols
)

fun MorseStatsEntity.toDomain() = MorseStats(
    averageTimePerChar = Gson().fromJson(
        averageTimePerChar,
        object : TypeToken<Map<Char, Pair<Double, Int>>>() {}.type
    ),
    totalTime = totalTime,
    averageAccuracyPerChar = Gson().fromJson(
        averageAccuracyPerChar,
        object : TypeToken<Map<Char, Pair<Int, Int>>>() {}.type
    ),
    totalCorrectSymbols = totalCorrectSymbols,
    totalSymbols = totalSymbols
)