package com.adoptme.app.data.repository

import androidx.room.withTransaction
import com.adoptme.app.data.local.AdoptMeDatabase
import com.adoptme.app.data.local.dao.ApplicationDao
import com.adoptme.app.data.local.toEntity
import com.adoptme.app.data.local.toModel
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.ApplicationDraft
import com.adoptme.app.data.model.ApplicationStatus

class RoomApplicationCommands(
    private val database: AdoptMeDatabase,
    private val dao: ApplicationDao
) : ApplicationCommands {

    override suspend fun insertNew(draft: ApplicationDraft, submittedAt: Long): AdoptionApplication {
        return database.withTransaction {
            val pending = dao.countStatus(draft.userId, draft.animalId, ApplicationStatus.PENDING)
            if (pending > 0) {
                throw IllegalStateException(ApplicationFactory.DUPLICATE_MESSAGE)
            }
            val sequence = (dao.maxSequence() ?: 0L) + 1
            val application = ApplicationFactory.create(draft, sequence, submittedAt)
            dao.insert(application.toEntity())
            application
        }
    }

    override suspend fun get(id: String): AdoptionApplication? = dao.get(id)?.toModel()

    override suspend fun update(application: AdoptionApplication) {
        dao.update(application.toEntity())
    }

    override suspend fun all(): List<AdoptionApplication> = dao.all().map { it.toModel() }
}
