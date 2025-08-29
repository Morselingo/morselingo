package at.aau.morselingo.practice

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PracticeScreenViewModel : ViewModel() {
    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input
    private val _inputLetters = MutableStateFlow("")
    val inputLetters: StateFlow<String> = _inputLetters

    fun onInput(symbol: String){
        val newInput = _input.value + symbol
        _input.value = newInput
        Log.d("ViewModel", "added newInput: $symbol")
    }

    fun preprocessInput(symbol: String){
        onInput(symbol)
        val newOutput = _inputLetters.value + decodeMorse(input.value)
        _inputLetters.value = newOutput
        _input.value = ""
    }

    // just temp for idea
    private fun decodeMorse(morse: String): String{
        return if (morse.length == 6) "a" else ""
    }



}