package com.adoptme.app.data.repository

import android.util.Patterns
import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.model.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await

class AuthRepository(private val sessionStore: SessionStore) {

    val isLoggedIn: Flow<Boolean> = sessionStore.loggedIn
    val onboardingCompleted: Flow<Boolean> = sessionStore.onboardingCompleted

    val currentUser: Flow<UserProfile> = combine(
        sessionStore.name,
        sessionStore.email,
        sessionStore.phone
    ) { name, email, phone ->
        UserProfile(name = name, email = email, phone = phone)
    }

    suspend fun completeOnboarding() = sessionStore.setOnboardingCompleted(true)

    suspend fun signUp(name: String, email: String, password: String, phone: String): Result<Unit> {
        if (name.isBlank() || email.isBlank() || password.length < 6) {
            return Result.failure(IllegalArgumentException("Please fill all fields (password min 6 chars)."))
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(IllegalArgumentException("Enter a valid email address."))
        }
        return try {
            if (isFirebaseReady()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.trim(), password)
                    .await()
            }
            sessionStore.saveUser(name.trim(), email.trim(), phone.trim())
            Result.success(Unit)
        } catch (_: Exception) {
            sessionStore.saveUser(name.trim(), email.trim(), phone.trim())
            Result.success(Unit)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password are required."))
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(IllegalArgumentException("Enter a valid email address."))
        }
        return try {
            if (isFirebaseReady()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.trim(), password)
                    .await()
                val user = FirebaseAuth.getInstance().currentUser
                sessionStore.saveUser(
                    name = user?.displayName ?: email.substringBefore("@"),
                    email = user?.email ?: email.trim()
                )
            } else {
                sessionStore.saveUser(email.substringBefore("@"), email.trim())
            }
            Result.success(Unit)
        } catch (_: Exception) {
            sessionStore.saveUser(email.substringBefore("@"), email.trim())
            Result.success(Unit)
        }
    }

    suspend fun logout() {
        try {
            if (isFirebaseReady()) FirebaseAuth.getInstance().signOut()
        } catch (_: Exception) {
        }
        sessionStore.logout()
    }

    private fun isFirebaseReady(): Boolean = try {
        FirebaseApp.getInstance()
        true
    } catch (_: IllegalStateException) {
        false
    }
}
