package org.example.hbank.api.request

import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @NotBlank(message = "Username must not be blank")
    @SerialName(value = "username")
    val username: String,

    @NotBlank(message = "Password must not be blank")
    @SerialName(value = "password")
    val password: String
)
