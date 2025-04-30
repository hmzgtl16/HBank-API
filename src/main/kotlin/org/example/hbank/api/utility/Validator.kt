package org.example.hbank.api.utility

import java.util.regex.Pattern

object Validator {

    private const val EMAIL_REGEX =
        "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
    private const val TOKEN_REGEX = "^[0-9]{6}$"
    private const val USERNAME_REGEX = "^[A-Za-z][A-Za-z0-9_.-]{5,29}$"
    private const val PASSWORD_DIGIT_REGEX = ".*[0-9].*"
    private const val PASSWORD_UPPERCASE_CHAR_REGEX = ".*[A-Z].*"
    private const val PASSWORD_LOWERCASE_CHAR_REGEX = ".*[a-z].*"
    private const val PASSWORD_SPECIAL_CHAR_REGEX = ".*[~!@#\$()\\-_'\",<.>/?].*"

    fun isValidEmail(email: String): Boolean =
        Pattern.compile(EMAIL_REGEX).matcher(email).matches()

    fun isValidToken(token: String?) = token != null
            && Pattern.compile(TOKEN_REGEX).matcher(token).matches()

    fun isValidUsername(username: String): Boolean =
        Pattern.compile(USERNAME_REGEX).matcher(username).matches()

    fun isValidPassword(password: String): Boolean =
        when {
            // At least 8 characters in length, but no more than 32
            password.length < 8 || password.length > 32 -> false
            // At least one digit [0-9]
            !Pattern.compile(PASSWORD_DIGIT_REGEX, Pattern.CASE_INSENSITIVE)
                .matcher(password).matches() -> false
            // At least one uppercase character [A-Z]
            !Pattern.compile(PASSWORD_UPPERCASE_CHAR_REGEX)
                .matcher(password).matches() -> false
            // At least one lowercase character [a-z]
            !Pattern.compile(PASSWORD_LOWERCASE_CHAR_REGEX)
                .matcher(password).matches() -> false
            // At least one special character [*.! @#$%^&(){}[]:;<>,.?/~_+-=|\]
            !Pattern.compile(PASSWORD_SPECIAL_CHAR_REGEX)
                .matcher(password).matches() -> false

            else -> true
        }

    fun isPasswordMatches(password1: String, password2: String) = password1 == password2

}
