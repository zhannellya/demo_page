package com.adoptme.app.di

import android.content.Context
import com.adoptme.app.data.local.AdoptMeDatabase
import com.adoptme.app.data.local.DatabaseSeeder
import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.repository.ApplicationSubmission
import com.adoptme.app.data.repository.AuthRepository
import com.adoptme.app.data.repository.CatalogRepository
import com.adoptme.app.data.repository.ChatRepository
import com.adoptme.app.data.repository.NewsRepository
import com.adoptme.app.data.repository.RoomApplicationGateway
import com.adoptme.app.remote.UnavailableAuthRemoteDataSource
import com.adoptme.app.remote.UnavailableCatalogRemoteDataSource
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val ready = CompletableDeferred<Unit>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AdoptMeDatabase = AdoptMeDatabase.build(appContext)
    val sessionStore = SessionStore(appContext)

    val authRepository = AuthRepository(
        userDao = database.userDao(),
        sessionStore = sessionStore,
        remote = UnavailableAuthRemoteDataSource()
    )

    val catalogRepository = CatalogRepository(
        animalDao = database.animalDao(),
        shelterDao = database.shelterDao(),
        favoriteDao = database.favoriteDao(),
        applicationDao = database.applicationDao(),
        sessionStore = sessionStore,
        submission = ApplicationSubmission(RoomApplicationGateway(database.applicationDao())),
        remote = UnavailableCatalogRemoteDataSource()
    )

    val chatRepository = ChatRepository(database.chatDao(), sessionStore)
    val newsRepository = NewsRepository(database.newsDao())

    init {
        scope.launch {
            try {
                DatabaseSeeder(database).seed()
                ready.complete(Unit)
            } catch (error: Throwable) {
                ready.completeExceptionally(error)
            }
        }
    }

    suspend fun awaitReady() {
        ready.await()
    }
}
