@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.adoptme.app.data.repository

import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.dao.NewsDao
import com.adoptme.app.data.local.dao.NotificationDao
import com.adoptme.app.data.local.dao.ShelterDao
import com.adoptme.app.data.local.toModel
import com.adoptme.app.data.model.AppNotification
import com.adoptme.app.data.model.NewsArticle
import com.adoptme.app.data.model.Shelter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ContentRepository(
    private val shelterDao: ShelterDao,
    private val newsDao: NewsDao,
    private val notificationDao: NotificationDao,
    private val sessionStore: SessionStore
) {
    fun observeShelters(): Flow<List<Shelter>> =
        shelterDao.observeAll().map { rows -> rows.map { it.toModel() } }

    fun observeNews(): Flow<List<NewsArticle>> =
        newsDao.observeAll().map { rows -> rows.map { it.toModel() } }

    fun observeArticle(id: String): Flow<NewsArticle?> =
        newsDao.observeById(id).map { it?.toModel() }

    fun observeNotifications(): Flow<List<AppNotification>> {
        return sessionStore.session.flatMapLatest { snapshot ->
            if (snapshot.userId.isBlank()) {
                flowOf(emptyList())
            } else {
                notificationDao.observeForUser(snapshot.userId).map { rows -> rows.map { it.toModel() } }
            }
        }
    }

    val notificationsEnabled: Flow<Boolean> = sessionStore.notifications

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        sessionStore.setNotificationsEnabled(enabled)
    }

    suspend fun markNotificationRead(id: String) {
        try {
            notificationDao.markRead(id)
        } catch (_: Exception) {
            // Reading state is not critical for the adoption record.
        }
    }
}
