package org.example.hbank.api.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.hbank.api.util.TransferIdentifierType
import org.example.hbank.api.util.TransferIdentifierTypeSerializer

@Serializable
data class CreateTransferRequest(
    @NotBlank(message = "Identifier type must not be blank")
    @Serializable(with = TransferIdentifierTypeSerializer::class)
    @SerialName(value = "identifier_type")
    val identifierType: TransferIdentifierType,
    @NotBlank(message = "Identifier value must not be blank")
    @SerialName(value = "identifier_value")
    val identifierValue: String,
    @Positive(message = "Amount must be positive")
    @Min(value = 100, message = "Amount must be greater than or equal to 100 DZD")
    @SerialName(value = "amount")
    val amount: Double
)

