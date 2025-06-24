package org.example.hbank.api.response

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.hbank.api.util.AccountLimit
import org.example.hbank.api.util.AccountStatus

@Serializable
data class PersonalAccountResponse(
    @SerialName(value = "name") val name: String,
    @SerialName(value = "number") val number: String,
    @SerialName(value = "balance") val balance: Double,
    @SerialName(value = "status") val status: AccountStatus,
    @SerialName(value = "limit") val limit: AccountLimit,
    @SerialName(value = "token") val token: String,
    @SerialName(value = "modified_at") val modifiedAt: Instant
)

