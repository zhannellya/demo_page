package com.adoptme.app.di

import android.content.Context
import com.adoptme.app.data.local.AdoptMeDatabase
import com.adoptme.app.data.local.DatabaseSeeder
import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.repository.AnimalRepository
import com.adoptme.app.data.repository.ApplicationRepository
import com.adoptme.app.data.repository.ApplicationService
import com.adoptme.app.data.repository.AuthRepository
import com.adoptme.app.data.repository.ChatRepository
import com.adoptme.app.data.repository.ContentRepository
import com.adoptme.app.data.repository.RoomApplicationCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database: AdoptMeDatabase = AdoptMeDatabase.create(appContext)
    val sessionStore = SessionStore(appContext)

    private val applicationService = ApplicationService(
        RoomApplicationCommands(database, database.applicationDao())
    )

    val authRepository = AuthRepository(database.userDao(), sessionStore)
    val animalRepository = AnimalRepository(
        database.animalDao(),
        database.favoriteDao(),
        sessionStore
    )
    val applicationRepository = ApplicationRepository(
        database.applicationDao(),
        database.animalDao(),
        database.notificationDao(),
        database.userDao(),
        sessionStore,
        applicationService
    )
    val chatRepository = ChatRepository(database.chatDao())
    val contentRepository = ContentRepository(
        database.shelterDao(),
        database.newsDao(),
        database.notificationDao(),
        sessionStore
    )

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()
    private val _startupError = MutableStateFlow<String?>(null)
    val startupError: StateFlow<String?> = _startupError.asStateFlow()

    init {
        seed()
    }

    fun seed() {
        _ready.value = false
        scope.launch {
            try {
                DatabaseSeeder(database).seed()
                _startupError.value = null
            } catch (_: Exception) {
                _startupError.value = "Не удалось подготовить локальную базу. Попробуйте ещё раз."
            } finally {
                _ready.value = true
            }
        }
    }
}
