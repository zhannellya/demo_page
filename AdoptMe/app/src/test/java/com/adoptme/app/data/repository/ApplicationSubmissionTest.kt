package com.adoptme.app.data.repository

import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.AnimalStatus
import com.adoptme.app.data.model.ApplicationDraft
import com.adoptme.app.data.model.ApplicationStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class ApplicationSubmissionTest {
    @Test
    fun savesOneNumberedApplicationAndRejectsADuplicateClick() = runBlocking {
        val store = MemoryApplicationGateway()
        val desk = ApplicationSubmission(store)
        val first = desk.submit(draft())
        val second = desk.submit(draft())
        assertTrue(first.isSuccess)
        assertEquals("ADM-00001", first.getOrThrow().number)
        assertTrue(second.isFailure)
        assertEquals(1, store.listForUser("user-1").size)
    }

    @Test
    fun parallelClicksDoNotCreateTwoPendingApplications() = runBlocking {
        val store = MemoryApplicationGateway()
        val desk = ApplicationSubmission(store)
        val results = (1..6).map {
            async(Dispatchers.Default) { desk.submit(draft()) }
        }.awaitAll()
        assertEquals(1, results.count { it.isSuccess })
        assertEquals(1, store.listForUser("user-1").size)
    }

    @Test
    fun applicationStaysVisibleForANewDeskAndChangesStatus() = runBlocking {
        val store = MemoryApplicationGateway()
        val saved = ApplicationSubmission(store).submit(draft()).getOrThrow()
        val reopened = ApplicationSubmission(store)
        val visible = reopened.gatewayList(store, "user-1")
        assertEquals(saved.number, visible.single().number)
        store.updateStatus(saved.id, ApplicationStatus.APPROVED)
        assertEquals(ApplicationStatus.APPROVED, store.listForUser("user-1").single().status)
    }

    @Test
    fun invalidFormIsNotStored() = runBlocking {
        val store = MemoryApplicationGateway()
        val result = ApplicationSubmission(store).submit(draft().copy(phone = "12", reason = "мало"))
        assertTrue(result.isFailure)
        assertTrue(store.listForUser("user-1").isEmpty())
    }

    private fun ApplicationSubmission.gatewayList(
        store: MemoryApplicationGateway,
        userId: String
    ): List<AdoptionApplication> = runBlocking { store.listForUser(userId) }

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

class MemoryApplicationGateway : ApplicationGateway {
    private val items = mutableListOf<AdoptionApplication>()

    override suspend fun commit(draft: ApplicationDraft): AdoptionApplication? = synchronized(items) {
        val exists = items.any {
            it.userId == draft.userId &&
                it.animalId == draft.animalId &&
                it.status == ApplicationStatus.PENDING
        }
        if (exists) return null
        val saved = AdoptionApplication(
            id = UUID.randomUUID().toString(),
            number = "ADM-%05d".format(items.size + 1),
            userId = draft.userId,
            animalId = draft.animalId,
            animalName = draft.animalName,
            applicantName = draft.applicantName.trim(),
            email = draft.email.trim(),
            phone = draft.phone.trim(),
            address = draft.address.trim(),
            experience = draft.experience.trim(),
            reason = draft.reason.trim(),
            status = ApplicationStatus.PENDING,
            submittedAt = System.currentTimeMillis()
        )
        items += saved
        saved
    }

    override suspend fun listForUser(userId: String): List<AdoptionApplication> =
        synchronized(items) { items.filter { it.userId == userId }.toList() }

    override suspend fun updateStatus(id: String, status: ApplicationStatus) = synchronized(items) {
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0) items[index] = items[index].copy(status = status)
    }
}
