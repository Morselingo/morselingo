package at.aau.morselingo.practice

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight

@Composable
fun LevelUpDialog(
    visible: Boolean,
    level: Int,
    onDismiss: () -> Unit,
    title: String = "Level up!",
    emojiCodePoint: Int = 0x1F389 // party popper emoji
) {
    if (!visible) return

    val emoji = remember(emojiCodePoint) { String(Character.toChars(emojiCodePoint)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = { Text("You have reached level $level! $emoji") },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Continue") }
        }
    )
}