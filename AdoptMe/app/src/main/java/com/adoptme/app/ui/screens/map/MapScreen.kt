package com.adoptme.app.ui.screens.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.CyanNeon
import com.adoptme.app.ui.theme.LavenderMist
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ContentViewModel

@Composable
fun MapScreen() {
    val container = LocalAppContainer.current
    val vm: ContentViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val shelters = vm.shelters

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize().padding(bottom = 88.dp)) {
            ScreenHeader(title = "Shelters map")
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(LavenderMist, Color(0xFFD8CFFF), Color(0xFFFFE6F5))
                        )
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Soft map grid
                    val step = 48f
                    for (x in 0..size.width.toInt() step step.toInt()) {
                        drawLine(Color.White.copy(0.35f), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), 2f)
                    }
                    for (y in 0..size.height.toInt() step step.toInt()) {
                        drawLine(Color.White.copy(0.35f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), 2f)
                    }
                    val points = listOf(
                        Offset(size.width * 0.28f, size.height * 0.42f),
                        Offset(size.width * 0.55f, size.height * 0.35f),
                        Offset(size.width * 0.42f, size.height * 0.68f),
                        Offset(size.width * 0.72f, size.height * 0.58f)
                    )
                    points.forEachIndexed { index, point ->
                        val glow = if (index % 2 == 0) PinkNeon else CyanNeon
                        drawCircle(glow.copy(0.35f), radius = 28f, center = point)
                        drawCircle(PurplePrimary, radius = 12f, center = point)
                        drawCircle(White, radius = 5f, center = point)
                    }
                }
                Text(
                    "Almaty shelters",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = PurplePrimary
                )
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(shelters, key = { it.id }) { shelter ->
                    AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(shelter.name, style = MaterialTheme.typography.titleLarge, color = PurplePrimary)
                            Spacer(Modifier.height(4.dp))
                            Text(shelter.address, color = TextSecondary)
                            Text(shelter.phone, style = MaterialTheme.typography.titleSmall)
                            Text("Open ${shelter.openHours}", color = CyanNeon, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
