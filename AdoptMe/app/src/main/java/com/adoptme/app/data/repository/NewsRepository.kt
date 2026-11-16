package com.adoptme.app.data.repository

import com.adoptme.app.data.local.dao.NewsDao
import com.adoptme.app.data.model.NewsArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewsRepository(private val newsDao: NewsDao) {
    val articles: Flow<List<NewsArticle>> = newsDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun article(id: String): Flow<NewsArticle?> = newsDao.observeById(id).map { it?.toDomain() }
}
