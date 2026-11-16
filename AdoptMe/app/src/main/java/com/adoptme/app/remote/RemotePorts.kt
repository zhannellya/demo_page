package com.adoptme.app.remote

/**
 * Точки подключения Firebase Authentication и Firestore.
 * Сейчас используется локальный демонстрационный режим: пароль хранится только как хеш в Room,
 * а роль администратора действует лишь на этом устройстве.
 *
 * Когда появится проект Firebase:
 * 1. Добавьте google-services.json и плагин com.google.gms.google-services.
 * 2. Подключите firebase-auth и firebase-firestore.
 * 3. Замените [UnavailableAuthRemoteDataSource] и [UnavailableCatalogRemoteDataSource]
 *    реализациями, которые вызывают FirebaseAuth и пишут заявки в коллекцию Firestore.
 * 4. Не считайте локальную роль LOCAL_ADMIN серверной, пока правила Firestore
 *    не проверяют custom claim администратора.
 */
interface AuthRemoteDataSource {
    suspend fun onLocalAccountReady(userId: String, email: String)
}

interface CatalogRemoteDataSource {
    fun prepare()
}

class UnavailableAuthRemoteDataSource : AuthRemoteDataSource {
    override suspend fun onLocalAccountReady(userId: String, email: String) = Unit
}

class UnavailableCatalogRemoteDataSource : CatalogRemoteDataSource {
    override fun prepare() = Unit
}
