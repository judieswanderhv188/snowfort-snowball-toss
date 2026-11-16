package com.snowfort.toss.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowfort.toss.core.config.GameConfig
import com.snowfort.toss.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            _state.update {
                it.copy(
                    stars = progress.stars,
                    currentYard = progress.currentYard,
                    totalYards = GameConfig.TOTAL_YARDS,
                    bestStars = progress.bestStars,
                    bestAccuracy = progress.bestAccuracy,
                    loaded = true
                )
            }
        }
    }
}
