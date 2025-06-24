package org.example.hbank.api.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.hbank.api.util.Errors
import org.example.hbank.api.util.UUIDSerializer
import org.example.hbank.api.util.Validator
import java.util.*

@Serializable
data class VerifyTransferRequest(
    @NotBlank(message = Errors.TRANSACTION_REFERENCE_IS_BLANK)
    @Pattern(regexp = Validator.UUID_REGEX, message = Errors.TRANSACTION_REFERENCE_INVALID_FORMAT)
    @Serializable(with = UUIDSerializer::class)
    @SerialName(value = "reference")
    val reference: UUID
)
