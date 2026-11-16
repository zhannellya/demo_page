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
    primary = Mauve,
    onPrimary = White,
    primaryContainer = Blush,
    onPrimaryContainer = Plum,
    secondary = Plum,
    onSecondary = White,
    secondaryContainer = LavenderMist,
    onSecondaryContainer = Plum,
    tertiary = PinkNeon,
    onTertiary = White,
    tertiaryContainer = Blush,
    background = LavenderBg,
    onBackground = Plum,
    surface = LilacCard,
    onSurface = Plum,
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
            window.statusBarColor = Plum.toArgb()
            window.navigationBarColor = LilacCard.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = AdoptMeColorScheme,
        typography = AdoptMeTypography,
        shapes = AdoptMeShapes,
        content = content
    )
}
