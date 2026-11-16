package com.snowfort.toss.presentation.shop

import com.snowfort.toss.domain.model.Mitten

data class MittenShopUiState(
    val stars: Int = 0,
    val mittens: List<Mitten> = emptyList(),
    val owned: Set<Int> = emptySet(),
    val equipped: Int = 0
) {
    fun isOwned(mitten: Mitten): Boolean = owned.contains(mitten.id)

    fun isEquipped(mitten: Mitten): Boolean = equipped == mitten.id

    fun canAfford(mitten: Mitten): Boolean = stars >= mitten.price
}
