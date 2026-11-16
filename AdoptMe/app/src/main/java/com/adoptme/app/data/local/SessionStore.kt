package com.adoptme.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "adoptme_session")

data class SessionSnapshot(
    val onboardingDone: Boolean = false,
    val loggedIn: Boolean = false,
    val userId: String = "",
    val notificationsEnabled: Boolean = true
)

class SessionStore(private val context: Context) {
    private val onboardingDone = booleanPreferencesKey("onboarding_done")
    private val isLoggedIn = booleanPreferencesKey("is_logged_in")
    private val userId = stringPreferencesKey("user_id")
    private val notifications = booleanPreferencesKey("notifications_enabled")

    val session: Flow<SessionSnapshot> = context.sessionDataStore.data.map { prefs ->
        SessionSnapshot(
            onboardingDone = prefs[onboardingDone] ?: false,
            loggedIn = prefs[isLoggedIn] ?: false,
            userId = prefs[userId] ?: "",
            notificationsEnabled = prefs[notifications] ?: true
        )
    }

    suspend fun snapshot(): SessionSnapshot = session.first()

    suspend fun setOnboardingCompleted(done: Boolean) {
        context.sessionDataStore.edit { it[onboardingDone] = done }
    }

    suspend fun saveLogin(id: String) {
        context.sessionDataStore.edit {
            it[userId] = id
            it[isLoggedIn] = true
        }
    }

    suspend fun logout() {
        context.sessionDataStore.edit {
            it[isLoggedIn] = false
            it[userId] = ""
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.sessionDataStore.edit { it[notifications] = enabled }
    }
}
