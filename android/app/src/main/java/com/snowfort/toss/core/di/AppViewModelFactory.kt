package com.snowfort.toss.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.snowfort.toss.presentation.app.AppViewModel
import com.snowfort.toss.presentation.fort.FortBuilderViewModel
import com.snowfort.toss.presentation.game.GameViewModel
import com.snowfort.toss.presentation.gameover.GameOverViewModel
import com.snowfort.toss.presentation.menu.MenuViewModel
import com.snowfort.toss.presentation.settings.SettingsViewModel
import com.snowfort.toss.presentation.shop.MittenShopViewModel
import com.snowfort.toss.presentation.splash.SplashViewModel

object AppViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel()

            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(ServiceLocator.progressRepository)

            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(
                    ServiceLocator.progressRepository,
                    ServiceLocator.settingsRepository,
                    ServiceLocator.buildYard,
                    ServiceLocator.stepPhysics,
                    ServiceLocator.scoreRound
                )

            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(ServiceLocator.progressRepository)

            modelClass.isAssignableFrom(FortBuilderViewModel::class.java) ->
                FortBuilderViewModel(ServiceLocator.progressRepository)

            modelClass.isAssignableFrom(MittenShopViewModel::class.java) ->
                MittenShopViewModel(ServiceLocator.progressRepository, ServiceLocator.purchaseMitten)

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(ServiceLocator.settingsRepository, ServiceLocator.progressRepository)

            else -> throw IllegalArgumentException(modelClass.name)
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }
}
