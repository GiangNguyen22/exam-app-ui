package com.internalexam.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Primary palette ──
val AppBlue = Color(0xFF5B67F1)
val AppIndigo = Color(0xFF6C5CE7)
val AppDarkIndigo = Color(0xFF4834D4)
val AppViolet = Color(0xFF8E6BF2)
val AppMint = Color(0xFF35C2A5)
val AppTeal = Color(0xFF2BB5A0)
val AppAmber = Color(0xFFFFB74D)
val AppRed = Color(0xFFFF6B6B)
val AppCoral = Color(0xFFFF8A7A)

// ── Semantic aliases ──
val AppSuccess = AppMint
val AppWarning = AppAmber
val AppDanger = AppRed
val AppInfo = AppBlue

// ── Surface & text ──
val AppBg = Color(0xFFF6F4FF)
val AppLightBg = Color(0xFFFAF9FF)
val AppSurface = Color(0xFFFFFFFF)
val AppText = Color(0xFF20213D)
val AppMuted = Color(0xFF7A7C99)
val AppLilac = Color(0xFFEDE8FF)
val AppCardBorder = Color(0xFFE8E5F0)

// ── Gradient brushes ──
val HeroGradient = Brush.linearGradient(
    colors = listOf(AppIndigo, AppBlue),
    start = Offset.Zero,
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)
val SplashGradient = Brush.linearGradient(
    colors = listOf(AppDarkIndigo, AppIndigo, AppViolet),
    start = Offset.Zero,
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)
val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(AppIndigo, AppBlue)
)
val BgGradient = Brush.verticalGradient(
    colors = listOf(AppBg, AppLilac.copy(alpha = 0.3f), AppBg)
)

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
