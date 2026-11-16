package com.adoptme.app.data.repository

import com.adoptme.app.data.local.dao.ApplicationDao
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.AnimalStatus
import com.adoptme.app.data.model.ApplicationDraft
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.utils.FormValidators
import kotlinx.coroutines.sync.Mutex
import java.util.UUID

interface ApplicationGateway {
    suspend fun commit(draft: ApplicationDraft): AdoptionApplication?
    suspend fun listForUser(userId: String): List<AdoptionApplication>
    suspend fun updateStatus(id: String, status: ApplicationStatus)
}

class RoomApplicationGateway(private val dao: ApplicationDao) : ApplicationGateway {
    override suspend fun commit(draft: ApplicationDraft): AdoptionApplication? {
        val entity = draft.toEntity(UUID.randomUUID().toString())
        return dao.insertIfNoPending(entity)?.toDomain()
    }

    override suspend fun listForUser(userId: String): List<AdoptionApplication> {
        return emptyList()
    }

    override suspend fun updateStatus(id: String, status: ApplicationStatus) {
        dao.updateStatus(id, status.name)
    }
}

class ApplicationSubmission(private val gateway: ApplicationGateway) {
    private val gate = Mutex()

    suspend fun submit(draft: ApplicationDraft): Result<AdoptionApplication> {
        if (!gate.tryLock()) {
            return Result.failure(IllegalStateException("Заявка уже отправляется."))
        }
        try {
            val validation = FormValidators.validateApplication(
                name = draft.applicantName,
                phone = draft.phone,
                email = draft.email,
                address = draft.address,
                experience = draft.experience,
                reason = draft.reason
            )
            if (validation != null) {
                return Result.failure(IllegalArgumentException(validation))
            }
            if (draft.animalStatus == AnimalStatus.ADOPTED) {
                return Result.failure(IllegalStateException("Это животное уже нашло дом."))
            }
            val saved = gateway.commit(draft)
                ?: return Result.failure(
                    IllegalStateException("Заявка на это животное уже на рассмотрении.")
                )
            return Result.success(saved)
        } finally {
            gate.unlock()
        }
    }
}
