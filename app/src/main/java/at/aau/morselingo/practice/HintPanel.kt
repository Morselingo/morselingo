package at.aau.morselingo.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HintPanel(expectedText: String, currentIndex: Int, initiallyExpanded: Boolean = false) {

    val currentChar: Char? = expectedText.getOrNull(currentIndex)?.uppercaseChar()
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HintHeader(
                expanded = expanded,
                onToggle = { expanded = !expanded }
            )

            AnimatedVisibility(visible = expanded) {
                HintBody(currentChar)
            }
        }
    }
}

@Composable
private fun HintHeader(expanded: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Lightbulb,
            contentDescription = "Hint",
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(22.dp)
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "Hint",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        Spacer(Modifier.weight(1f))

        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun HintBody(currentChar: Char?) {
    Column(Modifier.fillMaxWidth().padding(top = 12.dp)) {
        val hintMessage = buildHintMessage(currentChar)

        Text(
            text = hintMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

private fun buildHintMessage(currentChar: Char?): AnnotatedString {
    val morse = currentChar?.toMorse().orEmpty()

    return buildAnnotatedString {
        if (currentChar != null) {
            if (currentChar == ' ') {
                append("Press the space button!")
            } else {
                append("The letter ")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                ) {
                    append(currentChar.toString())
                }
                append(" is equivalent to ")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                ) {
                    append(morse)
                }
                append(" in morse code!")
            }
        } else {
            append("No character displayed!")
        }
    }
}