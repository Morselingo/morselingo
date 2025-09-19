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

// Converts Domain -> Proto
fun AppSettings.toProto(): Settings {
    return Settings.newBuilder()
        .setScoreVisibility(scoreVisibility)
        .clearAllowedChars()
        .addAllAllowedChars(allowedChars)
        .setHintVisibility(hintVisibility)
        .setSimpleInput(simpleInput)
        .setLongTouchTime(longTouchTime)
        .build()
}