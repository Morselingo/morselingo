package at.aau.morselingo.settings

data class Settings(
    val scoreVisibility: Boolean = false,
    val hintVisibility: Boolean = false,

    val simpleInput: Boolean = true,
    val longTouchTime: Long = 1000L
)