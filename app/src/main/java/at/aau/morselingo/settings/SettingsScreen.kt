package at.aau.morselingo.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.aau.morselingo.data.LocalAppSettings
import kotlin.math.roundToInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import at.aau.morselingo.data.AppSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            LocalContext.current
        )
    )
) {
    val settings = LocalAppSettings.current


    ProvideTextStyle(
        value = MaterialTheme.typography.titleMedium.copy(
            fontSize = 18.sp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingGroup {
                PracticeSettings(viewModel, settings)
            }
            SettingGroup {
                InputSettings(viewModel, settings)
            }

            SettingGroup {
                StatisticSettings(viewModel, settings)
            }
        }
    }

}

@Composable
fun SettingGroup(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SettingElement(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val maxWidth = this.maxWidth
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            //horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = maxWidth * 0.7f),
            ) {
                first()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                second()
            }
        }
    }
}

@Composable
fun BigSettingElement(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            first()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            second()
        }
    }
}

@Composable
fun ColumnScope.PracticeSettings(
    viewModel: SettingsViewModel,
    settings: AppSettings
) {
    SettingElement(
        {
            Text("Show Hints")
        },
        {
            Switch(
                checked = settings.hintVisibility,
                onCheckedChange = { viewModel.setHintVisibility(it) }
            )
        }
    )
    val iconSize = 30
    BigSettingElement(
        {
            var textRemove by remember { mutableStateOf(settings.allowedChars.last()) }
            var textRemoveVisible by remember { mutableStateOf(false) }
            var textAdd by remember { mutableStateOf(settings.allowedChars.last()) }
            var textAddVisible by remember { mutableStateOf(false) }
            val currentStyle: TextStyle =
                androidx.compose.material3.LocalTextStyle.current
            val textMeasurer = rememberTextMeasurer()
            var previousAllowedChars by remember { mutableStateOf(settings.allowedChars) }
            val scope = rememberCoroutineScope()
            var addJob by remember { mutableStateOf<Job?>(null) }
            var removeJob by remember { mutableStateOf<Job?>(null) }
            LaunchedEffect(settings.allowedChars.size) {
                if (settings.allowedChars.size > previousAllowedChars.size) {// Added chars
                    addJob?.cancel()
                    addJob = scope.launch {
                        textAdd = settings.allowedChars.last()
                        textAddVisible = true
                        previousAllowedChars = settings.allowedChars
                        delay(1000)
                        textAddVisible = false
                    }
                } else if (settings.allowedChars.size < previousAllowedChars.size) {// Removed chars
                    removeJob?.cancel()
                    removeJob = scope.launch {
                        textRemove = previousAllowedChars[settings.allowedChars.size]
                        textRemoveVisible = true
                        previousAllowedChars = settings.allowedChars
                        delay(1000)
                        textRemoveVisible = false

                    }
                }
            }
            Row(
                modifier = Modifier.width(with(LocalDensity.current) { MaterialTheme.typography.bodyLarge.fontSize.toDp() } + 16.dp) //+padding
            ) {
                AnimatedVisibility(
                    visible = textRemoveVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Text(
                        text = textRemove,
                        modifier = Modifier
                            .padding(start = iconSize.dp / 2 - with(LocalDensity.current) {
                                textMeasurer.measure(
                                    textRemove,
                                    style = currentStyle
                                ).size.width.toDp()
                            } / 2)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Box( // set size of Text, for it to not change when numbers get lower
                modifier = Modifier
                    .width(with(LocalDensity.current) { MaterialTheme.typography.bodyLarge.fontSize.toDp() * 0.6f } * 16)
            ) {
                Text(text = "Letter Count: ${settings.allowedChars.size}")
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.width(with(LocalDensity.current) { MaterialTheme.typography.bodyLarge.fontSize.toDp() } + 16.dp)// + padding
            ) {
                AnimatedVisibility(
                    visible = textAddVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
                    exit = fadeOut() + slideOutVertically(),
                    modifier = Modifier.width(with(LocalDensity.current) { MaterialTheme.typography.bodyLarge.fontSize.toDp() })

                ) {
                    Text(
                        text = textAdd,
                        modifier = Modifier
                            .padding(end = iconSize.dp / 2 - with(LocalDensity.current) {
                                textMeasurer.measure(
                                    textAdd,
                                    style = currentStyle
                                ).size.width.toDp()
                            } / 2)
                    )
                }
            }
        },
        {
            IconButton(
                onClick = {
                    viewModel.removeOneAllowedChars()
                },
                modifier = Modifier
                    .size(iconSize.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Remove 1",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Slider(
                value = settings.allowedChars.size.toFloat(),
                onValueChange = { newValueRaw ->
                    val newValue: Int =
                        newValueRaw.roundToInt() // stay in full numbers
                    viewModel.setAllowedChars(
                        newValue.coerceIn(5, 26)
                    )
                },
                valueRange = 5f..26f,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
            IconButton(
                onClick = {
                    viewModel.addOneAllowedChars()
                },
                modifier = Modifier
                    .size(iconSize.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add 1",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    )
}

@Composable
fun InputSettings(
    viewModel: SettingsViewModel,
    settings: AppSettings
) {
    SettingElement(
        {
            Text("Simple Input Mode")

        },
        {
            Switch(
                checked = settings.simpleInput,
                onCheckedChange = { viewModel.setSimpleInput(it) }
            )
        }
    )
    if (settings.simpleInput) {
        BigSettingElement(
            {
                Box( // set size of Text, for it to not change when numbers get lower
                    modifier = Modifier
                        .width(with(LocalDensity.current) { MaterialTheme.typography.bodyLarge.fontSize.toDp() * 0.6f } * 19)
                ) {
                    Text(text = "Touch Time: ${settings.longTouchTime.toInt()}ms")
                }
            },
            {
                val snapStep = 100
                val snapInterval = 1 / 5f // = 1/5 of snapStep = +-20
                Slider(
                    value = settings.longTouchTime.toFloat(),
                    onValueChange = { newValueRaw ->
                        val newValue: Int =
                            newValueRaw.roundToInt() // stay in full numbers
                        // Optional: round to nearest snapInterval if close enough
                        val remainder = newValue % snapStep
                        val snappedValue =
                            if (remainder < snapStep * snapInterval) {
                                newValue - remainder // lower bound
                            } else if (remainder > snapStep * (1 - snapInterval)) {
                                newValue + (snapStep - remainder) // upper bound
                            } else {
                                newValue
                            }
                        viewModel.setLongTouchTime(
                            snappedValue.coerceIn(0, 3000).toLong()
                        )
                    },
                    valueRange = 0f..2000f
                )
            }
        )
    }
}

@Composable
fun StatisticSettings(
    viewModel: SettingsViewModel,
    settings: AppSettings
) {
    val showDeleteAlertDialog = remember { mutableStateOf(false) }

    SettingElement(
        {
            Text("Show Letter Score")
        },
        {
            Switch(
                checked = settings.scoreVisibility,
                onCheckedChange = { viewModel.setScoreVisibility(it) }
            )
        }
    )

    val title = "Delete All Statistics"
    val label = "Delete Data"
    val explanation =
        "If you confirm, all statistics created on this device about your progress will be reset and lost forever."
    SettingElement(
        first = {
            Text(title)
        },
        second = {
            Button(
                onClick = {
                    showDeleteAlertDialog.value = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp)
                )
            }
        }
    )
    if (showDeleteAlertDialog.value) {
        AlertDialog(
            title = {
                Text(text = title)
            },
            text = {
                Text(text = explanation)
            },
            onDismissRequest = {
                showDeleteAlertDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAlertDialog.value = false
                        viewModel.deleteStatistics()
                    }
                ) {
                    Text(label)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteAlertDialog.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )

    }
}