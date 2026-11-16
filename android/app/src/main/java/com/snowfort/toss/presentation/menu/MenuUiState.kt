package com.snowfort.toss.presentation.menu

data class MenuUiState(
    val stars: Int = 0,
    val currentYard: Int = 1,
    val totalYards: Int = 60,
    val bestStars: Int = 0,
    val bestAccuracy: Int = 0,
    val loaded: Boolean = false
) {
    val progress: Float
        get() = (currentYard.toFloat() / totalYards.toFloat()).coerceIn(0f, 1f)

    val showStats: Boolean
        get() = bestStars > 0 && bestAccuracy > 0
}
