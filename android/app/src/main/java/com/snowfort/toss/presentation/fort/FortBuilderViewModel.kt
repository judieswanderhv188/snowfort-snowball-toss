package com.snowfort.toss.presentation.fort

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowfort.toss.data.sample.SampleData
import com.snowfort.toss.domain.repository.ProgressRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FortBuilderViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FortBuilderUiState(pieces = SampleData.fortPieces))
    val state: StateFlow<FortBuilderUiState> = _state.asStateFlow()

    private val _completed = Channel<Int>(Channel.BUFFERED)
    val completed: Flow<Int> = _completed.receiveAsFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            _state.update {
                it.copy(
                    stars = progress.stars,
                    yardsCleared = progress.yardsCleared,
                    layout = progress.fortLayout,
                    pieces = SampleData.fortPieces
                )
            }
        }
    }

    fun selectPiece(pieceId: Int) {
        val current = _state.value
        val piece = current.pieces.firstOrNull { it.id == pieceId } ?: return
        if (!current.unlocked(piece)) return
        _state.update { it.copy(selectedPiece = pieceId) }
    }

    fun placeAt(slot: Int) {
        val current = _state.value
        if (slot !in current.layout.indices) return
        val piece = current.pieces.firstOrNull { it.id == current.selectedPiece } ?: return
        if (!current.unlocked(piece)) return
        val updated = current.layout.toMutableList()
        updated[slot] = if (updated[slot] == piece.id) -1 else piece.id
        _state.update { it.copy(layout = updated) }
    }

    fun save() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            progressRepository.save(progress.copy(fortLayout = _state.value.layout))
            _completed.send(_state.value.placedCount)
        }
    }
}
