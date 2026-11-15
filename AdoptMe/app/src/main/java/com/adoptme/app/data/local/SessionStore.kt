package com.adoptme.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "adoptme_session")

class SessionStore(private val context: Context) {

    private val onboardingDone = booleanPreferencesKey("onboarding_done")
    private val isLoggedIn = booleanPreferencesKey("is_logged_in")
    private val userName = stringPreferencesKey("user_name")
    private val userEmail = stringPreferencesKey("user_email")
    private val userPhone = stringPreferencesKey("user_phone")
    private val favorites = stringSetPreferencesKey("favorites")

    val onboardingCompleted: Flow<Boolean> =
        context.dataStore.data.map { it[onboardingDone] ?: false }

    val loggedIn: Flow<Boolean> =
        context.dataStore.data.map { it[isLoggedIn] ?: false }

    val name: Flow<String> =
        context.dataStore.data.map { it[userName] ?: "" }

    val email: Flow<String> =
        context.dataStore.data.map { it[userEmail] ?: "" }

    val phone: Flow<String> =
        context.dataStore.data.map { it[userPhone] ?: "" }

    val favoriteIds: Flow<Set<String>> =
        context.dataStore.data.map { it[favorites] ?: emptySet() }

    suspend fun setOnboardingCompleted(done: Boolean) {
        context.dataStore.edit { it[onboardingDone] = done }
    }

    suspend fun saveUser(name: String, email: String, phone: String = "") {
        context.dataStore.edit {
            it[userName] = name
            it[userEmail] = email
            it[userPhone] = phone
            it[isLoggedIn] = true
        }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[isLoggedIn] = false
        }
    }

    suspend fun toggleFavorite(animalId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[favorites]?.toMutableSet() ?: mutableSetOf()
            if (!current.add(animalId)) current.remove(animalId)
            prefs[favorites] = current
        }
    }
}
