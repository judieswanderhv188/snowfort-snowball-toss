package com.snowfort.toss.presentation.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowfort.toss.data.sample.SampleData
import com.snowfort.toss.domain.model.Mitten
import com.snowfort.toss.domain.repository.ProgressRepository
import com.snowfort.toss.domain.usecase.PurchaseMittenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MittenShopViewModel(
    private val progressRepository: ProgressRepository,
    private val purchaseMitten: PurchaseMittenUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MittenShopUiState(mittens = SampleData.mittens))
    val state: StateFlow<MittenShopUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            _state.update {
                it.copy(
                    stars = progress.stars,
                    mittens = SampleData.mittens,
                    owned = progress.ownedMittens,
                    equipped = progress.equippedMitten
                )
            }
        }
    }

    fun select(mitten: Mitten) {
        viewModelScope.launch {
            val progress = progressRepository.load()
            val updated = purchaseMitten(progress, mitten) ?: return@launch
            progressRepository.save(updated)
            _state.update {
                it.copy(
                    stars = updated.stars,
                    owned = updated.ownedMittens,
                    equipped = updated.equippedMitten
                )
            }
        }
    }
}
