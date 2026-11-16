package com.snowfort.toss.domain.model

data class Projectile(
    val id: Int,
    val position: Vec2,
    val velocity: Vec2,
    val ageMs: Int = 0,
    val spin: Float = 0f
)
