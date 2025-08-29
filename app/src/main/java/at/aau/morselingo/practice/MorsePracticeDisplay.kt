package at.aau.morselingo.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MorsePracticeDisplay(
    expectedText: String,
    userInput: String,
    viewModel: MorsePracticeDisplayViewmodel = viewModel(factory = MorsePracticeDisplayViewmodelFactory(LocalContext.current))
) {
    val upperExpected = expectedText.uppercase()
    var currentIndex = 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        upperExpected.forEach { char ->
            val morseCode = char.toMorse()
            val morseLength = morseCode?.length!!

            val userSubInput = if (currentIndex + morseLength <= userInput.length) {
                userInput.substring(currentIndex, (currentIndex + morseLength).coerceAtMost(userInput.length))
            } else if (currentIndex < userInput.length) {
                userInput.substring(currentIndex)
            } else {
                ""
            }

            MorseLetter(char, userSubInput)
            currentIndex += morseLength
        }
    }
}

@Composable
fun MorseLetter(letter: Char, morseInputForChar: String) {
    val correctMorseSymbols = morseInputForChar.take(5).mapIndexed { index, ch -> letter.toMorse()?.get(index) == ch }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .width(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = letter.toString(),
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
        )
        HighlightIncorrectText(morseInputForChar, correctMorseSymbols)
    }
}

@Composable
private fun HighlightIncorrectText(text: String, correctness: List<Boolean>) {
    val annotatedString = buildAnnotatedString {
        text.forEachIndexed { index, ch ->
            val color = if (index < correctness.size && correctness[index]) {
                Color.Black
            } else {
                Color.Red
            }

            withStyle(style = SpanStyle(color = color)) {
                append(ch)
            }
        }
    }

    Text(
        text = annotatedString,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        style = TextStyle(letterSpacing = 4.sp),
        textAlign = TextAlign.Center,
    )
}