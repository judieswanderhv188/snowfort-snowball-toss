package com.snowfort.toss.presentation.settings

data class SettingsUiState(
    val sound: Boolean = true,
    val haptics: Boolean = true,
    val arcAssist: Boolean = true,
    val confirmVisible: Boolean = false,
    val stars: Int = 0,
    val yardsCleared: Int = 0
)
