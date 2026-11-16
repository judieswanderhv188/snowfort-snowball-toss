package com.snowfort.toss.data.repository

import com.snowfort.toss.data.local.PreferencesStorage
import com.snowfort.toss.domain.model.PlayerProgress
import com.snowfort.toss.domain.repository.ProgressRepository

class ProgressRepositoryImpl(
    private val storage: PreferencesStorage
) : ProgressRepository {

    override fun load(): PlayerProgress {
        val layoutRaw = storage.readString(PreferencesStorage.KEY_FORT_LAYOUT, DEFAULT_LAYOUT)
        val ownedRaw = storage.readString(PreferencesStorage.KEY_OWNED_MITTENS, DEFAULT_OWNED)
        return PlayerProgress(
            stars = storage.readInt(PreferencesStorage.KEY_STARS, 0),
            currentYard = storage.readInt(PreferencesStorage.KEY_CURRENT_YARD, 1),
            yardsCleared = storage.readInt(PreferencesStorage.KEY_YARDS_CLEARED, 0),
            bestStars = storage.readInt(PreferencesStorage.KEY_BEST_STARS, 0),
            bestAccuracy = storage.readInt(PreferencesStorage.KEY_BEST_ACCURACY, 0),
            fortLayout = decodeList(layoutRaw, 9),
            ownedMittens = decodeList(ownedRaw, 0).toSet().ifEmpty { setOf(0) },
            equippedMitten = storage.readInt(PreferencesStorage.KEY_EQUIPPED_MITTEN, 0)
        )
    }

    override fun save(progress: PlayerProgress) {
        storage.writeInt(PreferencesStorage.KEY_STARS, progress.stars)
        storage.writeInt(PreferencesStorage.KEY_CURRENT_YARD, progress.currentYard)
        storage.writeInt(PreferencesStorage.KEY_YARDS_CLEARED, progress.yardsCleared)
        storage.writeInt(PreferencesStorage.KEY_BEST_STARS, progress.bestStars)
        storage.writeInt(PreferencesStorage.KEY_BEST_ACCURACY, progress.bestAccuracy)
        storage.writeString(PreferencesStorage.KEY_FORT_LAYOUT, progress.fortLayout.joinToString(","))
        storage.writeString(
            PreferencesStorage.KEY_OWNED_MITTENS,
            progress.ownedMittens.sorted().joinToString(",")
        )
        storage.writeInt(PreferencesStorage.KEY_EQUIPPED_MITTEN, progress.equippedMitten)
    }

    override fun clear() {
        storage.wipeProgress()
    }

    private fun decodeList(raw: String, expected: Int): List<Int> {
        val parsed = raw.split(",").mapNotNull { it.trim().toIntOrNull() }
        if (expected <= 0) return parsed
        return if (parsed.size == expected) parsed else List(expected) { -1 }
    }

    private companion object {
        const val DEFAULT_LAYOUT = "-1,-1,-1,-1,-1,-1,-1,-1,-1"
        const val DEFAULT_OWNED = "0"
    }
}
