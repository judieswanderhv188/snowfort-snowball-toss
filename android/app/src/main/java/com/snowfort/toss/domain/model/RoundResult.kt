package com.snowfort.toss.domain.model

data class RoundResult(
    val yard: Int,
    val hits: Int,
    val shots: Int,
    val stars: Int,
    val cleared: Boolean
) {
    val accuracy: Int
        get() = if (shots <= 0) 0 else (hits * 100) / shots
}
