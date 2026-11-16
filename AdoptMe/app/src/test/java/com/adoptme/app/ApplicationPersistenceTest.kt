package com.adoptme.app

import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.ApplicationDraft
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.repository.ApplicationCommands
import com.adoptme.app.data.repository.ApplicationFactory
import com.adoptme.app.data.repository.ApplicationService
import com.adoptme.app.utils.ClickLock
import com.adoptme.app.utils.PasswordHasher
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ApplicationPersistenceTest {

    @Test
    fun applicationSurvivesNewServiceInstanceAndStatusChange() = runBlocking {
        val store = InMemoryApplicationCommands()
        val first = ApplicationService(store)
        val saved = first.submit(draft()).getOrThrow()

        assertEquals("ADOPT-000001", saved.number)
        assertEquals(ApplicationStatus.PENDING, saved.status)

        val reopened = ApplicationService(store)
        val loaded = reopened.cancel(saved.id, saved.userId).getOrThrow()
        assertEquals(ApplicationStatus.CANCELLED, loaded.status)
        assertEquals(saved.number, store.all().single().number)
        assertEquals(ApplicationStatus.CANCELLED, store.get(saved.id)?.status)
    }

    @Test
    fun repeatedSubmitDoesNotCreateSecondPendingApplication() = runBlocking {
        val store = InMemoryApplicationCommands()
        val service = ApplicationService(store)
        val first = service.submit(draft()).getOrThrow()
        val second = service.submit(draft(animalId = "sharik"))

        assertTrue(second.isFailure)
        assertEquals(ApplicationFactory.DUPLICATE_MESSAGE, second.exceptionOrNull()?.message)
        assertEquals(listOf(first.id), store.all().map { it.id })
    }

    @Test
    fun numbersIncreaseAndReviewLocksTheDecision() = runBlocking {
        val store = InMemoryApplicationCommands()
        val service = ApplicationService(store)
        val first = service.submit(draft(animalId = "sharik", animalName = "Шарик")).getOrThrow()
        val second = service.submit(draft(animalId = "tosha", animalName = "Тоша")).getOrThrow()
        assertEquals("ADOPT-000002", second.number)

        val approved = service.review(first.id, approve = true).getOrThrow()
        assertEquals(ApplicationStatus.APPROVED, approved.status)
        assertTrue(service.review(first.id, approve = false).isFailure)
        assertTrue(service.cancel(first.id, first.userId).isFailure)

        val rejected = service.review(second.id, approve = false).getOrThrow()
        assertEquals(ApplicationStatus.REJECTED, rejected.status)
    }

    @Test
    fun clickLockIgnoresASecondPressUntilRelease() {
        val lock = ClickLock()
        assertTrue(lock.tryAcquire())
        assertFalse(lock.tryAcquire())
        lock.release()
        assertTrue(lock.tryAcquire())
    }

    @Test
    fun passwordHashIsNotPlainTextAndVerifies() {
        val salt = PasswordHasher.generateSalt()
        val otherSalt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hash("Admin12345", salt)
        assertNotEquals("Admin12345", hash)
        assertTrue(PasswordHasher.verify("Admin12345", salt, hash))
        assertFalse(PasswordHasher.verify("wrong-password", salt, hash))
        assertNotEquals(hash, PasswordHasher.hash("Admin12345", otherSalt))
    }

    private fun draft(
        animalId: String = "sharik",
        animalName: String = "Шарик"
    ) = ApplicationDraft(
        userId = "user-1",
        animalId = animalId,
        animalName = animalName,
        applicantName = "Иван Петров",
        email = "Ivan@Example.com",
        phone = "+7 900 111 22 33",
        address = "Москва, учебный адрес",
        experience = "Раньше жила кошка",
        reason = "Хочу заботиться о собаке"
    )
}

private class InMemoryApplicationCommands : ApplicationCommands {
    private val items = mutableListOf<AdoptionApplication>()

    override suspend fun insertNew(draft: ApplicationDraft, submittedAt: Long): AdoptionApplication {
        synchronized(items) {
            val pending = items.any {
                it.userId == draft.userId &&
                    it.animalId == draft.animalId &&
                    it.status == ApplicationStatus.PENDING
            }
            if (pending) throw IllegalStateException(ApplicationFactory.DUPLICATE_MESSAGE)
            val sequence = (items.maxOfOrNull { it.sequence } ?: 0L) + 1
            val application = ApplicationFactory.create(draft, sequence, submittedAt)
            items += application
            return application
        }
    }

    override suspend fun get(id: String): AdoptionApplication? = synchronized(items) {
        items.find { it.id == id }
    }

    override suspend fun update(application: AdoptionApplication) {
        synchronized(items) {
            val index = items.indexOfFirst { it.id == application.id }
            if (index >= 0) items[index] = application
        }
    }

    override suspend fun all(): List<AdoptionApplication> = synchronized(items) { items.toList() }
}
