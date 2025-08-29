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
) {
    //CHAT GPT GENERATED FOR DEBUG REASONS
    fun toDebugString(): String {
        val builder = StringBuilder()

        builder.appendLine("📊 Morse Stats Debug")
        builder.appendLine("======================")
        builder.appendLine("Total Symbols: $totalSymbols")
        builder.appendLine("Correct Symbols: $totalCorrectSymbols")
        builder.appendLine("Accuracy Overall: ${if (totalSymbols > 0) "%.2f".format(totalCorrectSymbols * 100.0 / totalSymbols) else "N/A"}%")
        builder.appendLine("Total Time: ${totalTime}ms")
        builder.appendLine()

        builder.appendLine("⏱ Average Time per Char:")
        if (averageTimePerChar.isEmpty()) {
            builder.appendLine("  (no data)")
        } else {
            averageTimePerChar.forEach { (char, pair) ->
                val (sum, count) = pair
                val avgTime = if (count > 0) sum / count else 0.0
                builder.appendLine("  • $char: %.2f ms (count: $count)".format(avgTime))
            }
        }

        builder.appendLine()
        builder.appendLine("🎯 Accuracy per Char:")
        if (averageAccuracyPerChar.isEmpty()) {
            builder.appendLine("  (no data)")
        } else {
            averageAccuracyPerChar.forEach { (char, pair) ->
                val (correct, count) = pair
                val accuracy = if (count > 0) correct * 100.0 / count else 0.0
                builder.appendLine("  • $char: %.2f%% (count: $count)".format(accuracy))
            }
        }

        return builder.toString()
    }
}

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