package com.snowfort.toss.domain.model

data class Puff(
    val id: Int,
    val position: Vec2,
    val ageMs: Int,
    val strong: Boolean
)
