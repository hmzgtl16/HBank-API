package org.example.hbank.api.response

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.hbank.api.util.TransferStatus

@Serializable
data class TransferResponse(
    @SerialName(value = "reference")
    val reference: String,

    @SerialName(value = "created")
    val created: Instant,

    @SerialName(value = "amount")
    val amount: Double,

    @SerialName(value = "status")
    val status: TransferStatus,

    @SerialName(value = "beneficiary")
    val beneficiary: AccountResponse
)




