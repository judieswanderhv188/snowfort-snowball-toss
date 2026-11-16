package com.snowfort.toss.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SnowfortColorScheme = darkColorScheme(
    primary = MittenOrange,
    onPrimary = NightBase,
    primaryContainer = VioletPrimary,
    onPrimaryContainer = TextPrimary,
    secondary = FrostBlue,
    onSecondary = NightBase,
    secondaryContainer = VioletSecondary,
    onSecondaryContainer = TextPrimary,
    tertiary = MittenOrangeLight,
    onTertiary = NightBase,
    background = NightBase,
    onBackground = TextPrimary,
    surface = SurfaceSheet,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    error = MittenOrangeLight,
    onError = NightBase
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SnowfortColorScheme,
        typography = AppTypography,
        content = content
    )
}
