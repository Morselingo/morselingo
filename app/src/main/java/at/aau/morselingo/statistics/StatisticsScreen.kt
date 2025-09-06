package at.aau.morselingo.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat
import kotlin.random.Random

@Composable
fun StatisticsScreen() {
    Column(

    ) {
        AggregatedStats(
            averageLetterTime = 1200L,
            totalSymbolsCorrect = 100,
            totalSymbolsAttempted = 200
        )
        CharStatGrid()
    }
}

@Composable
fun CharStatGrid() {
    val characters = listOf("a", "b", "c", "d", "e", "f", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "␣")
    val random = Random(1)

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(characters) { char ->
            CharTile(char, random.nextFloat())
        }
    }
}

@Composable
fun CharTile(char: String, score: Float) {
    val backgroundColor = scoreToColor(score)
    val textColor = if (backgroundColor.isDark()) Color.White else Color.Black

    Card(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1f),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(backgroundColor)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = char.uppercase(),
                color = textColor,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

    }
}

@Composable
fun AggregatedStats(averageLetterTime: Long, totalSymbolsCorrect: Int, totalSymbolsAttempted: Int) {
    val accuracy = if (totalSymbolsAttempted > 0) {
        DecimalFormat("#.##").format(
            (totalSymbolsCorrect.toFloat() / totalSymbolsAttempted) * 100
        )
    } else {
        "0"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Aggregated Stats",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Stat("${averageLetterTime}ms", "Avg Char Time")
                Stat("$accuracy%", "Accuracy")
                Stat(totalSymbolsCorrect.toString(), "# Correct")
                Stat(totalSymbolsAttempted.toString(), "# Attempted")
            }
        }
    }
}

@Composable
fun Stat(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

//TODO: move this to the viewmodel and just return a color
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