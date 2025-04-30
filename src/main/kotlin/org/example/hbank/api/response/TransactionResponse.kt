package org.example.hbank.api.response

import org.example.hbank.api.utility.TransactionStatus
import org.example.hbank.api.utility.TransactionType
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponse(

    @SerialName(value = "reference")
    val reference: String,

    @SerialName(value = "created")
    val created: Instant,

    @SerialName(value = "modified")
    val modified: Instant,

    @SerialName(value = "amount")
    val amount: Double,

    @SerialName(value = "fees")
    val fees: Double,

    @SerialName(value = "type")
    val type: TransactionType,

    @SerialName(value = "status")
    val status: TransactionStatus,

    @SerialName(value = "from")
    val from: AccountResponse,

    @SerialName(value = "to")
    val to: AccountResponse
)

