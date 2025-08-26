package at.aau.morselingo.practice

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

const val dot = "·"
const val line = "-"
const val space = " "
const val spaceSymbol = "␣"
const val wordSeparationTime = 2

@Composable
fun MorseInput(
    onInput: (String) -> Unit,
    onInputProcess: (String) -> Unit,
    clickSpeed: Long = 0, //longTouchTime
) {

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (clickSpeed == 0L) {
                    MyButton(dot, 2f) { onInput(dot) }
                    MyButton(spaceSymbol, 3f) { onInput(space) }
                    MyButton(line, 2f) { onInput(line) }
                }else{
                    Spacer(modifier = Modifier.weight(2f))
                    MorseButton("test", 3f, clickSpeed, onInput = onInput, onInputProcess = onInput)
                    Spacer(modifier = Modifier.weight(2f))
                }
            }
        }
}

@Composable
fun RowScope.MyButton(
    text: String,
    size: Float,
    onInput: () -> Unit
    ){
    Button(
        onClick = onInput,
        modifier = Modifier
            .weight(size)
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp)
    ){
        Text(
            text,
            style = MaterialTheme.typography.titleMedium
        )
    }

}

@Composable
fun RowScope.MorseButton(
    text: String,
    size: Float,
    longTouchTime: Long,
    onInput: (String) -> Unit,
    onInputProcess: (String) -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .weight(size)
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.primary)
            .clickable( interactionSource = interactionSource, indication = ripple(), onClick = {} )
            .pointerInput(Unit){
                coroutineScope {
                    var timerJob: Job? = null // for timing end of word
                    Log.d("MorseButton", "Button initialized?")

                    while(true){
                    var up: PointerInputChange? = null
                    awaitPointerEventScope {
                        awaitFirstDown()
                        Log.d("MorseButton", "Button Pressed: Down")
                        timerJob?.cancel()
                        val duration = measureTimeMillis {
                            up = waitForUpOrCancellation()
                        }
                        Log.d("MorseButton", "Button Pressed: Up")
                        if (up == null) {
                            // Handle error
                        }
                        if (duration < longTouchTime) {
                            onInput(dot)
                            Log.d("MorseButton", "Send dot")
                        } else {
                            onInput(line)
                            Log.d("MorseButton", "Send line")
                        }

                        timerJob = launch {
                            delay(longTouchTime * wordSeparationTime)
                            Log.d("MorseButton", "Delay finished: "+longTouchTime*wordSeparationTime)
                            onInputProcess(space)
                            Log.d("MorseButton", "send space")
                        }
                    }
                    }
                }
            },
    contentAlignment = Alignment.Center
    ){
        Text(
            text,
            style = MaterialTheme.typography.titleMedium
        )
    }

}