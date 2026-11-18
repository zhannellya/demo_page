package com.adoptme.app.data.repository

import com.adoptme.app.data.local.db.DonationDao
import com.adoptme.app.data.local.db.DonationEntity
import com.adoptme.app.data.local.db.toDomain
import com.adoptme.app.data.model.Donation
import com.adoptme.app.data.model.DonationReceipt
import com.adoptme.app.data.model.DonationRequest
import com.adoptme.app.data.payment.PaymentService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DonationRepository(
    private val donationDao: DonationDao,
    private val paymentService: PaymentService
) {
    fun observeHistory(): Flow<List<Donation>> =
        donationDao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun donate(request: DonationRequest): Result<DonationReceipt> {
        val result = paymentService.process(request)
        result.getOrNull()?.let { receipt ->
            donationDao.insert(
                DonationEntity(
                    id = receipt.id,
                    shelterId = receipt.shelterId,
                    purpose = receipt.purpose,
                    amountKzt = receipt.amountKzt,
                    createdAt = receipt.createdAt,
                    status = receipt.status.name
                )
            )
        }
        return result
    }
}
