package com.snowfort.toss.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val TICK_MS = 16L
    const val TICK_SECONDS = 0.016f
    const val SNOWBALLS_PER_ROUND = 6
    const val TOTAL_YARDS = 60
    const val FROST_SEGMENTS = 3
    const val MAX_TARGETS = 5
    const val GRAVITY = 1.7f
    const val MAX_POWER = 2.2f
    const val DRAG_MAX_NORM = 0.45f
    const val DRAG_MAX_RADIUS_DP = 180
    const val ORIGIN_X = 0.18f
    const val ORIGIN_Y = 0.86f
    const val BALL_RADIUS = 0.038f
    const val TARGET_RADIUS = 0.072f
    const val PREVIEW_STEP_SECONDS = 0.07f
    const val RESOLVE_MS = 320
    const val DEFAULT_POWER = 0.74f
    const val DEFAULT_ANGLE_DEG = -52f
    const val TRIPLE_SPREAD_DEG = 9f
    const val WIND_START_YARD = 20
    const val HIDE_START_YARD = 6
    const val MAX_FLIGHT_MS = 3000
    const val FORT_GRID = 3
    const val FORT_BOARD_PAD_DP = 10
    const val FORT_BOARD_BORDER_DP = 2
    const val FORT_BOARD_MAX_DP = 380
    val UNLOCK_THRESHOLDS = listOf(0, 3, 8, 15, 24, 40)
}
