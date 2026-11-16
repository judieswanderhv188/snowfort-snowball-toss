package com.snowfort.toss.domain.usecase

import com.snowfort.toss.core.config.GameConfig
import com.snowfort.toss.domain.model.Projectile
import com.snowfort.toss.domain.model.Vec2
import kotlin.math.cos
import kotlin.math.sin

class StepPhysicsUseCase {

    operator fun invoke(projectile: Projectile, seconds: Float, wind: Float): Projectile {
        val velocity = Vec2(
            projectile.velocity.x + wind * seconds,
            projectile.velocity.y + GameConfig.GRAVITY * seconds
        )
        val position = Vec2(
            projectile.position.x + velocity.x * seconds,
            projectile.position.y + velocity.y * seconds
        )
        return projectile.copy(
            position = position,
            velocity = velocity,
            ageMs = projectile.ageMs + (seconds * 1000f).toInt(),
            spin = projectile.spin + seconds * 514f
        )
    }

    fun launchVelocity(power: Float, angleRadians: Float): Vec2 {
        val speed = power.coerceIn(0f, 1f) * GameConfig.MAX_POWER
        return Vec2(cos(angleRadians) * speed, sin(angleRadians) * speed)
    }

    fun trajectory(origin: Vec2, velocity: Vec2, dots: Int, wind: Float): List<Vec2> {
        val step = GameConfig.PREVIEW_STEP_SECONDS
        return (1..dots).map { index ->
            val time = index * step
            Vec2(
                origin.x + velocity.x * time + 0.5f * wind * time * time,
                origin.y + velocity.y * time + 0.5f * GameConfig.GRAVITY * time * time
            )
        }.takeWhile { it.y < 1.05f && it.x < 1.1f }
    }

    fun isOutOfPlay(projectile: Projectile): Boolean =
        projectile.position.y > 1.02f ||
            projectile.position.x > 1.08f ||
            projectile.position.x < -0.08f ||
            projectile.ageMs > GameConfig.MAX_FLIGHT_MS
}
