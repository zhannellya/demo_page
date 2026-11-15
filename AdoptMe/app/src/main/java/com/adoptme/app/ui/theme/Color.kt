package com.adoptme.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core palette — elegant purple & lavender with neon accents
val PurpleDeep = Color(0xFF5B3FD6)
val PurplePrimary = Color(0xFF7B5CFF)
val PurpleSoft = Color(0xFF9B84FF)
val Lavender = Color(0xFFB8A9FF)
val LavenderMist = Color(0xFFE8E0FF)
val LavenderBg = Color(0xFFF8F5FF)
val LilacCard = Color(0xFFFFFBFF)

val PinkNeon = Color(0xFFFF6BCB)
val PinkSoft = Color(0xFFFFB3E0)
val CyanNeon = Color(0xFF5CE1E6)
val CyanSoft = Color(0xFFB8F4F6)

val TextPrimary = Color(0xFF2D2456)
val TextSecondary = Color(0xFF7A7199)
val TextMuted = Color(0xFFA89FBF)
val DividerSoft = Color(0xFFEDE6FA)

val SuccessGreen = Color(0xFF4CD964)
val WarningAmber = Color(0xFFFFB020)
val ErrorRose = Color(0xFFFF5A7A)
val White = Color(0xFFFFFFFF)

val PurpleGradient = Brush.horizontalGradient(
    colors = listOf(PurplePrimary, Color(0xFFA78BFA), PinkNeon.copy(alpha = 0.85f))
)

val PurpleVerticalGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF8B6CFF), PurplePrimary, PurpleDeep)
)

val SoftBgGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFF3EDFF), LavenderBg, Color(0xFFFFF0FA))
)

val GlowPink = PinkNeon.copy(alpha = 0.35f)
val GlowCyan = CyanNeon.copy(alpha = 0.30f)
val GlowPurple = PurplePrimary.copy(alpha = 0.25f)
