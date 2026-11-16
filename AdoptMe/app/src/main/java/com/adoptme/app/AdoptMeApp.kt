package com.adoptme.app

import android.app.Application
import com.adoptme.app.di.AppContainer

class AdoptMeApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
