package org.example.hbank.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountResponse(
    @SerialName(value = "name")
    val name: String,

    @SerialName(value = "number")
    val number: String,

    @SerialName(value = "avatar")
    val avatar: String?
)
