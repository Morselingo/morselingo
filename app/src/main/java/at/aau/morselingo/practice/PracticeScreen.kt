package at.aau.morselingo.practice

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PracticeScreen(
   viewModel: PracticeScreenViewModel = viewModel()
) {
   val expected = "Hallo";
   val userInput = ".... -.-.- --.. - --.";
   MorsePracticeDisplay(expected, userInput)
}