package at.aau.morselingo.data

data class AppSettings(
    val scoreVisibility: Boolean = false,
    val allowedChars: List<String> = listOf("e", "t", "i", "a", "n"),
    val hintVisibility: Boolean = false,
    val simpleInput: Boolean = true,
    val longTouchTime: Long = 1000L
)