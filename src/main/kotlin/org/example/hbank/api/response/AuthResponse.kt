package org.example.hbank.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName(value = "access_token")
    val accessToken: String,
    @SerialName(value = "refresh_token")
    val refreshToken: String
)
