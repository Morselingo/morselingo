package at.aau.morselingo.practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

@Composable
fun PracticeScreen(
   viewModel: PracticeScreenViewModel = viewModel()
) {
   val text by viewModel.input.collectAsState()
   val letterText by viewModel.inputLetters.collectAsState()

   MorseInput(
      viewModel::onInput,
      viewModel::preprocessInput,
      clickSpeed = 0,
   )

   MorsePracticeDisplay("Hallo World", text)
}