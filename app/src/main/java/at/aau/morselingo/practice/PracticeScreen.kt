package at.aau.morselingo.practice

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun PracticeScreen(
   viewModel: PracticeScreenViewModel = viewModel()
) {
   val text by viewModel.input.collectAsState()
   val letterText by viewModel.inputLetters.collectAsState()
/*
   var text1 by remember { mutableStateOf("Nothing") }
   LaunchedEffect(Unit) {
      viewModel.inputEvents.collect { event ->
         text1 = event
      }
   }
   Text(text1)
   TestPracticeComposable(viewModel.inputEvents)
      MorseInput(viewModel::onEvent )*/
   Column {
      Text(text.ifEmpty { "Nothing" })
      Text(letterText.ifEmpty { "Nothing" })
   }
/*
   MorseInput(
      viewModel::onInput,
      viewModel::preprocessInput
      )
      */
   MorseInput(
      viewModel::onInput,
      viewModel::preprocessInput,
      clickSpeed = 500,
   )

}