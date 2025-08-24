package at.aau.morselingo.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MorsePracticeDisplay(expectedText: String, userInput: String) {

    //TODO: make the string length fix

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        expectedText.uppercase().forEach { char ->
            MorseLetter(char, userInput.substring(0, 4))
        }
    }
}

@Composable
fun MorseLetter(letter: Char, userMorseInputForChar: String) {
    val morseMap = mapOf(
        'A' to ".-",    'B' to "-...",  'C' to "-.-.", 'D' to "-..",
        'E' to ".",     'F' to "..-.",  'G' to "--.",  'H' to "....",
        'I' to "..",    'J' to ".---",  'K' to "-.-",  'L' to ".-..",
        'M' to "--",    'N' to "-.",    'O' to "---",  'P' to ".--.",
        'Q' to "--.-",  'R' to ".-.",   'S' to "...",  'T' to "-",
        'U' to "..-",   'V' to "...-",  'W' to ".--",  'X' to "-..-",
        'Y' to "-.--",  'Z' to "--..",
        '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-",
        '5' to ".....", '6' to "-....", '7' to "--...", '8' to "---..",
        '9' to "----.", '0' to "-----"
    )

    val isError = !morseMap[letter]?.startsWith(userMorseInputForChar)!!

    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = letter.toString(),
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = userMorseInputForChar.substring(0, 5),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = if (isError) androidx.compose.ui.graphics.Color.Red
                else androidx.compose.ui.graphics.Color.Black
        )
    }
}