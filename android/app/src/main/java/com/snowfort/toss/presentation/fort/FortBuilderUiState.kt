package com.snowfort.toss.presentation.fort

import com.snowfort.toss.domain.model.FortPiece

data class FortBuilderUiState(
    val stars: Int = 0,
    val yardsCleared: Int = 0,
    val layout: List<Int> = List(9) { -1 },
    val pieces: List<FortPiece> = emptyList(),
    val selectedPiece: Int = 0
) {
    fun unlocked(piece: FortPiece): Boolean = yardsCleared >= piece.unlockYards

    val placedCount: Int
        get() = layout.count { it >= 0 }
}
