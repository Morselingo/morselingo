package at.aau.morselingo.practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext

@Composable
fun PracticeScreen(
   viewModel: PracticeScreenViewModel = viewModel(factory = PracticeScreenViewModelFactory(
      LocalContext.current
   ))
) {
   val text by viewModel.userInputForAttempt.collectAsState()

   MorseInput(
      viewModel::onInput,
      clickSpeed = 0,
   )

   MorsePracticeDisplay(viewModel.expectedText, text)
}