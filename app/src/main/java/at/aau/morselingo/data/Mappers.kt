package at.aau.morselingo.data

fun Settings.toDomain(): AppSettings {
    return AppSettings(
        scoreVisibility = scoreVisibility,
        allowedChars = allowedCharsList,
        hintVisibility = hintVisibility,
        simpleInput = simpleInput,
        longTouchTime = longTouchTime
    )
}