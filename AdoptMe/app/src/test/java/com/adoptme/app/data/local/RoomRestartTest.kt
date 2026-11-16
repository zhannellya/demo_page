package com.adoptme.app.data.local

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.adoptme.app.data.local.entity.FavoriteEntity
import com.adoptme.app.data.model.AnimalStatus
import com.adoptme.app.data.model.ApplicationDraft
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.repository.ApplicationSubmission
import com.adoptme.app.data.repository.RoomApplicationGateway
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = Application::class)
class RoomRestartTest {
    @Test
    fun applicationAndFavoriteSurviveDatabaseReopen() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val name = "adoptme-restart"
        context.deleteDatabase(name)

        val first = Room.databaseBuilder(context, AdoptMeDatabase::class.java, name).build()
        val saved = ApplicationSubmission(RoomApplicationGateway(first.applicationDao()))
            .submit(draft())
            .getOrThrow()
        first.favoriteDao().insert(FavoriteEntity("user-1", "sharik"))
        first.close()

        val second = Room.databaseBuilder(context, AdoptMeDatabase::class.java, name).build()
        val restored = second.applicationDao().findById(saved.id)
        assertEquals(saved.number, restored?.number)
        assertEquals(ApplicationStatus.PENDING.name, restored?.status)
        assertEquals(1, second.favoriteDao().count("user-1", "sharik"))
        second.applicationDao().updateStatus(saved.id, ApplicationStatus.APPROVED.name)
        second.close()

        val third = Room.databaseBuilder(context, AdoptMeDatabase::class.java, name).build()
        assertEquals(ApplicationStatus.APPROVED.name, third.applicationDao().findById(saved.id)?.status)
        assertNull(third.applicationDao().findPending("user-1", "sharik"))
        third.close()
    }

    private fun draft() = ApplicationDraft(
        userId = "user-1",
        animalId = "sharik",
        animalName = "Шарик",
        animalStatus = AnimalStatus.AVAILABLE,
        applicantName = "Анна",
        email = "anna@adoptme.app",
        phone = "+7 900 000-00-00",
        address = "улица Ленина 10",
        experience = "Раньше жила кошка",
        reason = "Хочу забрать Шарика и гулять с ним каждый день"
    )
}
