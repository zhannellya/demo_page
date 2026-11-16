package com.adoptme.app.ui.screens.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.formatDateTime
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.NewsDetailViewModel
import com.adoptme.app.viewmodel.NewsListViewModel

@Composable
fun NewsScreen(onBack: () -> Unit, onOpen: (String) -> Unit) {
    val container = LocalAppContainer.current
    val vm: NewsListViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val articles by vm.articles.collectAsState()

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Новости", onBack = onBack)
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(articles, key = { it.id }) { article ->
                    AdoptMeCard(modifier = Modifier.clickable { onOpen(article.id) }) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(article.category, color = PurplePrimary, fontWeight = FontWeight.SemiBold)
                            Text(article.title, style = MaterialTheme.typography.titleLarge)
                            Text(article.summary, color = TextSecondary)
                            Text(formatDateTime(article.publishedAt), color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsDetailScreen(newsId: String, onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: NewsDetailViewModel = viewModel(factory = AdoptMeViewModelFactory(container, newsId))
    val article by vm.article.collectAsState()

    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(article?.title ?: "Новость", onBack = onBack)
            article?.let { item ->
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.category, color = PurplePrimary, fontWeight = FontWeight.SemiBold)
                    Text(formatDateTime(item.publishedAt), color = TextSecondary)
                    Text(item.body, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
