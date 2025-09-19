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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.aau.morselingo.data.LocalAppSettings
import java.text.DecimalFormat

@Composable
fun StatisticsScreen(
    viewModel: StatisticsScreenViewModel = viewModel(factory = StatisticsScreenViewModelFactory(
        LocalContext.current
    ))
) {
    val stats: StatisticsData by viewModel.statisticalData.collectAsState()
    Column() {
        AggregatedStats(
            averageLetterTime = stats.averageLetterTime,
            totalSymbolsCorrect = stats.totalSymbolsCorrect,
            totalSymbolsAttempted = stats.totalSymbolsAttempted
        )
        CharStatGrid(chars = stats.letterData.map { it.key to it.value })
    }
}

@Composable
fun CharStatGrid(chars: List<Pair<Char, LetterStats>>) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(chars.sortedBy { it.first }) { (char, stats) ->
            CharTile(char = char.toString(), score = stats.score)
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
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = char.uppercase(),
                color = textColor,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.align(Alignment.Center)
            )
            //TODO: maybe add a toggle for the score
            if(LocalAppSettings.current.scoreVisibility) {
                Text(
                    text = DecimalFormat("0.00").format(score),
                    color = textColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                )
            }
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