package com.adoptme.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AdoptMeColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = White,
    primaryContainer = LavenderMist,
    onPrimaryContainer = TextPrimary,
    secondary = PinkNeon,
    onSecondary = White,
    secondaryContainer = PinkSoft.copy(alpha = 0.35f),
    onSecondaryContainer = TextPrimary,
    tertiary = CyanNeon,
    onTertiary = TextPrimary,
    tertiaryContainer = CyanSoft.copy(alpha = 0.4f),
    background = LavenderBg,
    onBackground = TextPrimary,
    surface = LilacCard,
    onSurface = TextPrimary,
    surfaceVariant = LavenderMist,
    onSurfaceVariant = TextSecondary,
    outline = DividerSoft,
    error = ErrorRose,
    onError = White
)

@Composable
fun AdoptMeTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = LavenderBg.toArgb()
            window.navigationBarColor = LilacCard.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = AdoptMeColorScheme,
        typography = AdoptMeTypography,
        shapes = AdoptMeShapes,
        content = content
    )
}
