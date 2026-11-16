package com.snowfort.toss.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Game : Screen
    data class GameOver(
        val yard: Int,
        val hits: Int,
        val shots: Int,
        val stars: Int,
        val cleared: Boolean
    ) : Screen
    data object FortBuilder : Screen
    data object MittenShop : Screen
    data object Settings : Screen
}
