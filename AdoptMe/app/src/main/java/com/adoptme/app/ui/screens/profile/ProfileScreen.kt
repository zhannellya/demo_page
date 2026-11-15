package com.adoptme.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.CyanNeon
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.PurpleGradient
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.SuccessGreen
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.WarningAmber
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNotifications: () -> Unit,
    onHelp: () -> Unit,
    onLostFound: () -> Unit,
    onVeterinary: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ProfileViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val user by vm.user.collectAsState()
    val applications by vm.applications.collectAsState()

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize().padding(bottom = 88.dp)) {
            ScreenHeader(title = "Profile")
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(PurpleGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                                    color = White,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                            Spacer(Modifier.size(16.dp))
                            Column {
                                Text(user.name.ifBlank { "Guest" }, style = MaterialTheme.typography.headlineSmall)
                                Text(user.email.ifBlank { "Not signed in" }, color = TextSecondary)
                                if (user.phone.isNotBlank()) {
                                    Text(user.phone, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                item {
                    AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            ProfileLink("Notifications", onNotifications)
                            ProfileLink("Lost & Found", onLostFound)
                            ProfileLink("Help & Donate", onHelp)
                            ProfileLink("Veterinary", onVeterinary)
                        }
                    }
                }
                item {
                    Text("Adoption history", style = MaterialTheme.typography.headlineSmall, color = PurplePrimary)
                }
                if (applications.isEmpty()) {
                    item {
                        AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "No applications yet. Find a pet you love and tap Adopt.",
                                modifier = Modifier.padding(20.dp),
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    items(applications, key = { it.id }) { app ->
                        AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(app.animalName, style = MaterialTheme.typography.titleLarge)
                                Spacer(Modifier.height(4.dp))
                                Text(app.reason, color = TextSecondary, maxLines = 2)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    app.status.name.replace('_', ' '),
                                    color = statusColor(app.status),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                item {
                    GradientButton(
                        text = "Log out",
                        onClick = {
                            vm.logout()
                            onLogout()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileLink(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(label, color = PurplePrimary, modifier = Modifier.fillMaxWidth())
    }
}

private fun statusColor(status: ApplicationStatus) = when (status) {
    ApplicationStatus.PENDING -> WarningAmber
    ApplicationStatus.UNDER_REVIEW -> CyanNeon
    ApplicationStatus.APPROVED -> SuccessGreen
    ApplicationStatus.REJECTED -> ErrorRose
}
