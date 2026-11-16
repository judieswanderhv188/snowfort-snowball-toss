package com.snowfort.toss.domain.model

data class AppSettings(
    val sound: Boolean = true,
    val haptics: Boolean = true,
    val arcAssist: Boolean = true
)
