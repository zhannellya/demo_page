package com.adoptme.app.di

import android.content.Context
import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.repository.AnimalRepository
import com.adoptme.app.data.repository.AuthRepository

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val sessionStore = SessionStore(appContext)
    val authRepository = AuthRepository(sessionStore)
    val animalRepository = AnimalRepository(sessionStore)
}
