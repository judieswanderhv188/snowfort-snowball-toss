package com.snowfort.toss.domain.repository

import com.snowfort.toss.domain.model.PlayerProgress

interface ProgressRepository {
    fun load(): PlayerProgress
    fun save(progress: PlayerProgress)
    fun clear()
}
