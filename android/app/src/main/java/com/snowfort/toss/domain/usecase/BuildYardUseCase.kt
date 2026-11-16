package com.snowfort.toss.domain.usecase

import com.snowfort.toss.core.config.GameConfig
import com.snowfort.toss.domain.model.SnowTarget
import com.snowfort.toss.domain.model.Vec2
import com.snowfort.toss.domain.model.Yard
import kotlin.math.min
import kotlin.random.Random

class BuildYardUseCase {

    operator fun invoke(yardIndex: Int): Yard {
        val random = Random(yardIndex * 7919L + 31L)
        val count = min(3 + yardIndex / 12, GameConfig.MAX_TARGETS)
        val lanes = List(count) { slot ->
            val laneStart = 0.18f + slot * (0.74f / count)
            val laneWidth = 0.74f / count
            val x = laneStart + random.nextFloat() * (laneWidth * 0.7f)
            val y = 0.18f + random.nextFloat() * 0.37f
            SnowTarget(
                id = slot,
                position = Vec2(x.coerceIn(0.18f, 0.92f), y.coerceIn(0.18f, 0.55f)),
                phaseMs = random.nextInt(0, 1200)
            )
        }
        val wind = if (yardIndex > GameConfig.WIND_START_YARD) yardIndex * 0.0015f else 0f
        return Yard(
            index = yardIndex,
            targets = lanes,
            snowballs = GameConfig.SNOWBALLS_PER_ROUND,
            previewDots = (18 - yardIndex / 4).coerceAtLeast(4),
            wind = wind
        )
    }
}
