package com.adoptme.app

import android.app.Application
import com.adoptme.app.di.AppContainer
import com.google.firebase.FirebaseApp

class AdoptMeApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
        } catch (_: Exception) {
            // google-services.json may be missing — local mode still works
        }
        container = AppContainer(this)
    }
}
