package com.snowfort.toss.data.repository

import com.snowfort.toss.data.local.PreferencesStorage
import com.snowfort.toss.domain.model.AppSettings
import com.snowfort.toss.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val storage: PreferencesStorage
) : SettingsRepository {

    override fun load(): AppSettings = AppSettings(
        sound = storage.readBoolean(PreferencesStorage.KEY_SOUND, true),
        haptics = storage.readBoolean(PreferencesStorage.KEY_HAPTICS, true),
        arcAssist = storage.readBoolean(PreferencesStorage.KEY_ASSIST, true)
    )

    override fun save(settings: AppSettings) {
        storage.writeBoolean(PreferencesStorage.KEY_SOUND, settings.sound)
        storage.writeBoolean(PreferencesStorage.KEY_HAPTICS, settings.haptics)
        storage.writeBoolean(PreferencesStorage.KEY_ASSIST, settings.arcAssist)
    }
}
