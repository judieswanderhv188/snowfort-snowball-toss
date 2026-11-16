package com.snowfort.toss.domain.model

data class SnowTarget(
    val id: Int,
    val position: Vec2,
    val phaseMs: Int,
    val hidden: Boolean = false,
    val knocked: Boolean = false
)
