package org.example.hbank.api.response

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.hbank.api.util.RequestStatus

@Serializable
data class RequestResponse(

    @SerialName(value = "reference")
    val reference: String,

    @SerialName(value = "created")
    val created: Instant,

    @SerialName(value = "amount")
    val amount: Double,

    @SerialName(value = "status")
    val status: RequestStatus,

    @SerialName(value = "recipient")
    val recipient: AccountResponse
)




