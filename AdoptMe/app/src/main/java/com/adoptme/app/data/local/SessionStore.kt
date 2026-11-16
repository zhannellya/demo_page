package com.adoptme.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adoptme.app.data.model.SessionSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "adoptme_session")

class SessionStore(private val context: Context) {
    private val onboardingDone = booleanPreferencesKey("onboarding_done")
    private val userIdKey = stringPreferencesKey("user_id")
    private val notificationsEnabled = booleanPreferencesKey("notifications_enabled")

    val session: Flow<SessionSnapshot> = context.sessionDataStore.data.map { prefs ->
        SessionSnapshot(
            onboardingDone = prefs[onboardingDone] ?: false,
            userId = prefs[userIdKey].orEmpty()
        )
    }

    val notifications: Flow<Boolean> =
        context.sessionDataStore.data.map { it[notificationsEnabled] ?: true }

    suspend fun currentUserId(): String? =
        session.map { it.userId.takeIf(String::isNotBlank) }.first()

    suspend fun setOnboardingCompleted() {
        context.sessionDataStore.edit { it[onboardingDone] = true }
    }

    suspend fun startSession(userId: String) {
        context.sessionDataStore.edit { it[userIdKey] = userId }
    }

    suspend fun logout() {
        context.sessionDataStore.edit { it[userIdKey] = "" }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.sessionDataStore.edit { it[notificationsEnabled] = enabled }
    }
}
