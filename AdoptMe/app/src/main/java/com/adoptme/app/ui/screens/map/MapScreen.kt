package com.adoptme.app.ui.screens.map

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.BuildConfig
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SearchBar
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.OpenShelterChatViewModel
import com.adoptme.app.viewmodel.ShelterDetailViewModel
import com.adoptme.app.viewmodel.ShelterViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShelterSearchScreen(onOpenShelter: (String) -> Unit) {
    val container = LocalAppContainer.current
    val vm: ShelterViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()

    SoftBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    ScreenHeader("Приюты")
                    SearchBar(
                        query = state.query,
                        onQueryChange = vm::setQuery,
                        placeholder = "Название или город"
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CategoryChip("Все города", state.city == null) { vm.setCity(null) }
                        state.cities.forEach { city ->
                            CategoryChip(city, state.city == city) {
                                vm.setCity(if (state.city == city) null else city)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    ShelterMap(state.shelters)
                }
            }
            items(state.shelters, key = { it.id }) { shelter ->
                AdoptMeCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable { onOpenShelter(shelter.id) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(shelter.name, style = MaterialTheme.typography.titleLarge)
                        Text(shelter.city, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                        Text(shelter.hours, color = TextSecondary)
                        Text(shelter.description, color = TextSecondary, maxLines = 3)
                    }
                }
            }
        }
    }
}

@Composable
fun ShelterDetailScreen(
    shelterId: String,
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ShelterDetailViewModel = viewModel(factory = AdoptMeViewModelFactory(container, shelterId))
    val chatVm: OpenShelterChatViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val shelter by vm.shelter.collectAsState()

    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(shelter?.name ?: "Приют", onBack = onBack)
            val item = shelter
            if (item != null) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdoptMeCard {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(item.city, style = MaterialTheme.typography.titleMedium)
                            Text(item.description)
                            Text("Часы: ${item.hours}", color = TextSecondary)
                            Text("Телефон: ${item.phone}", color = TextSecondary)
                            Text("Email: ${item.email}", color = TextSecondary)
                            Text(item.addressNote, color = TextSecondary)
                        }
                    }
                    ShelterMap(listOf(item))
                    GradientButton(
                        text = "Написать приюту",
                        onClick = {
                            chatVm.open(item.id, item.name, onOpenChat)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShelterMap(shelters: List<Shelter>) {
    val context = LocalContext.current
    val playServicesReady = remember(context) {
        GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }
    val mapsReady = BuildConfig.MAPS_API_KEY.isNotBlank() && playServicesReady
    val positioned = shelters.mapNotNull { shelter ->
        val lat = shelter.latitude
        val lng = shelter.longitude
        if (lat == null || lng == null) null else shelter to LatLng(lat, lng)
    }
    AdoptMeCard {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Карта", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (!mapsReady) {
                Text(
                    if (BuildConfig.MAPS_API_KEY.isBlank()) {
                        "Ключ Google Maps не задан. Список приютов работает без карты. Добавьте MAPS_API_KEY в local.properties и не публикуйте ключ в репозитории."
                    } else {
                        "Сервисы Google Play недоступны, поэтому карта не открывается. Список приютов остаётся рабочим."
                    },
                    color = TextSecondary
                )
            } else {
                val camera = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(61.5240, 105.3188), 3f)
                }
                Text(
                    "Кадр показывает обзор, а не адрес приюта. Метка появляется только если у записи есть проверенные координаты.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    cameraPositionState = camera
                ) {
                    positioned.forEach { (shelter, position) ->
                        key(shelter.id) {
                            val markerState = remember(shelter.id) { MarkerState(position = position) }
                            Marker(
                                state = markerState,
                                title = shelter.name,
                                snippet = shelter.city
                            )
                        }
                    }
                }
            }
            if (positioned.isEmpty()) {
                Text(
                    "В каталоге нет проверенных координат, поэтому метки не ставятся.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
