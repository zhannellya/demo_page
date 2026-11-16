package com.adoptme.app.ui.screens.veterinary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.CyanNeon
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ContentViewModel

@Composable
fun VeterinaryScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: ContentViewModel = viewModel(factory = AdoptMeViewModelFactory(container))

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Ветеринар", onBack = onBack)
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Советы по уходу", style = MaterialTheme.typography.titleLarge, color = PurplePrimary)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Следите за прививками, проходите ежегодный осмотр и уточняйте в приюте медицинскую историю до усыновления.",
                                color = TextSecondary
                            )
                        }
                    }
                }
                items(vm.clinics, key = { it.id }) { clinic ->
                    AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(clinic.name, style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(4.dp))
                            Text(clinic.specialties, color = CyanNeon)
                            Spacer(Modifier.height(6.dp))
                            Text(clinic.address, color = TextSecondary)
                            Text(clinic.phone, style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(4.dp))
                            Text("Оценка ${clinic.rating} ★", color = PurplePrimary)
                        }
                    }
                }
            }
        }
    }
}
