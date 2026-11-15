package com.adoptme.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.adoptme.app.ui.theme.CardShape
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.PurplePrimary

@Composable
fun AdoptMeCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = CardShape,
                ambientColor = PurplePrimary.copy(alpha = 0.12f),
                spotColor = PurplePrimary.copy(alpha = 0.18f)
            )
            .clip(CardShape)
            .background(color = LilacCard, shape = CardShape)
    ) {
        content()
    }
}
