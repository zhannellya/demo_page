package com.adoptme.app.ui.screens.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.utils.label
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.NewsDetailViewModel
import com.adoptme.app.viewmodel.NewsViewModel

@Composable
fun NewsScreen(onBack: () -> Unit, onOpen: (String) -> Unit) {
    val container = LocalAppContainer.current
    val vm: NewsViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val articles by vm.articles.collectAsState()

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Новости", onBack)
            if (articles.isEmpty()) {
                EmptyState("Новостей нет", "Лента появится после подготовки локальной базы.")
            } else {
                LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                    items(articles, key = { it.id }) { article ->
                        AdoptMeCard(
                            Modifier
                                .padding(bottom = 12.dp)
                                .clickable { onOpen(article.id) }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(article.category.label(), color = PurplePrimary, style = MaterialTheme.typography.labelMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(article.title, style = MaterialTheme.typography.titleLarge)
                                Text(article.summary, color = TextSecondary)
                                Text(article.publishedLabel, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsDetailScreen(articleId: String, onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: NewsDetailViewModel = viewModel(
        key = articleId,
        factory = AdoptMeViewModelFactory(container, articleId = articleId)
    )
    val article by vm.article.collectAsState()

    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(article?.category?.label() ?: "Новость", onBack)
            Column(Modifier.padding(horizontal = 20.dp)) {
                val item = article
                if (item == null) {
                    Text("Материал не найден.", color = TextSecondary)
                } else {
                    Text(item.title, style = MaterialTheme.typography.headlineSmall, color = PurplePrimary)
                    Text(item.publishedLabel, color = TextSecondary)
                    Spacer(Modifier.height(12.dp))
                    Text(item.body, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
