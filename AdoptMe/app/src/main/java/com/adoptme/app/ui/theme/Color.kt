package com.adoptme.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Blush = Color(0xFFF5C9F2)
val Mauve = Color(0xFF6F4F84)
val DeepPlum = Color(0xFF311E49)
val RoyalPlum = Color(0xFF43145D)

val PurpleDeep = DeepPlum
val PurplePrimary = RoyalPlum
val PurpleSoft = Mauve
val Lavender = Blush
val LavenderMist = Color(0xFFF8E7F6)
val LavenderBg = Color(0xFFFDF6FB)
val LilacCard = Color(0xFFFFFFFF)

val PinkNeon = Color(0xFFC47BB4)
val PinkSoft = Blush
val CyanNeon = Mauve
val CyanSoft = Color(0xFFE7D4EA)

val TextPrimary = DeepPlum
val TextSecondary = Color(0xFF5E456F)
val TextMuted = Color(0xFF8D759C)
val DividerSoft = Color(0xFFE6D5EA)

val SuccessGreen = Color(0xFF2E7D4F)
val WarningAmber = Color(0xFFB86E00)
val ErrorRose = Color(0xFFB4234A)
val White = Color(0xFFFFFFFF)

val PurpleGradient = Brush.horizontalGradient(
    colors = listOf(Mauve, RoyalPlum)
)

val PurpleVerticalGradient = Brush.verticalGradient(
    colors = listOf(Blush, Mauve, RoyalPlum)
)

val SoftBgGradient = Brush.verticalGradient(
    colors = listOf(Blush, Color(0xFFF8EEF6), Color(0xFFEADAF0))
)

val GlowPink = Blush.copy(alpha = 0.55f)
val GlowCyan = Mauve.copy(alpha = 0.18f)
val GlowPurple = RoyalPlum.copy(alpha = 0.12f)
