package com.internalexam.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AppBlue = Color(0xFF5B67F1)
val AppIndigo = Color(0xFF6C5CE7)
val AppViolet = Color(0xFF8E6BF2)
val AppMint = Color(0xFF35C2A5)
val AppAmber = Color(0xFFFFB74D)
val AppRed = Color(0xFFFF6B6B)
val AppBg = Color(0xFFF6F4FF)
val AppSurface = Color(0xFFFFFFFF)
val AppText = Color(0xFF20213D)
val AppMuted = Color(0xFF7A7C99)
val AppCoral = Color(0xFFFF8A7A)
val AppLilac = Color(0xFFEDE8FF)

private val LightColors: ColorScheme = lightColorScheme(
    primary = AppIndigo,
    secondary = AppCoral,
    tertiary = AppMint,
    background = AppBg,
    surface = AppSurface,
    surfaceVariant = AppLilac,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = AppText,
    onSurface = AppText,
    onSurfaceVariant = AppText,
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
