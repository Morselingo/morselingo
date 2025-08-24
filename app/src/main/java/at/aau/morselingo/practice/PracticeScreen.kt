package at.aau.morselingo.practice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PracticeScreen(
    viewModel: PracticeScreenViewModel = viewModel()
) {
    val expected = "Hallo"
    var userString by remember { mutableStateOf("") }

    Column {
        MorsePracticeDisplay(expected, userString)

        Row {
            Button(onClick = {
                userString += "-"
            }) { Text("-") }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                userString += "·"
            }) { Text("·") }
        }
    }
}