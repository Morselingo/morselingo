package at.aau.morselingo.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import at.aau.morselingo.settings.LocalSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

const val dot = "·"
const val line = "-"
const val spaceSymbol = "␣"
const val wordSeparationTime = 2

@Composable
fun MorseInput(
    onInput: (String) -> Unit
) {
    val settings = LocalSettings.current
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (!settings.simpleInput) {
                MyButton(dot) { onInput(dot) }
                MyButton(spaceSymbol) { onInput(spaceSymbol) }
                MyButton(line) { onInput(line) }
            } else {
                Spacer(modifier = Modifier.weight(2f))
                MorseButton("Tap", 3f, settings.longTouchTime, onInput = onInput)
                Spacer(modifier = Modifier.weight(2f))
            }
        }
    }
}

@Composable
fun RowScope.MyButton(
    text: String,
    onInput: () -> Unit
) {
    Button(
        onClick = onInput,
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors().copy(
            MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
        )
    }

}

@Composable
fun RowScope.MorseButton(
    text: String,
    size: Float,
    longTouchTime: Long,
    onInput: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .weight(size)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(interactionSource = interactionSource, indication = ripple(), onClick = {})
            .pointerInput(Unit) {
                coroutineScope {
                    var timerJob: Job? = null // for timing end of word

                    while (true) {
                        var up: PointerInputChange? = null
                        awaitPointerEventScope {
                            awaitFirstDown()
                            timerJob?.cancel()
                            val duration = measureTimeMillis {
                                up = waitForUpOrCancellation()
                            }
                            if (up == null) {
                                // Handle error
                            }
                            if (duration < longTouchTime) {
                                onInput(dot)
                            } else {
                                onInput(line)
                            }

                            timerJob = launch {
                                delay(longTouchTime * wordSeparationTime)
                                onInput(spaceSymbol)
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
        )
    }

}