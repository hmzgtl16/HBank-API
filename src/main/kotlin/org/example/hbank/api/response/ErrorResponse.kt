package org.example.hbank.api.response

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName(value = "timestamp")
    val timestamp: Instant,
    @SerialName(value = "status")
    val status: Int,
    @SerialName(value = "error")
    val error: String,
    @SerialName(value = "message")
    val message: String,
    @SerialName(value = "path")
    val path: String
)

