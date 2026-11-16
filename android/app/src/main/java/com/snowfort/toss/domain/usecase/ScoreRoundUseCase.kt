package com.snowfort.toss.domain.usecase

import com.snowfort.toss.core.config.GameConfig
import com.snowfort.toss.domain.model.PlayerProgress
import com.snowfort.toss.domain.model.RoundResult
import kotlin.math.max
import kotlin.math.min

class ScoreRoundUseCase {

    operator fun invoke(
        yard: Int,
        hits: Int,
        shots: Int,
        snowballsLeft: Int,
        cleared: Boolean
    ): RoundResult {
        val stars = when {
            cleared && snowballsLeft >= 2 -> 3
            cleared -> 2
            hits > 0 -> 1
            else -> 0
        }
        return RoundResult(yard = yard, hits = hits, shots = shots, stars = stars, cleared = cleared)
    }

    fun applyTo(progress: PlayerProgress, result: RoundResult): PlayerProgress {
        val nextYard = if (result.cleared) {
            min(progress.currentYard + 1, GameConfig.TOTAL_YARDS)
        } else {
            progress.currentYard
        }
        return progress.copy(
            stars = progress.stars + result.stars,
            currentYard = nextYard,
            yardsCleared = if (result.cleared) progress.yardsCleared + 1 else progress.yardsCleared,
            bestStars = max(progress.bestStars, result.stars),
            bestAccuracy = max(progress.bestAccuracy, result.accuracy)
        )
    }
}
