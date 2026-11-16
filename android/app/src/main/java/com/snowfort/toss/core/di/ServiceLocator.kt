package com.snowfort.toss.core.di

import android.content.Context
import com.snowfort.toss.data.local.PreferencesStorage
import com.snowfort.toss.data.repository.ProgressRepositoryImpl
import com.snowfort.toss.data.repository.SettingsRepositoryImpl
import com.snowfort.toss.domain.repository.ProgressRepository
import com.snowfort.toss.domain.repository.SettingsRepository
import com.snowfort.toss.domain.usecase.BuildYardUseCase
import com.snowfort.toss.domain.usecase.PurchaseMittenUseCase
import com.snowfort.toss.domain.usecase.ScoreRoundUseCase
import com.snowfort.toss.domain.usecase.StepPhysicsUseCase

object ServiceLocator {

    private var storage: PreferencesStorage? = null
    private var progress: ProgressRepository? = null
    private var settings: SettingsRepository? = null

    val buildYard: BuildYardUseCase = BuildYardUseCase()
    val stepPhysics: StepPhysicsUseCase = StepPhysicsUseCase()
    val scoreRound: ScoreRoundUseCase = ScoreRoundUseCase()
    val purchaseMitten: PurchaseMittenUseCase = PurchaseMittenUseCase()

    fun init(context: Context) {
        if (storage != null) return
        val created = PreferencesStorage(context)
        storage = created
        progress = ProgressRepositoryImpl(created)
        settings = SettingsRepositoryImpl(created)
    }

    val progressRepository: ProgressRepository
        get() = requireNotNull(progress)

    val settingsRepository: SettingsRepository
        get() = requireNotNull(settings)
}
