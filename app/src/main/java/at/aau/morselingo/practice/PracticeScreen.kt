package at.aau.morselingo.practice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import at.aau.morselingo.data.LocalAppSettings

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
   val showHints = LocalAppSettings.current.hintVisibility

   var showLevelUp by rememberSaveable { mutableStateOf(false) }
   var reachedLevel by rememberSaveable { mutableIntStateOf(1) }

   LaunchedEffect(viewModel) {
      viewModel.events.collect { e ->
         if (e is PracticeScreenViewModel.UiEvent.LevelUp) {
            reachedLevel = e.level
            showLevelUp = true
         }
      }
   }

   // TODO: figure out how to do the reset when the test is done

   MorseInput(
      viewModel::onInput
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

   LevelUpDialog(
      visible = showLevelUp,
      level = reachedLevel,
      onDismiss = { showLevelUp = false }
   )
}