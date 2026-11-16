package com.snowfort.toss.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snowfort.toss.core.di.AppViewModelFactory
import com.snowfort.toss.core.navigation.Screen
import com.snowfort.toss.core.theme.NightBase
import com.snowfort.toss.presentation.fort.FortBuilderScreen
import com.snowfort.toss.presentation.game.GameScreen
import com.snowfort.toss.presentation.gameover.GameOverScreen
import com.snowfort.toss.presentation.menu.MenuScreen
import com.snowfort.toss.presentation.settings.SettingsScreen
import com.snowfort.toss.presentation.shop.MittenShopScreen
import com.snowfort.toss.presentation.splash.SplashScreen

@Composable
fun AppRoot(
    appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory)
) {
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) {
        appViewModel.back()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NightBase)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                if (targetState is Screen.Game) {
                    (
                        fadeIn(animationSpec = tween(durationMillis = 220)) +
                            slideInVertically(animationSpec = tween(durationMillis = 220)) { it / 12 }
                        ) togetherWith fadeOut(animationSpec = tween(durationMillis = 180))
                } else {
                    fadeIn(animationSpec = tween(durationMillis = 220)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 180))
                }
            },
            label = "screenHost"
        ) { target ->
            when (target) {
                is Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.replace(Screen.Menu) }
                )

                is Screen.Menu -> MenuScreen(
                    onPlay = { appViewModel.open(Screen.Game) },
                    onFort = { appViewModel.open(Screen.FortBuilder) },
                    onShop = { appViewModel.open(Screen.MittenShop) },
                    onSettings = { appViewModel.open(Screen.Settings) }
                )

                is Screen.Game -> GameScreen(
                    onExit = { appViewModel.back() },
                    onRoundFinished = { yard, hits, shots, stars, cleared ->
                        appViewModel.replace(
                            Screen.GameOver(
                                yard = yard,
                                hits = hits,
                                shots = shots,
                                stars = stars,
                                cleared = cleared
                            )
                        )
                    }
                )

                is Screen.GameOver -> GameOverScreen(
                    yard = target.yard,
                    hits = target.hits,
                    shots = target.shots,
                    stars = target.stars,
                    cleared = target.cleared,
                    onPlayAgain = { appViewModel.replace(Screen.Game) },
                    onMenu = { appViewModel.home() }
                )

                is Screen.FortBuilder -> FortBuilderScreen(
                    onBack = { appViewModel.back() }
                )

                is Screen.MittenShop -> MittenShopScreen(
                    onBack = { appViewModel.back() }
                )

                is Screen.Settings -> SettingsScreen(
                    onBack = { appViewModel.back() }
                )
            }
        }
    }
}
