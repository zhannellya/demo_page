package com.adoptme.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Blush = Color(0xFFF5C9F2)
val PurpleMid = Color(0xFF6F4F84)
val Ink = Color(0xFF311E49)
val Plum = Color(0xFF43145D)

val PurpleDeep = Ink
val PurplePrimary = PurpleMid
val PurpleSoft = Color(0xFF8D6B9A)
val Lavender = Blush
val LavenderMist = Color(0xFFF8E7F4)
val LavenderBg = Color(0xFFFBF3F8)
val LilacCard = Color(0xFFFFFFFF)

val PinkNeon = Color(0xFFC46EB4)
val PinkSoft = Blush
val CyanNeon = Color(0xFF6F4F84)
val CyanSoft = Color(0xFFE7D4EA)

val TextPrimary = Ink
val TextSecondary = Color(0xFF5A4568)
val TextMuted = Color(0xFF8C7894)
val DividerSoft = Color(0xFFEAD7E8)

val SuccessGreen = Color(0xFF2E8B57)
val WarningAmber = Color(0xFFB86E00)
val ErrorRose = Color(0xFFB4234A)
val White = Color(0xFFFFFFFF)

val PurpleGradient = Brush.horizontalGradient(
    colors = listOf(PurpleMid, Plum)
)

val PurpleVerticalGradient = Brush.verticalGradient(
    colors = listOf(Blush, PurpleMid, Plum)
)

val SoftBgGradient = Brush.verticalGradient(
    colors = listOf(Blush, Color(0xFFF7E4F3), Color(0xFFF3DFF1))
)

val GlowPink = Blush.copy(alpha = 0.55f)
val GlowCyan = PurpleMid.copy(alpha = 0.18f)
val GlowPurple = Plum.copy(alpha = 0.16f)
