package com.adoptme.app.data.payment

import com.adoptme.app.data.model.DonationReceipt
import com.adoptme.app.data.model.DonationRequest
import com.adoptme.app.data.model.DonationStatus
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Abstraction over a future payment-provider SDK.
 * Implementations must never persist full card numbers, CVV/CVC or expiry.
 */
interface PaymentService {
    suspend fun process(request: DonationRequest): Result<DonationReceipt>
}

class DemoPaymentService : PaymentService {
    override suspend fun process(request: DonationRequest): Result<DonationReceipt> {
        if (request.amountKzt <= 0) {
            return Result.failure(IllegalArgumentException("Укажите сумму пожертвования."))
        }
        if (request.shelterId.isBlank()) {
            return Result.failure(IllegalArgumentException("Выберите приют."))
        }
        delay(1_400)
        return Result.success(
            DonationReceipt(
                id = UUID.randomUUID().toString(),
                shelterId = request.shelterId,
                purpose = request.purpose,
                amountKzt = request.amountKzt,
                createdAt = System.currentTimeMillis(),
                status = DonationStatus.TEST_SUCCESS
            )
        )
    }
}
