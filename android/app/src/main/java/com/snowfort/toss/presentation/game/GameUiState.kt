package com.snowfort.toss.presentation.game

import com.snowfort.toss.core.config.GameConfig
import com.snowfort.toss.domain.model.Projectile
import com.snowfort.toss.domain.model.Puff
import com.snowfort.toss.domain.model.SnowTarget
import com.snowfort.toss.domain.model.Vec2

enum class GamePhase {
    AIMING,
    CHARGING,
    FLYING,
    RESOLVING,
    FINISHED
}

data class GameUiState(
    val yard: Int = 1,
    val phase: GamePhase = GamePhase.AIMING,
    val targets: List<SnowTarget> = emptyList(),
    val projectiles: List<Projectile> = emptyList(),
    val puffs: List<Puff> = emptyList(),
    val previewDots: List<Vec2> = emptyList(),
    val snowballsLeft: Int = GameConfig.SNOWBALLS_PER_ROUND,
    val hits: Int = 0,
    val shots: Int = 0,
    val streak: Int = 0,
    val frost: Int = 0,
    val power: Float = 0f,
    val angle: Float = 0f,
    val aimActive: Boolean = false,
    val paused: Boolean = false,
    val arcAssist: Boolean = true
) {
    val tripleReady: Boolean
        get() = frost >= GameConfig.FROST_SEGMENTS

    val canThrow: Boolean
        get() = phase == GamePhase.AIMING && snowballsLeft > 0 && !paused

    val remainingTargets: Int
        get() = targets.count { !it.knocked }
}
