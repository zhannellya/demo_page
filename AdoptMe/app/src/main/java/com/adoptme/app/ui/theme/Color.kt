package com.adoptme.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Plum = Color(0xFF311E49)
val Mauve = Color(0xFF6F4F84)
val Blush = Color(0xFFF5C9F2)

val PurpleDeep = Plum
val PurplePrimary = Mauve
val PurpleSoft = Color(0xFF8E6BA3)
val Lavender = Blush
val LavenderMist = Color(0xFFF8E4F4)
val LavenderBg = Color(0xFFF3D7EF)
val LilacCard = Color(0xFFFFF8FC)

val PinkNeon = Color(0xFFC4538A)
val PinkSoft = Blush
val CyanNeon = Color(0xFF7E6AA8)
val CyanSoft = Color(0xFFE7D4F0)

val TextPrimary = Plum
val TextSecondary = Color(0xFF5C4570)
val TextMuted = Color(0xFF8A7496)
val DividerSoft = Color(0xFFF0D9EC)
val OnGradient = Color(0xFFFFFFFF)
val OnGradientMuted = Blush

val SuccessGreen = Color(0xFF2E9B57)
val WarningAmber = Color(0xFFC47B12)
val ErrorRose = Color(0xFFC43858)
val White = Color(0xFFFFFFFF)

val PurpleGradient = Brush.horizontalGradient(
    colors = listOf(Plum, Mauve)
)

val PurpleVerticalGradient = Brush.verticalGradient(
    colors = listOf(Plum, Mauve, Blush)
)

val SoftBgGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0.0f to Color(0xFF311E49),
        0.42f to Color(0xFF6F4F84),
        1.0f to Color(0xFFF5C9F2)
    )
)

val GlowPink = Blush.copy(alpha = 0.28f)
val GlowCyan = Color(0xFFE7D4F0).copy(alpha = 0.22f)
val GlowPurple = Plum.copy(alpha = 0.18f)
