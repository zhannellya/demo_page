package com.adoptme.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "adoptme_session")

class SessionStore(private val context: Context) {
    private val userIdKey = longPreferencesKey("user_id")

    val userId: Flow<Long?> = context.dataStore.data.map { prefs -> prefs[userIdKey] }

    suspend fun setUserId(id: Long) {
        context.dataStore.edit { prefs -> prefs[userIdKey] = id }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs -> prefs.remove(userIdKey) }
    }
}
