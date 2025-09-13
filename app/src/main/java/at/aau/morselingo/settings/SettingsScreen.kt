package at.aau.morselingo.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            LocalContext.current
        )
    )
) {
    val settings = LocalSettings.current
    val showDeleteAlertDialog = remember { mutableStateOf(false) }

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
            }
            SettingGroup(
                {
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
                        SettingElement(
                            {
                                Box( // set size of Text, for it to not change when numbers get lower
                                    modifier = Modifier
                                        .width(with(LocalDensity.current) { MaterialTheme.typography.bodyLarge.fontSize.toDp() * 0.6f } * 11)
                                ) {
                                    Text(text = "Value: ${settings.longTouchTime.toInt()}")
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
                                        val snappedValue = if (remainder < snapStep * snapInterval) {
                                            newValue - remainder // lower bound
                                        } else if (remainder > snapStep * (1-snapInterval)) {
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
            )
            SettingGroup {
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
        content()
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
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                second()
            }
        }
    }
}