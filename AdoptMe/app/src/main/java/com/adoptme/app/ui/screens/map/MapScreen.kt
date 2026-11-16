package com.adoptme.app.ui.screens.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.BuildConfig
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ShelterViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShelterSearchScreen() {
    val container = LocalAppContainer.current
    val vm: ShelterViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val shelters by vm.visible.collectAsState()
    val all by vm.all.collectAsState()
    val city by vm.selectedCity.collectAsState()
    val cities = all.map { it.city }.distinct()

    SoftBackground {
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            item {
                ScreenHeader("Приюты")
                DemoBanner(
                    "Карточки учебные. У них нет улицы и координат, поэтому на карте нет меток приютов. " +
                        "Не ищите по этим названиям реальную организацию."
                )
                Spacer(Modifier.height(12.dp))
                ShelterMapPanel()
                Spacer(Modifier.height(12.dp))
                Text("Город", style = MaterialTheme.typography.titleMedium, color = PurplePrimary)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip("Все", city == null) { vm.selectCity(null) }
                    cities.forEach { item ->
                        CategoryChip(item, city == item) { vm.selectCity(item) }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            if (shelters.isEmpty()) {
                item { EmptyState("Приюты не найдены", "Выберите другой город.") }
            } else {
                items(shelters, key = { it.id }) { shelter ->
                    AdoptMeCard(Modifier.padding(bottom = 12.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(shelter.name, style = MaterialTheme.typography.titleLarge, color = PurplePrimary)
                            Text(shelter.city, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(shelter.description, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(shelter.hours, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(shelter.contactNote, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShelterMapPanel() {
    val context = LocalContext.current
    val key = BuildConfig.MAPS_API_KEY
    val playServicesReady = GoogleApiAvailability.getInstance()
        .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    if (key.isBlank() || !playServicesReady) {
        AdoptMeCard {
            Column(Modifier.padding(16.dp)) {
                Text("Карта не подключена", style = MaterialTheme.typography.titleMedium, color = PurplePrimary)
                Spacer(Modifier.height(6.dp))
                Text(
                    if (key.isBlank()) {
                        "Ключ Google Maps не задан. Список приютов работает без сети и без карты."
                    } else {
                        "Службы Google Play недоступны. Список приютов остаётся на экране."
                    },
                    color = TextSecondary
                )
            }
        }
        return
    }

    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(55.751244, 37.618423), 3.5f)
    }
    Column {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(24.dp)),
            cameraPositionState = camera
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Камера показывает общий обзор. Метки приютов не ставятся: у демонстрационных карточек нет подтверждённых координат.",
            color = TextSecondary
        )
    }
}
