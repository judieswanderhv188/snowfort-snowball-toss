package com.snowfort.toss.domain.model

data class PlayerProgress(
    val stars: Int = 0,
    val currentYard: Int = 1,
    val yardsCleared: Int = 0,
    val bestStars: Int = 0,
    val bestAccuracy: Int = 0,
    val fortLayout: List<Int> = List(9) { -1 },
    val ownedMittens: Set<Int> = setOf(0),
    val equippedMitten: Int = 0
)
