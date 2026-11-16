package com.adoptme.app.ui.screens.veterinary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adoptme.app.data.local.SeedData
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary

@Composable
fun VeterinaryScreen(onBack: () -> Unit) {
    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader("Ветеринарная помощь", onBack)
            Column(Modifier.padding(horizontal = 16.dp)) {
                DemoBanner("Это не справочник клиник. Адреса и телефоны намеренно скрыты.")
                Spacer(Modifier.height(12.dp))
                SeedData.clinics.forEach { clinic ->
                    AdoptMeCard(Modifier.padding(bottom = 12.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(clinic.name, style = MaterialTheme.typography.titleLarge, color = PurplePrimary)
                            Text(clinic.city, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(clinic.specialties, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(clinic.note, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
