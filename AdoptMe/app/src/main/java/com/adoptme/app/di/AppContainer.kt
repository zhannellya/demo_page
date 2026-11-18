package com.adoptme.app.di

import android.content.Context
import com.adoptme.app.data.local.PetPhotoStore
import com.adoptme.app.data.local.SessionStore
import com.adoptme.app.data.local.db.AdoptMeDatabase
import com.adoptme.app.data.payment.DemoPaymentService
import com.adoptme.app.data.payment.PaymentService
import com.adoptme.app.data.repository.AnimalRepository
import com.adoptme.app.data.repository.AuthRepository
import com.adoptme.app.data.repository.ChatRepository
import com.adoptme.app.data.repository.DonationRepository

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = AdoptMeDatabase.create(appContext)
    val sessionStore = SessionStore(appContext)
    val petPhotoStore = PetPhotoStore(appContext)
    val paymentService: PaymentService = DemoPaymentService()
    val authRepository = AuthRepository(sessionStore, database.userDao())
    val animalRepository = AnimalRepository(
        sessionStore = sessionStore,
        animalDao = database.animalDao(),
        favoriteDao = database.favoriteDao(),
        applicationDao = database.applicationDao(),
        shelterDao = database.shelterDao()
    )
    val chatRepository = ChatRepository(
        chatDao = database.chatDao(),
        shelterDao = database.shelterDao()
    )
    val donationRepository = DonationRepository(
        donationDao = database.donationDao(),
        paymentService = paymentService
    )
}
