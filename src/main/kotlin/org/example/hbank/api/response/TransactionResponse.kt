package org.example.hbank.api.response

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.hbank.api.util.TransactionStatus
import org.example.hbank.api.util.TransactionType
import org.example.hbank.api.util.UUIDSerializer
import java.util.*

@Serializable
data class TransactionResponse(

    @Serializable(with = UUIDSerializer::class)
    @SerialName(value = "reference")
    val reference: UUID,

    @SerialName(value = "amount")
    val amount: Double,

    @SerialName(value = "fees")
    val fees: Double,

    @SerialName(value = "status")
    val status: TransactionStatus,

    @SerialName(value = "type")
    val type: TransactionType,

    @SerialName(value = "from")
    val from: AccountResponse,

    @SerialName(value = "to")
    val to: AccountResponse,

    @SerialName(value = "created_at")
    val createdAt: Instant,

    @SerialName(value = "modified_at")
    val modifiedAt: Instant
)

