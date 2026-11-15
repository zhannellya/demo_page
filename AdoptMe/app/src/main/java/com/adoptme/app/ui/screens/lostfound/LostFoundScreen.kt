package com.adoptme.app.ui.screens.lostfound

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.adoptme.app.data.model.LostFoundType
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.CyanNeon
import com.adoptme.app.ui.theme.LavenderMist
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ContentViewModel

@Composable
fun LostFoundScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: ContentViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val posts by vm.lostFound.collectAsState()

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Lost & Found", onBack = onBack)
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(posts, key = { it.id }) { post ->
                    AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(14.dp)) {
                            AsyncImage(
                                model = post.imageUrl,
                                contentDescription = post.animalName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(96.dp)
                                    .height(96.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(LavenderMist)
                            )
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                val accent = if (post.type == LostFoundType.LOST) PinkNeon else CyanNeon
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(accent.copy(0.18f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(post.type.name, color = accent, style = MaterialTheme.typography.labelMedium)
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(post.animalName, style = MaterialTheme.typography.titleLarge)
                                Text(post.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 3)
                                Spacer(Modifier.height(6.dp))
                                Text("${post.location} · ${post.date}", style = MaterialTheme.typography.labelSmall)
                                Text(post.contact, style = MaterialTheme.typography.labelMedium, color = accent)
                            }
                        }
                    }
                }
            }
        }
    }
}
