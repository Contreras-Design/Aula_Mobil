package com.itespf.aulamovil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueLight,
    onPrimaryContainer = AccentGreen,
    secondary = BlueDark,
    background = Surface,
    surface = Surface,
    onSurface = OnSurface,
    outline = Outline,
    error = AccentRed
)

private val DarkColors = darkColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = AccentGreen,
    onPrimaryContainer = BlueLight,
    secondary =BlueDark,
    background = Color(0xFF14161C),
    surface = Color(0xFF1C1F26),
    onSurface = Color(0xFFADD2FF),
    outline = Color(0xFF3A3E48),
    error = AccentRed
)

@Composable
fun AulaMovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
