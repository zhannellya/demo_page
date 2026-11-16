package com.adoptme.app.data.remote

/**
 * Seam for a future Firebase Authentication backend.
 *
 * The app does not depend on the Firebase SDK. Shipping that SDK without
 * google-services.json makes FirebaseInitProvider crash the process at launch.
 * When a real project is connected, implement this interface with Firebase Auth
 * and pass it into [com.adoptme.app.data.repository.AuthRepository]. Firestore
 * can later mirror animals and applications; Room remains the offline cache.
 */
interface AuthRemoteDataSource {
    val isEnabled: Boolean

    suspend fun register(email: String, password: String): Result<String>

    suspend fun signIn(email: String, password: String): Result<String>
}

class DisabledAuthRemoteDataSource : AuthRemoteDataSource {
    override val isEnabled: Boolean = false

    override suspend fun register(email: String, password: String): Result<String> =
        Result.failure(IllegalStateException("Серверная регистрация не подключена."))

    override suspend fun signIn(email: String, password: String): Result<String> =
        Result.failure(IllegalStateException("Серверный вход не подключён."))
}
