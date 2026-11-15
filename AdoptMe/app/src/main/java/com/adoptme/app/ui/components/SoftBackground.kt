package com.adoptme.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adoptme.app.ui.theme.GlowCyan
import com.adoptme.app.ui.theme.GlowPink
import com.adoptme.app.ui.theme.GlowPurple
import com.adoptme.app.ui.theme.SoftBgGradient

@Composable
fun SoftBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBgGradient)
    ) {
        NeonOrbs()
        content()
    }
}

@Composable
private fun NeonOrbs() {
    val transition = rememberInfiniteTransition(label = "glow")
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    Box(
        modifier = Modifier
            .padding(top = 40.dp, start = 20.dp)
            .size((140f * pulse).dp)
            .background(GlowPink, CircleShape)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 180.dp, end = 10.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Box(
            modifier = Modifier
                .size((120f * pulse).dp)
                .background(GlowCyan, CircleShape)
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = 30.dp)
                .size(100.dp)
                .background(GlowPurple, CircleShape)
        )
    }
}
