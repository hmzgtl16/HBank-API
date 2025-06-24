package org.example.hbank.api.request

import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmRequestRequest(
    @NotBlank(message = "Token must not be blank")
    @SerialName(value = "token")
    val token: String
)
