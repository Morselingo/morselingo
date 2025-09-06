package at.aau.morselingo.practice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PracticeScreen(
   viewModel: PracticeScreenViewModel = viewModel(factory = PracticeScreenViewModelFactory(
      LocalContext.current
   ))
) {
   val userInputForCurrentAttempt by viewModel.userInputForAttempt.collectAsState()
   val statsForCurrentAttempt by viewModel.stats.collectAsState()
   val currentIndex by viewModel.currentIndex.collectAsState()
   val expectedText by viewModel.expectedText.collectAsState()
   val showHints = true // TODO: get from settings

   // TODO: figure out how to do the reset when the test is done

   MorseInput(
      viewModel::onInput,
      clickSpeed = 0,
   )

   Column(
      modifier = Modifier
         .fillMaxWidth()
         .padding(16.dp),
   ) {
      MorsePracticeDisplay(expectedText, userInputForCurrentAttempt)
      LiveStatsPanel(statsForCurrentAttempt, expectedText)

      if (showHints) {
         HintPanel(expectedText, currentIndex)
      }
   }
}