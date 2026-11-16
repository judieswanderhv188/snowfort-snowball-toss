package com.snowfort.toss.domain.model

data class Yard(
    val index: Int,
    val targets: List<SnowTarget>,
    val snowballs: Int,
    val previewDots: Int,
    val wind: Float
)
