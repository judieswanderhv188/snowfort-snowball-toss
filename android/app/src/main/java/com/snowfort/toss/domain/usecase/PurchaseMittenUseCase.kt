package com.snowfort.toss.domain.usecase

import com.snowfort.toss.domain.model.Mitten
import com.snowfort.toss.domain.model.PlayerProgress

class PurchaseMittenUseCase {

    operator fun invoke(progress: PlayerProgress, mitten: Mitten): PlayerProgress? {
        if (progress.ownedMittens.contains(mitten.id)) {
            return progress.copy(equippedMitten = mitten.id)
        }
        if (progress.stars < mitten.price) return null
        return progress.copy(
            stars = progress.stars - mitten.price,
            ownedMittens = progress.ownedMittens + mitten.id,
            equippedMitten = mitten.id
        )
    }
}
