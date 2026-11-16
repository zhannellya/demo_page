package com.adoptme.app.ui.screens.lostfound

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.adoptme.app.R
import com.adoptme.app.data.local.SeedData
import com.adoptme.app.data.model.LostFoundType
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@Composable
fun LostFoundScreen(onBack: () -> Unit) {
    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader("Потерянные и найденные", onBack)
            Column(Modifier.padding(horizontal = 16.dp)) {
                DemoBanner("Объявления учебные. Место не указано, чтобы пример нельзя было принять за реальный адрес.")
                Spacer(Modifier.height(12.dp))
                SeedData.lostFound.forEach { post ->
                    AdoptMeCard(Modifier.padding(bottom = 12.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                if (post.type == LostFoundType.LOST) "Ищут" else "Нашли",
                                color = PurplePrimary,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(post.animalName, style = MaterialTheme.typography.titleLarge)
                            Text("${post.species} · ${post.date}", color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            AsyncImage(
                                model = post.imageUrl,
                                contentDescription = post.animalName,
                                placeholder = painterResource(R.drawable.ic_paw),
                                error = painterResource(R.drawable.ic_paw),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(post.description, color = TextSecondary)
                            Spacer(Modifier.height(6.dp))
                            Text(post.location, color = TextSecondary)
                            Text(post.contact, color = TextSecondary)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
