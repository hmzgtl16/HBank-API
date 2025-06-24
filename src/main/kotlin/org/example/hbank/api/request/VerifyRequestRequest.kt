package org.example.hbank.api.request

import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.hbank.api.util.UUIDSerializer
import java.util.UUID

@Serializable
data class VerifyRequestRequest(
    @NotBlank(message = "Reference must not be blank")
    @Serializable(with = UUIDSerializer::class)
    @SerialName(value = "reference")
    val reference: UUID
)
