package com.adoptme.app.data.repository

import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.ApplicationDraft
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.utils.AdoptionFormValidator
import java.util.UUID

interface ApplicationCommands {
    suspend fun insertNew(draft: ApplicationDraft, submittedAt: Long = System.currentTimeMillis()): AdoptionApplication
    suspend fun get(id: String): AdoptionApplication?
    suspend fun update(application: AdoptionApplication)
    suspend fun all(): List<AdoptionApplication>
}

object ApplicationFactory {
    const val DUPLICATE_MESSAGE =
        "У вас уже есть заявка на рассмотрении по этому животному."
    const val SAVE_FAILED = "Не удалось сохранить заявку. Попробуйте ещё раз."
    const val NOT_FOUND = "Заявка не найдена."
    const val FOREIGN = "Нельзя изменить чужую заявку."
    const val CANCEL_ONLY_PENDING =
        "Отменить можно только заявку со статусом «На рассмотрении»."
    const val ALREADY_DECIDED = "Решение по этой заявке уже принято."

    fun create(draft: ApplicationDraft, sequence: Long, submittedAt: Long): AdoptionApplication {
        return AdoptionApplication(
            id = UUID.randomUUID().toString(),
            number = "ADOPT-%06d".format(sequence),
            sequence = sequence,
            animalId = draft.animalId,
            animalName = draft.animalName.trim(),
            userId = draft.userId,
            applicantName = draft.applicantName.trim(),
            email = draft.email.trim().lowercase(),
            phone = draft.phone.trim(),
            address = draft.address.trim(),
            experience = draft.experience.trim(),
            reason = draft.reason.trim(),
            status = ApplicationStatus.PENDING,
            submittedAt = submittedAt
        )
    }
}

class ApplicationService(private val commands: ApplicationCommands) {

    suspend fun submit(draft: ApplicationDraft): Result<AdoptionApplication> {
        AdoptionFormValidator.validate(
            draft.applicantName,
            draft.phone,
            draft.email,
            draft.address,
            draft.experience,
            draft.reason
        )?.let { return Result.failure(IllegalArgumentException(it)) }

        if (draft.userId.isBlank() || draft.animalId.isBlank()) {
            return Result.failure(IllegalArgumentException("Не удалось определить животное или пользователя."))
        }

        return try {
            Result.success(commands.insertNew(draft))
        } catch (error: IllegalStateException) {
            Result.failure(IllegalStateException(error.message ?: ApplicationFactory.DUPLICATE_MESSAGE))
        } catch (_: Exception) {
            Result.failure(IllegalStateException(ApplicationFactory.SAVE_FAILED))
        }
    }

    suspend fun cancel(id: String, userId: String): Result<AdoptionApplication> {
        val current = commands.get(id) ?: return Result.failure(IllegalArgumentException(ApplicationFactory.NOT_FOUND))
        if (current.userId != userId) {
            return Result.failure(IllegalArgumentException(ApplicationFactory.FOREIGN))
        }
        if (current.status != ApplicationStatus.PENDING) {
            return Result.failure(IllegalStateException(ApplicationFactory.CANCEL_ONLY_PENDING))
        }
        val updated = current.copy(status = ApplicationStatus.CANCELLED)
        commands.update(updated)
        return Result.success(updated)
    }

    suspend fun review(id: String, approve: Boolean): Result<AdoptionApplication> {
        val current = commands.get(id) ?: return Result.failure(IllegalArgumentException(ApplicationFactory.NOT_FOUND))
        if (current.status != ApplicationStatus.PENDING) {
            return Result.failure(IllegalStateException(ApplicationFactory.ALREADY_DECIDED))
        }
        val updated = current.copy(
            status = if (approve) ApplicationStatus.APPROVED else ApplicationStatus.REJECTED
        )
        commands.update(updated)
        return Result.success(updated)
    }
}
