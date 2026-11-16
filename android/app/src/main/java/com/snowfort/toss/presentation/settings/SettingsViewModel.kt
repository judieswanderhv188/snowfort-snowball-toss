package com.snowfort.toss.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowfort.toss.domain.model.AppSettings
import com.snowfort.toss.domain.repository.ProgressRepository
import com.snowfort.toss.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val settings = settingsRepository.load()
            val progress = progressRepository.load()
            _state.update {
                it.copy(
                    sound = settings.sound,
                    haptics = settings.haptics,
                    arcAssist = settings.arcAssist,
                    stars = progress.stars,
                    yardsCleared = progress.yardsCleared
                )
            }
        }
    }

    fun setSound(enabled: Boolean) {
        _state.update { it.copy(sound = enabled) }
        persist()
    }

    fun setHaptics(enabled: Boolean) {
        _state.update { it.copy(haptics = enabled) }
        persist()
    }

    fun setArcAssist(enabled: Boolean) {
        _state.update { it.copy(arcAssist = enabled) }
        persist()
    }

    fun requestReset() {
        _state.update { it.copy(confirmVisible = true) }
    }

    fun dismissReset() {
        _state.update { it.copy(confirmVisible = false) }
    }

    fun confirmReset() {
        viewModelScope.launch {
            progressRepository.clear()
            val progress = progressRepository.load()
            _state.update {
                it.copy(
                    confirmVisible = false,
                    stars = progress.stars,
                    yardsCleared = progress.yardsCleared
                )
            }
        }
    }

    private fun persist() {
        viewModelScope.launch {
            val current = _state.value
            settingsRepository.save(
                AppSettings(
                    sound = current.sound,
                    haptics = current.haptics,
                    arcAssist = current.arcAssist
                )
            )
        }
    }
}
