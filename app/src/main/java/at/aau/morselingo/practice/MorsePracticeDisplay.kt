package at.aau.morselingo.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LetterData(val letter: Char, val userInput: String)

@Composable
fun MorsePracticeDisplay(
    expectedText: String,
    userInput: String,
) {
    val listState = rememberLazyListState()
    val upperExpected = expectedText.uppercase()

    val letterData = remember(upperExpected, userInput) {
        var currentIndex = 0
        upperExpected.map { char ->
            val morseCode = char.toMorse() ?: ""
            val morseCodeLength = morseCode.length

            val userSubInput = if (currentIndex + morseCodeLength <= userInput.length) {
                userInput.substring(currentIndex, (currentIndex + morseCodeLength).coerceAtMost(userInput.length))
            } else if (currentIndex < userInput.length) {
                userInput.substring(currentIndex)
            } else {
                ""
            }

            currentIndex += morseCodeLength
            LetterData(char, userSubInput)
        }
    }

    val currentLetterIndex = remember(letterData) {
        val nextIndex = letterData.indexOfFirst { it.userInput.length < (it.letter.toMorse()?.length ?: 0) }
        when {
            userInput.isEmpty() -> 0
            nextIndex != -1 && nextIndex > 0 -> nextIndex - 1
            nextIndex == 0 -> 0
            else -> letterData.lastIndex
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        items(letterData) { letter ->
            MorseLetter(letter.letter, letter.userInput)
        }
    }

    LaunchedEffect(userInput) {
        listState.animateScrollToItem(currentLetterIndex)
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