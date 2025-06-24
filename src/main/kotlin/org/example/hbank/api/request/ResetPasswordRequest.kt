package org.example.hbank.api.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.hbank.api.util.Errors
import org.example.hbank.api.util.Validator

@Serializable
data class ResetPasswordRequest(
    @field:NotBlank(message = Errors.VERIFICATION_CODE_IS_BLANK)
    @field:Pattern(regexp = Validator.VERIFICATION_CODE_REGEX, message = Errors.VERIFICATION_CODE_INVALID)
    @SerialName(value = "verification_code")
    val verificationCode: String,

    @field:NotBlank(message = Errors.PASSWORD_IS_BLANK)
    @field:Size(min = 8, max = 64, message = Errors.PASSWORD_INVALID_SIZE)
    @field:Pattern(
        regexp = Validator.PASSWORD_DIGIT_REGEX,
        message = Errors.PASSWORD_MISSING_DIGIT
    )
    @field:Pattern(
        regexp = Validator.PASSWORD_UPPERCASE_CHAR_REGEX,
        message = Errors.PASSWORD_MISSING_UPPERCASE
    )
    @field:Pattern(
        regexp = Validator.PASSWORD_LOWERCASE_CHAR_REGEX,
        message = Errors.PASSWORD_MISSING_LOWERCASE
    )
    @field:Pattern(
        regexp = Validator.PASSWORD_SPECIAL_CHAR_REGEX,
        message = Errors.PASSWORD_MISSING_SPECIAL_CHAR
    )
    @SerialName(value = "password")
    val password: String
)
