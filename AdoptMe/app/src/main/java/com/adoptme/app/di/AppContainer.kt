package com.adoptme.app.di

import android.content.Context
import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.db.AdoptMeDatabase
import com.adoptme.app.data.repository.AnimalRepository
import com.adoptme.app.data.repository.AuthRepository

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = AdoptMeDatabase.create(appContext)
    val sessionStore = SessionStore(appContext)
    val authRepository = AuthRepository(sessionStore, database.userDao())
    val animalRepository = AnimalRepository(
        sessionStore = sessionStore,
        animalDao = database.animalDao(),
        favoriteDao = database.favoriteDao(),
        applicationDao = database.applicationDao()
    )
}
