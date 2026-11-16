package com.adoptme.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.navigation.AdoptMeNavHost
import com.adoptme.app.ui.theme.AdoptMeTheme
import com.adoptme.app.ui.theme.Plum


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as AdoptMeApp
        setContent {
            AdoptMeTheme {
                CompositionLocalProvider(LocalAppContainer provides app.container) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Plum
                    ) {
                        AdoptMeNavHost()
                    }
                }
            }
        }
    }
}
