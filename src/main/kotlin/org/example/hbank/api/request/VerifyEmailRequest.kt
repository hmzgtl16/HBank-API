package org.example.hbank.api.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.hbank.api.util.Errors
import org.example.hbank.api.util.Validator

@Serializable
data class VerifyEmailRequest(
    @field:NotBlank(message = Errors.VERIFICATION_CODE_IS_BLANK)
    @field:Pattern(regexp = Validator.VERIFICATION_CODE_REGEX, message = Errors.VERIFICATION_CODE_INVALID)
    @SerialName(value = "verification_code")
    val verificationCode: String
)
