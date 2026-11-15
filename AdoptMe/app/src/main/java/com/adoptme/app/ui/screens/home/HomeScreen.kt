package com.adoptme.app.ui.screens.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AnimalCard
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.SearchBar
import com.adoptme.app.ui.components.SectionTitle
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.CyanNeon
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextPrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onOpenAnimal: (String) -> Unit,
    onSeeAll: () -> Unit,
    onNotifications: () -> Unit,
    onLostFound: () -> Unit,
    onHelp: () -> Unit,
    onVeterinary: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: HomeViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()

    SoftBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hello, ${state.userName}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text("Find a friend", style = MaterialTheme.typography.headlineLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onNotifications) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = PurplePrimary)
                }
            }

            SearchBar(
                query = state.query,
                onQueryChange = vm::setQuery,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryChip("All", state.selectedCategory == null, { vm.setCategory(null) })
                CategoryChip("Cats", state.selectedCategory == AnimalType.CAT, { vm.setCategory(AnimalType.CAT) }, PinkNeon)
                CategoryChip("Dogs", state.selectedCategory == AnimalType.DOG, { vm.setCategory(AnimalType.DOG) }, CyanNeon)
            }

            Spacer(Modifier.height(8.dp))
            SectionTitle("Quick actions")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard("Lost & Found", "Report or find pets", Modifier.weight(1f), onLostFound)
                QuickActionCard("Help & Donate", "Support shelters", Modifier.weight(1f), onHelp)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard("Veterinary", "Clinics & care tips", Modifier.weight(1f), onVeterinary)
                QuickActionCard("Browse all", "Cats & dogs", Modifier.weight(1f), onSeeAll)
            }

            SectionTitle("Recommended", "See all", onSeeAll)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(state.animals, key = { it.id }) { animal ->
                    AnimalCard(
                        animal = animal,
                        onClick = { onOpenAnimal(animal.id) },
                        onFavorite = { vm.toggleFavorite(animal.id) },
                        modifier = Modifier.width(220.dp)
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AdoptMeCard(modifier = modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = PurplePrimary)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}
