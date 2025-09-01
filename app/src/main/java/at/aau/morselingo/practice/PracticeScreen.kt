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

   MorseInput(
      viewModel::onInput,
      clickSpeed = 0,
   )

   Column(
      modifier = Modifier
         .fillMaxWidth()
         .padding(16.dp),
   ) {
      MorsePracticeDisplay(viewModel.expectedText, userInputForCurrentAttempt)
      LiveStatsPanel(statsForCurrentAttempt, viewModel.expectedText)
   }
}