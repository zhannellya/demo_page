package com.adoptme.app.ui.screens.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.KazakhstanCities
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.EmptyState
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
import kotlin.math.roundToInt

@Composable
fun MapScreen(
    onBack: (() -> Unit)? = null,
    onOpenShelter: (String) -> Unit = {}
) {
    val container = LocalAppContainer.current
    val vm: ContentViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val shelters by vm.shelters.collectAsState()
    var selectedCity by rememberSaveable { mutableStateOf<String?>(null) }
    val cities = (KazakhstanCities.featured + shelters.map { it.city })
        .filter { it.isNotBlank() }
        .distinct()
    val visible = shelters.filter { selectedCity == null || it.city == selectedCity }

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Приюты", onBack = onBack)
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryChip("Все", selectedCity == null) { selectedCity = null }
                cities.forEach { city ->
                    CategoryChip(city, selectedCity == city) { selectedCity = city }
                }
            }
            Spacer(Modifier.height(12.dp))
            ShelterMap(
                shelters = visible,
                onMarkerClick = onOpenShelter,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Spacer(Modifier.height(12.dp))
            if (visible.isEmpty()) {
                EmptyState("Нет приютов", "Выберите другой город Казахстана.")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visible, key = { it.id }) { shelter ->
                        AdoptMeCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenShelter(shelter.id) }
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(shelter.name, style = MaterialTheme.typography.titleLarge, color = PurplePrimary)
                                Spacer(Modifier.height(4.dp))
                                Text(shelter.city, color = CyanNeon, style = MaterialTheme.typography.labelLarge)
                                Text(shelter.address, color = TextSecondary)
                                Text(shelter.phone, style = MaterialTheme.typography.titleSmall)
                                Text("Открыто ${shelter.workingHours}", color = CyanNeon, style = MaterialTheme.typography.labelMedium)
                                if (shelter.description.isNotBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(shelter.description, color = TextSecondary, maxLines = 2)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShelterMap(
    shelters: List<Shelter>,
    onMarkerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(LavenderMist, Color(0xFFD8CFFF), Color(0xFFFFE6F5))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 48f
            for (x in 0..size.width.toInt() step step.toInt()) {
                drawLine(Color.White.copy(0.35f), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), 2f)
            }
            for (y in 0..size.height.toInt() step step.toInt()) {
                drawLine(Color.White.copy(0.35f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), 2f)
            }
        }
        Text(
            "Приюты Казахстана",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = PurplePrimary
        )
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
            val heightPx = with(LocalDensity.current) { maxHeight.toPx() }
            val minLat = 42.2
            val maxLat = 51.8
            val minLng = 69.3
            val maxLng = 77.2
            shelters.forEachIndexed { index, shelter ->
                val x = ((shelter.longitude - minLng) / (maxLng - minLng)).coerceIn(0.08, 0.92) * widthPx
                val y = (1.0 - ((shelter.latitude - minLat) / (maxLat - minLat)).coerceIn(0.0, 1.0)).coerceIn(0.18, 0.86) * heightPx
                Box(
                    modifier = Modifier
                        .offset { IntOffset((x - 18).roundToInt(), (y - 18).roundToInt()) }
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (index % 2 == 0) PinkNeon.copy(0.35f) else CyanNeon.copy(0.35f))
                        .clickable { onMarkerClick(shelter.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary)
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(White)
                    )
                }
            }
        }
    }
}
