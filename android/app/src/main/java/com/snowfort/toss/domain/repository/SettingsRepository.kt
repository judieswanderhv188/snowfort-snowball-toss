package com.snowfort.toss.domain.repository

import com.snowfort.toss.domain.model.AppSettings

interface SettingsRepository {
    fun load(): AppSettings
    fun save(settings: AppSettings)
}
