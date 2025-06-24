package org.example.hbank.api.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.hbank.api.util.Errors

@Serializable
data class ForgotPasswordRequest(
    @field:NotBlank(message = Errors.EMAIL_IS_BLANK)
    @field:Email(message = Errors.EMAIL_INVALID_FORMAT)
    @SerialName(value = "email")
    val email: String
)
