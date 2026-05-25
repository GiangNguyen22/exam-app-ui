package com.internalexam.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AppBlue = Color(0xFF2563EB)
val AppIndigo = Color(0xFF4F46E5)
val AppViolet = Color(0xFF7C3AED)
val AppMint = Color(0xFF16A34A)
val AppAmber = Color(0xFFF59E0B)
val AppRed = Color(0xFFDC2626)
val AppBg = Color(0xFFF7F9FF)
val AppSurface = Color(0xFFFFFFFF)
val AppText = Color(0xFF14213D)
val AppMuted = Color(0xFF64748B)

private val LightColors: ColorScheme = lightColorScheme(
    primary = AppBlue,
    secondary = AppViolet,
    tertiary = AppMint,
    background = AppBg,
    surface = AppSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = AppText,
    onSurface = AppText,
    error = AppRed
)

@Composable
fun InternalExamTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
