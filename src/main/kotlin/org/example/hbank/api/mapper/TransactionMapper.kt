package org.example.hbank.api.mapper

import kotlinx.datetime.toKotlinInstant
import org.example.hbank.api.model.Account
import org.example.hbank.api.model.File
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.request.CreateRequestRequest
import org.example.hbank.api.request.CreateTransferRequest
import org.example.hbank.api.response.AccountResponse
import org.example.hbank.api.response.TransactionResponse
import org.example.hbank.api.util.TransactionStatus
import org.example.hbank.api.util.TransactionType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.Clock
import java.util.*

@Component
class TransactionMapper(
    private val clock: Clock
) {

    fun toEntity(
        reference: UUID,
        amount: Double,
        type: TransactionType,
        from: Account,
        to: Account
    ): Transaction =
        Transaction(
            reference = reference,
            amount = amount,
            status = TransactionStatus.CREATED,
            type = type,
            from = from,
            to = to,
            createdAt = clock.instant(),
            modifiedAt = clock.instant()
        )

    fun updateEntity(transaction: Transaction): Transaction =
        transaction.copy(modifiedAt = clock.instant())

    fun toResponse(transaction: Transaction): TransactionResponse = TransactionResponse(
        transaction.reference,
        amount = transaction.amount,
        fees = transaction.fees,
        status = transaction.status,
        type = transaction.type,
        from = toResponse(account = transaction.from),
        to = toResponse(account = transaction.to),
        createdAt = transaction.createdAt.toKotlinInstant(),
        modifiedAt = transaction.modifiedAt.toKotlinInstant()
    )

    private fun toResponse(account: Account): AccountResponse = AccountResponse(
        name = account.name,
        number = account.number,
        avatar = avatarUrl(avatar = account.customer.avatar)
    )

    private fun avatarUrl(avatar: File?): String? = avatar?.let {
        ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/v1/customer/avatar")
            .path(it.id.toString())
            .toUriString()
    }
}