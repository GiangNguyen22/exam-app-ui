package com.internalexam.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    headlineLarge = Typography().headlineLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp),
    headlineMedium = Typography().headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
    titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = Typography().bodyLarge.copy(fontSize = 16.sp),
    bodyMedium = Typography().bodyMedium.copy(fontSize = 14.sp)
)
